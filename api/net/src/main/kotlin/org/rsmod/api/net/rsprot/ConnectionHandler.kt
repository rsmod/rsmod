package org.rsmod.api.net.rsprot

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.crypto.xtea.XteaKey
import net.rsprot.protocol.api.GameConnectionHandler
import net.rsprot.protocol.api.login.GameLoginResponseHandler
import net.rsprot.protocol.loginprot.incoming.util.AuthenticationType
import net.rsprot.protocol.loginprot.incoming.util.LoginBlock
import net.rsprot.protocol.loginprot.outgoing.LoginResponse
import net.rsprot.protocol.loginprot.outgoing.util.AuthenticatorResponse
import org.rsmod.api.config.refs.BaseModGroups
import org.rsmod.api.net.rsprot.player.SessionEnd
import org.rsmod.api.net.rsprot.player.SessionStart
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.registry.player.isSuccess
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.map.CoordGrid

class ConnectionHandler
@Inject
constructor(private val registry: PlayerRegistry, private val events: EventBus) :
    GameConnectionHandler<Player> {
    private val logger = InlineLogger()

    override fun onLogin(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<AuthenticationType<*>>,
    ) {
        val slot = registry.nextFreeSlot()
        if (slot == null) {
            responseHandler.writeFailedResponse(LoginResponse.ServerFull)
            return
        }

        val player =
            Player().apply {
                slotId = slot
                coords = CoordGrid(0, 50, 50, 35, 20)
                username = block.username
                displayName = username
                varMoveSpeed = MoveSpeed.Run
                modGroup = BaseModGroups.owner
                uuid = username.hashCode().toLong()
                observerUUID = uuid
            }

        val session =
            responseHandler.writeSuccessfulResponse(
                loginBlock = block,
                response =
                    LoginResponse.Ok(
                        authenticatorResponse = AuthenticatorResponse.NoAuthenticator,
                        staffModLevel = 2,
                        playerMod = true,
                        index = slot,
                        member = true,
                        accountHash = 0,
                        userId = 0,
                        userHash = 0,
                    ),
            )
        checkNotNull(session)

        events.publish(SessionStart(player, session))

        val add = registry.add(player)
        if (!add.isSuccess()) {
            events.publish(SessionEnd(player, session))
            logger.warn { "Could not register player. (err=$add, player=$player)" }
            session.requestClose()
            return
        }

        val disconnectionHook = Runnable {
            registry.del(player)
            events.publish(SessionEnd(player, session))
        }
        session.setDisconnectionHook(disconnectionHook)
    }

    override fun onReconnect(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<XteaKey>,
    ) {
        println("TODO(onReconnect)")
        // TODO("Not yet implemented")
    }
}
