package org.rsmod.api.net.rsprot

import net.rsprot.crypto.xtea.XteaKey
import net.rsprot.protocol.api.GameConnectionHandler
import net.rsprot.protocol.api.login.GameLoginResponseHandler
import net.rsprot.protocol.loginprot.incoming.util.AuthenticationType
import net.rsprot.protocol.loginprot.incoming.util.LoginBlock
import net.rsprot.protocol.loginprot.outgoing.LoginResponse
import net.rsprot.protocol.loginprot.outgoing.util.AuthenticatorResponse
import org.rsmod.api.net.rsprot.event.SessionEnd
import org.rsmod.api.net.rsprot.event.SessionStart
import org.rsmod.api.player.varMoveSpeed
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.map.CoordGrid

class ConnectionHandler(private val players: PlayerList, private val events: EventBus) :
    GameConnectionHandler<Player> {
    override fun onLogin(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<AuthenticationType<*>>,
    ) {
        val slot = players.nextFreeSlot() ?: error("No slots.")
        val response =
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
        checkNotNull(response)
        val player =
            Player().apply {
                slotId = slot
                coords = CoordGrid(0, 50, 50, 15, 19)
                username = response.loginBlock.username
                displayName = username
                varMoveSpeed = MoveSpeed.Run
            }
        // TODO: thread-safety for below
        players[slot] = player
        events.publish(SessionStart(player, response))
        response.setDisconnectionHook(Runnable { events.publish(SessionEnd(player, response)) })
    }

    override fun onReconnect(
        responseHandler: GameLoginResponseHandler<Player>,
        block: LoginBlock<XteaKey>,
    ) {
        println("TODO(onReconnect)")
        // TODO("Not yet implemented")
    }
}
