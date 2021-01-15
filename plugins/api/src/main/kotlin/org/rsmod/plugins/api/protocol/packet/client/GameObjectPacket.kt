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
import org.rsmod.game.model.obj.type.ObjectTypeList
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
    private val objTypes: ObjectTypeList,
    private val objectMap: GameObjectMap,
    private val objectApSet: GameObjectApSet
) : ClientPacketHandler<OperateObjectOne> {

    override fun handle(client: Client, player: Player, packet: OperateObjectOne) {
        val (id, x, y) = packet
        val coords = Coordinates(x, y, player.coords.level)
        val objects = objectMap[coords].filter { it.id == id }
        val obj = objects.firstOrNull()
        if (obj == null) {
            player.warn { "Operate object error (coords=$coords, obj=$obj)" }
            return
        }
        val type = objTypes[obj.id] // TODO replace with id based on player varp
        val approach = objectApSet.contains(type.id)
        val option = ObjectAction.Option1(player, type, coords)
        val action = ObjectClick(player, type, coords, option, approach)
        actionBus.publish(action)
    }
}
