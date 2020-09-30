package gg.rsmod.plugins.core.protocol.packet.client

import com.google.inject.Inject
import gg.rsmod.game.action.ActionBus
import gg.rsmod.game.message.ClientPacket
import gg.rsmod.game.message.ClientPacketHandler
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.mob.Player
import gg.rsmod.plugins.core.protocol.packet.MapMove

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
        val action = MapMove(player, destination)
        actions.publish(action)
    }
}
