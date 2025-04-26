package org.rsmod.api.net.rsprot

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.crypto.xtea.XteaKey
import net.rsprot.protocol.api.GameConnectionHandler
import net.rsprot.protocol.api.login.GameLoginResponseHandler
import net.rsprot.protocol.loginprot.incoming.util.AuthenticationType
import net.rsprot.protocol.loginprot.incoming.util.LoginBlock
import net.rsprot.protocol.loginprot.incoming.util.OtpAuthenticationType
import net.rsprot.protocol.loginprot.incoming.util.Password
import net.rsprot.protocol.loginprot.outgoing.LoginResponse
import org.rsmod.api.account.AccountManager
import org.rsmod.api.account.loader.request.AccountLoadAuth
import org.rsmod.api.net.rsprot.player.AccountLoadResponseHook
import org.rsmod.api.pw.hash.PasswordHashing
import org.rsmod.api.registry.account.AccountRegistry
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.server.config.WorldConfig
import org.rsmod.api.totp.TotpManager
import org.rsmod.api.totp.useSecret
import org.rsmod.events.EventBus
import org.rsmod.game.GameUpdate
import org.rsmod.game.entity.Player

class ConnectionHandler
@Inject
private constructor(
    private val worldConfig: WorldConfig,
    private val update: GameUpdate,
    private val eventBus: EventBus,
    private val playerReg: PlayerRegistry,
    private val accountReg: AccountRegistry,
    private val accountManager: AccountManager,
    private val passwordHashing: PasswordHashing,
    private val totpManager: TotpManager,
) : GameConnectionHandler<Player> {
    private val logger = InlineLogger()

    override fun onLogin(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<AuthenticationType>,
    ) {
        if (accountManager.isLoaderShuttingDown()) {
            responseHandler.writeFailedResponse(LoginResponse.LoginServerOffline)
            return
        }

        if (accountManager.isLoaderRejectingRequests()) {
            responseHandler.writeFailedResponse(LoginResponse.LoginServerNoReply)
            return
        }

        when (val auth = block.authentication) {
            is AuthenticationType.PasswordAuthentication -> authLogin(responseHandler, block, auth)
            is AuthenticationType.TokenAuthentication -> authLogin(responseHandler, block, auth)
        }
    }

    private fun authLogin(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<AuthenticationType>,
        auth: AuthenticationType.PasswordAuthentication,
    ) {
        // This may be filtered earlier at the protocol layer (e.g., rsprot), but we defensively
        // check again to ensure the password is not empty.
        val inputPassword = auth.password.asString().toCharArray()
        if (inputPassword.isEmpty()) {
            responseHandler.writeFailedResponse(LoginResponse.InvalidUsernameOrPassword)
            return
        }
        val responseHook =
            AccountLoadResponseHook(
                config = worldConfig,
                update = update,
                eventBus = eventBus,
                accountRegistry = accountReg,
                playerRegistry = playerReg,
                loginBlock = block,
                channelResponses = responseHandler,
                inputPassword = inputPassword,
                verifyPassword = ::verifyPassword,
                verifyTotp = ::verifyTotp,
            )
        val loadAuth = auth.otpAuthentication.toAccountLoadAuth()
        val username = block.username

        // Important: Password hashing can potentially saturate cpu and starve threads. There are
        // two solutions to mitigate this risk:
        // 1) Enable `requireRegistration` - this delegates account creation (and thus password
        //  hashing) to an external system. The server will only handle authentication for
        //  pre-registered accounts.
        // 2) Configure the `LoginHandlers` used by rsprot, particularly the `loginFlowExecutor`.
        //  Use a thread pool with `(cores - 1) * 2` threads to prevent hashing from monopolizing
        //  cpu cores.
        val requestSubmitted =
            if (worldConfig.requireRegistration) {
                accountManager.load(loadAuth, username, responseHook)
            } else {
                val hashedPassword = computePasswordHash(auth.password)
                if (hashedPassword == null) {
                    responseHandler.writeFailedResponse(LoginResponse.InvalidUsernameOrPassword)
                    return
                }
                accountManager.loadOrCreate(loadAuth, username, { hashedPassword }, responseHook)
            }

        if (!requestSubmitted) {
            responseHandler.writeFailedResponse(LoginResponse.LoginServerLoadError)
        }
    }

    private fun computePasswordHash(pass: Password): String? {
        return try {
            passwordHashing.hash(pass.asString().toCharArray())
        } catch (e: Exception) {
            logger.error { "Password hashing error: ${e::class.simpleName}" }
            null
        }
    }

    private fun verifyPassword(hash: String, password: CharArray): Boolean {
        return try {
            passwordHashing.verify(hash, password)
        } catch (e: Exception) {
            logger.error { "Password verification error: ${e::class.simpleName}" }
            false
        }
    }

    private fun verifyTotp(secret: CharArray, code: String): Boolean {
        return try {
            useSecret(secret) { totpManager.verifyCode(it, code) }
        } catch (e: Exception) {
            logger.error { "Totp verification error: ${e::class.simpleName}" }
            false
        }
    }

    // TODO: Token authentication handling.
    private fun authLogin(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<AuthenticationType>,
        @Suppress("unused") auth: AuthenticationType.TokenAuthentication,
    ) {
        logger.warn { "Unhandled login authentication for: $block" }
        responseHandler.writeFailedResponse(LoginResponse.InvalidLoginPacket)
    }

    private fun OtpAuthenticationType.toAccountLoadAuth(): AccountLoadAuth =
        when (this) {
            is OtpAuthenticationType.NoMultiFactorAuthentication -> {
                AccountLoadAuth.UnknownDevice
            }
            is OtpAuthenticationType.TrustedAuthenticator -> {
                AccountLoadAuth.AuthCodeInputTrusted(otp)
            }
            is OtpAuthenticationType.UntrustedAuthentication -> {
                AccountLoadAuth.AuthCodeInputUntrusted(otp)
            }
            is OtpAuthenticationType.TrustedComputer -> {
                AccountLoadAuth.TrustedDevice(identifier)
            }
        }

    override fun onReconnect(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<XteaKey>,
    ) {
        // TODO: Reconnection.
        responseHandler.writeFailedResponse(LoginResponse.ConnectFail)
    }
}
