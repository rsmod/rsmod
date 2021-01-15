package org.rsmod.plugins.api.protocol.packet.client

import com.google.inject.Inject
import org.rsmod.game.action.ActionBus
import org.rsmod.game.model.obj.GameObjectMap
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.obj.GameObjectApSet
import org.rsmod.plugins.api.protocol.packet.ObjectAction
import org.rsmod.plugins.api.protocol.packet.ObjectClick

data class OperateObjectOne(
    val id: Int,
    val x: Int,
    val y: Int,
    val mode: Int
) : ClientPacket

class OperateObjectOneHandler @Inject constructor(
    private val actionBus: ActionBus,
    private val objectMap: GameObjectMap,
    private val objectApSet: GameObjectApSet
) : ClientPacketHandler<OperateObjectOne> {

    override fun handle(client: Client, player: Player, packet: OperateObjectOne) {
        val (id, x, y) = packet
        val coords = Coordinates(x, y, player.coords.plane)
        val objects = objectMap[coords].filter { it.id == id }
        val obj = objects.firstOrNull()
        if (obj == null) {
            player.warn { "Operate object error: ObjectNotFound(coords=$coords, obj=$obj)" }
            return
        }
        val approach = objectApSet.contains(obj.id)
        val operate = ObjectAction.Operate1(player, obj)
        val action = ObjectClick(player, obj, operate, approach)
        actionBus.publish(action)
    }
}
