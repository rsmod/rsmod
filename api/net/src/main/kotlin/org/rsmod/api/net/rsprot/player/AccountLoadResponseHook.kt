package org.rsmod.api.net.rsprot.player

import com.github.michaelbull.logging.InlineLogger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import net.rsprot.protocol.api.login.GameLoginResponseHandler
import net.rsprot.protocol.loginprot.incoming.util.AuthenticationType
import net.rsprot.protocol.loginprot.incoming.util.LoginBlock
import net.rsprot.protocol.loginprot.outgoing.LoginResponse
import net.rsprot.protocol.loginprot.outgoing.util.AuthenticatorResponse
import org.rsmod.api.account.character.main.CharacterAccountData
import org.rsmod.api.account.loader.request.AccountLoadAuth
import org.rsmod.api.account.loader.request.AccountLoadCallback
import org.rsmod.api.account.loader.request.AccountLoadResponse
import org.rsmod.api.config.refs.modgroups
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.registry.account.AccountRegistry
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.registry.player.isSuccess
import org.rsmod.api.server.config.WorldConfig
import org.rsmod.api.server.config.isDevRealm
import org.rsmod.events.EventBus
import org.rsmod.game.GameUpdate
import org.rsmod.game.GameUpdate.Companion.isCountdown
import org.rsmod.game.GameUpdate.Companion.isUpdating
import org.rsmod.game.entity.Player
import org.rsmod.game.type.mod.ModGroup

