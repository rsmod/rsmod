package org.rsmod.api.net.rsprot

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.crypto.xtea.XteaKey
import net.rsprot.protocol.api.GameConnectionHandler
import net.rsprot.protocol.api.login.GameLoginResponseHandler
import net.rsprot.protocol.loginprot.incoming.util.AuthenticationType
import net.rsprot.protocol.loginprot.incoming.util.LoginBlock
import net.rsprot.protocol.loginprot.incoming.util.OtpAuthenticationType
import net.rsprot.protocol.loginprot.outgoing.LoginResponse
import org.rsmod.api.account.AccountManager
import org.rsmod.api.account.loader.request.AccountLoadAuth
import org.rsmod.api.config.refs.modlevels
import org.rsmod.api.net.rsprot.player.AccountLoadResponseHook
import org.rsmod.api.net.rsprot.provider.Js5Store
import org.rsmod.api.pw.hash.PasswordHashing
import org.rsmod.api.realm.Realm
import org.rsmod.api.registry.account.AccountRegistry
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.server.config.ServerConfig
import org.rsmod.api.totp.Totp
import org.rsmod.api.totp.useSecret
import org.rsmod.events.EventBus
import org.rsmod.game.GameUpdate
import org.rsmod.game.entity.Player
import org.rsmod.game.type.mod.ModLevelTypeList

class ConnectionHandler
@Inject
private constructor(
    private val realm: Realm,
    private val config: ServerConfig,
    private val update: GameUpdate,
    private val eventBus: EventBus,
    private val playerReg: PlayerRegistry,
    private val accountReg: AccountRegistry,
    private val accountManager: AccountManager,
    private val passwordHashing: PasswordHashing,
    private val totp: Totp,
    modLevelTypes: ModLevelTypeList,
    js5: Js5Store,
) : GameConnectionHandler<Player> {
    private val logger = InlineLogger()
    private val js5Crc = js5.crc

    private val devModeModLevel by lazy { modLevelTypes[modlevels.owner] }

    private val world: Int
        get() = config.world

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

        if (!block.crc.validate(js5Crc)) {
            responseHandler.writeFailedResponse(LoginResponse.OutOfDateReload)
            return
        }

        when (val auth = block.authentication) {
            is AuthenticationType.PasswordAuthentication -> passLogin(responseHandler, block, auth)
            is AuthenticationType.TokenAuthentication -> tokenLogin(responseHandler, block, auth)
        }
    }

    private fun passLogin(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<AuthenticationType>,
        auth: AuthenticationType.PasswordAuthentication,
    ) {
        val password = auth.password.asCharArray()
        try {
            passLogin(responseHandler, block, auth, password)
        } finally {
            // `password` char array is already cleared during `computePasswordHash`, but that is
            // an implementation detail in the password hashing interface; we ensure to clear it
            // after usage regardless.
            password.fill('\u0000')
            auth.password.clear()
        }
    }

    private fun passLogin(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<AuthenticationType>,
        auth: AuthenticationType.PasswordAuthentication,
        password: CharArray,
    ) {
        // This may be filtered earlier at the protocol layer (e.g., rsprot), but we defensively
        // check again to ensure the password is not empty.
        if (password.isEmpty()) {
            responseHandler.writeFailedResponse(LoginResponse.InvalidUsernameOrPassword)
            return
        }
        // Capture a local snapshot, as `realm.config` is mutable and may change.
        val realmConfig = realm.config
        val responseHook =
            AccountLoadResponseHook(
                world = world,
                config = realmConfig,
                update = update,
                eventBus = eventBus,
                accountRegistry = accountReg,
                playerRegistry = playerReg,
                devModeModLevel = devModeModLevel,
                loginBlock = block,
                channelResponses = responseHandler,
                inputPassword = password.copyOf(),
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
            if (realmConfig.requireRegistration) {
                accountManager.load(loadAuth, username, responseHook)
            } else {
                val hashedPassword = computePasswordHash(password)
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

    private fun computePasswordHash(password: CharArray): String? {
        return try {
            passwordHashing.hash(password)
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
            useSecret(secret) { totp.verifyCode(it, code) }
        } catch (e: Exception) {
            logger.error { "Totp verification error: ${e::class.simpleName}" }
            false
        }
    }

    // TODO: Token authentication handling.
    private fun tokenLogin(
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
