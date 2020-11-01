package org.rsmod.plugins.api.protocol.packet.client

import com.google.inject.Inject
import org.rsmod.game.action.ActionBus
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.protocol.packet.MapMove

data class MoveGameClick(
    val x: Int,
    val y: Int,
    val type: Int
) : ClientPacket

class GameClickHandler @Inject constructor(
    private val actions: ActionBus
) : ClientPacketHandler<MoveGameClick> {

    override fun handle(client: Client, player: Player, packet: MoveGameClick) {
        val destination = Coordinates(packet.x, packet.y)
        val action = MapMove(player, destination, player.speed)
        actions.publish(action)
    }
}