class AccountLoadResponseHook(
    private val config: WorldConfig,
    private val update: GameUpdate,
    private val eventBus: EventBus,
    private val accountRegistry: AccountRegistry,
    private val playerRegistry: PlayerRegistry,
    private val loginBlock: LoginBlock<AuthenticationType>,
    private val channelResponses: GameLoginResponseHandler<Player>,
    private val inputPassword: CharArray,
    private val verifyPassword: (String, CharArray) -> Boolean,
    private val verifyTotp: (CharArray, String) -> Boolean,
) : AccountLoadCallback {
    override fun invoke(response: AccountLoadResponse) {
        handleLoadResponse(response)
    }

    private fun handleLoadResponse(response: AccountLoadResponse) =
        when (response) {
            is AccountLoadResponse.Ok.NewAccount -> {
                safeQueueLogin(response)
            }
            is AccountLoadResponse.Ok.LoadAccount -> {
                validateAndQueueLogin(response)
            }
            is AccountLoadResponse.Err.AccountNotFound -> {
                writeErrorResponse(LoginResponse.InvalidUsernameOrPassword)
            }
            is AccountLoadResponse.Err.Timeout -> {
                writeErrorResponse(LoginResponse.Timeout)
            }
            is AccountLoadResponse.Err.InternalServiceError -> {
                writeErrorResponse(LoginResponse.LoginServerLoadError)
            }
            is AccountLoadResponse.Err.ShutdownInProgress -> {
                writeErrorResponse(LoginResponse.UpdateInProgress)
            }
            is AccountLoadResponse.Err.Exception -> {
                writeErrorResponse(LoginResponse.UnknownReplyFromLoginServer)
            }
        }

    private fun validateAndQueueLogin(response: AccountLoadResponse.Ok.LoadAccount) {
        // Note: We could move this branch to `handleLoadResponse`, but we intentionally keep it
        // here to mirror production login flow.
        val ignoreAuthentication = config.ignorePasswords && config.isDevRealm
        if (ignoreAuthentication) {
            logger.debug { "Bypass password and two-factor authentication: enabled" }
        }

        val verifyTwoFactor = response.account.twofaEnabled
        if (verifyTwoFactor) {
            val secret = response.account.twofaSecret
            if (secret == null) {
                writeErrorResponse(LoginResponse.InvalidAuthenticatorCode)
                logger.error { "Two-factor enabled without a stored secret: ${response.account}" }
                return
            }

            val authResponse =
                validateTwoFactor(response.account, response.auth, secret.toCharArray())
            if (!ignoreAuthentication && authResponse != null) {
                writeErrorResponse(authResponse)
                return
            }
        }

        val storedHashedPassword = response.account.hashedPassword
        val passwordVerified = verifyPassword(storedHashedPassword, inputPassword)
        if (!ignoreAuthentication && !passwordVerified) {
            writeErrorResponse(LoginResponse.InvalidUsernameOrPassword)
            return
        }

        safeQueueLogin(response)
    }

    private fun validateTwoFactor(
        account: CharacterAccountData,
        auth: AccountLoadAuth,
        secret: CharArray,
    ): LoginResponse? =
        when (auth) {
            is AccountLoadAuth.InitialRequest -> {
                val requiresAuth = requiresTwoFactorAuth(account, auth)
                if (requiresAuth) {
                    LoginResponse.Authenticator
                } else {
                    null
                }
            }
            is AccountLoadAuth.CodeInput -> {
                val correctCode = verifyTotp(secret, auth.otp.toString())
                if (!correctCode) {
                    LoginResponse.InvalidAuthenticatorCode
                } else {
                    null
                }
            }
        }

    private fun requiresTwoFactorAuth(
        account: CharacterAccountData,
        auth: AccountLoadAuth.InitialRequest,
    ): Boolean {
        val requiresImmediateCode =
            when (auth) {
                is AccountLoadAuth.TrustedDevice -> account.knownDevice != auth.identifier
                AccountLoadAuth.UnknownDevice -> true
            }

        if (requiresImmediateCode) {
            return true
        }

        val lastVerified = account.twofaLastVerified ?: return true
        val daysSince = ChronoUnit.DAYS.between(lastVerified, LocalDateTime.now())
        return daysSince >= DAYS_BETWEEN_2FA_VERIFICATION
    }

    private fun safeQueueLogin(response: AccountLoadResponse.Ok) {
        try {
            val player = createPlayer(response).applyConfigTransforms()
            accountRegistry.queueLogin(player, response, ::safeHandleGameLogin)
        } catch (e: Exception) {
            writeErrorResponse(LoginResponse.ConnectFail)
            logger.error(e) { "Could not queue login for account: ${response.account}" }
        }
    }

    private fun createPlayer(fromResponse: AccountLoadResponse.Ok): Player {
        val player = Player()
        for (transform in fromResponse.transforms) {
            transform.apply(player)
        }
        val newAccount = fromResponse is AccountLoadResponse.Ok.NewAccount
        player.newAccount = newAccount
        return player
    }

    private fun Player.applyConfigTransforms(): Player {
        if (config.isDevRealm && newAccount) {
            modGroup = modgroups.owner
        }
        if (config.autoAssignDisplayName && newAccount) {
            displayName = username.toDisplayName()
        }
        return this
    }

    // Since logins are processed on the game thread, we isolate player-specific failures to prevent
    // them from affecting the server. Exceptions are caught, logged, and a generic failure response
    // is sent to the player's channel.
    private fun safeHandleGameLogin(player: Player, loadResponse: AccountLoadResponse.Ok) {
        try {
            handleGameLogin(player, loadResponse)
        } catch (e: Exception) {
            writeErrorResponse(LoginResponse.ConnectFail)
            logger.error(e) { "Error handling login for player: $player" }
        }
    }

    private fun handleGameLogin(player: Player, loadResponse: AccountLoadResponse.Ok) {
        val isOnline = player.isOnline(loadResponse.account.worldId)
        if (isOnline) {
            writeErrorResponse(LoginResponse.Duplicate)
            return
        }

        val slotId = playerRegistry.nextFreeSlot()
        if (slotId == null) {
            writeErrorResponse(LoginResponse.ServerFull)
            return
        }

        val updateState = update.state
        if (updateState.isUpdating()) {
            writeErrorResponse(LoginResponse.UpdateInProgress)
            return
        }

        if (updateState.isCountdown() && updateState.current <= UPDATE_TIMER_REJECT_BUFFER) {
            writeErrorResponse(LoginResponse.UpdateInProgress)
            return
        }

        val response = player.createLoginResponse(slotId, loadResponse.auth)
        val session = channelResponses.writeSuccessfulResponse(response, loginBlock)

        val disconnectionHook = Runnable { player.clientDisconnected.set(true) }
        session.setDisconnectionHook(disconnectionHook)

        // `setDisconnectionHook` will invoke the disconnection hook instantly if the session
        // is not active at this point. Since the channel is no longer connected, we can no-op
        // and return early.
        if (player.clientDisconnected.get()) {
            return
        }

        player.slotId = slotId
        eventBus.publish(SessionStart(player, session))
        val register = playerRegistry.add(player)
        if (!register.isSuccess()) {
            logger.warn { "Failed to register player: $register (player=$player)" }
            session.requestClose()
        }
    }

    private fun Player.isOnline(lastKnownWorld: Int?): Boolean {
        val loggedInAnotherWorld = lastKnownWorld != null && lastKnownWorld != config.worldId
        if (loggedInAnotherWorld) {
            return true
        }
        return playerRegistry.isOnline(userId)
    }

    private fun Player.createLoginResponse(slotId: Int, auth: AccountLoadAuth) =
        LoginResponse.Ok(
            authenticatorResponse = authenticatorResponse(auth),
            staffModLevel = modGroup?.toStaffModLevel() ?: 0,
            playerMod = modGroup?.isClientMod == true,
            index = slotId,
            member = members,
            accountHash = accountHash,
            userId = userId,
            userHash = userHash,
        )

    private fun Player.authenticatorResponse(auth: AccountLoadAuth): AuthenticatorResponse =
        when (auth) {
            is AccountLoadAuth.AuthCodeInputTrusted,
            is AccountLoadAuth.AuthCodeInputUntrusted,
            is AccountLoadAuth.TrustedDevice -> {
                val knownDevice = lastKnownDevice ?: randomInt()
                lastKnownDevice = knownDevice
                AuthenticatorResponse.AuthenticatorCode(knownDevice)
            }
            AccountLoadAuth.UnknownDevice -> AuthenticatorResponse.NoAuthenticator
        }

    private fun writeErrorResponse(response: LoginResponse) {
        channelResponses.writeFailedResponse(response)
    }

    private companion object {
        private const val DAYS_BETWEEN_2FA_VERIFICATION = 30

        /** Once the game update timer hits this value, we reject any further login requests. */
        private const val UPDATE_TIMER_REJECT_BUFFER = 25

        private val logger = InlineLogger()

        private var Player.newAccount by boolVarBit(varbits.new_player_account)

        private fun ModGroup.toStaffModLevel(): Int =
            when {
                isClientAdmin -> 2
                isClientMod -> 1
                else -> 0
            }

        // TODO: Decide how to deal with email login usernames.
        private fun String.toDisplayName(): String {
            return trim().split(Regex(" +")).joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }

        @Suppress("konsist.avoid usage of stdlib Random in functions")
        private fun randomInt(): Int = java.util.concurrent.ThreadLocalRandom.current().nextInt()
    }
}
