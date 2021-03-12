package org.rsmod.plugins.api.protocol.packet.client

import javax.inject.Inject
import org.rsmod.game.action.ActionBus
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.protocol.packet.MapMove
import org.rsmod.plugins.api.protocol.packet.MoveType

private const val FORCE_RUN_TYPE = 1
private const val TELE_TYPE = 2

data class MoveGameClick(
    val x: Int,
    val y: Int,
    val type: Int
) : ClientPacket

data class MoveMinimapClick(
    val x: Int,
    val y: Int,
    val type: Int
) : ClientPacket

class GameClickHandler @Inject constructor(
    private val actions: ActionBus
) : ClientPacketHandler<MoveGameClick> {

    override fun handle(client: Client, player: Player, packet: MoveGameClick) {
        val (x, y, type) = packet
        val action = player.mapMove(x, y, type)
        actions.publish(action)
    }
}

class MinimapClickHandler @Inject constructor(
    private val actions: ActionBus
) : ClientPacketHandler<MoveMinimapClick> {

    override fun handle(client: Client, player: Player, packet: MoveMinimapClick) {
        val (x, y, type) = packet
        val action = player.mapMove(x, y, type)
        actions.publish(action)
    }
}

private fun Player.mapMove(x: Int, y: Int, type: Int): MapMove {
    val destination = Coordinates(x, y)
    val moveType = when (type) {
        FORCE_RUN_TYPE -> MoveType.ForceRun
        TELE_TYPE -> MoveType.Displace
        else -> MoveType.Neutral
    }
    return MapMove(this, destination, moveType)
}
