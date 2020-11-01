package org.rsmod.plugins.api.protocol.packet.client

import com.google.inject.Inject
import org.rsmod.game.action.Action
import org.rsmod.game.action.ActionBus
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.protocol.packet.client.OperateObjectAction.Companion.OPERATE_ONE_OPTION

interface OperateObjectAction : Action {
    val player: Player
    val objectId: Int
    val coords: Coordinates
    val option: Int

    companion object {
        const val OPERATE_ONE_OPTION = 1
    }
}

data class OperateObjectOne(
    val id: Int,
    val x: Int,
    val y: Int,
    val mode: Int
) : ClientPacket

class OperateObjectOneAction(
    override val player: Player,
    override val objectId: Int,
    override val coords: Coordinates,
    override val option: Int = OPERATE_ONE_OPTION
) : OperateObjectAction

class OperateObjectOneHandler @Inject constructor(
    private val actionBus: ActionBus
) : ClientPacketHandler<OperateObjectOne> {

    override fun handle(client: Client, player: Player, packet: OperateObjectOne) {
        val (id, x, y) = packet
        val action = OperateObjectOneAction(player, id, Coordinates(x, y, player.coords.plane))
        actionBus.publish(action, id)
    }
}
