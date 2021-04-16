package org.rsmod.plugins.api.protocol.packet.client

import org.rsmod.game.action.ActionBus
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.obj.GameObject
import org.rsmod.game.model.obj.GameObjectApSet
import org.rsmod.game.model.obj.GameObjectMap
import org.rsmod.game.model.obj.type.ObjectType
import org.rsmod.game.model.obj.type.ObjectTypeList
import org.rsmod.game.model.vars.type.VarbitTypeList
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.protocol.packet.MoveType
import org.rsmod.plugins.api.protocol.packet.ObjectAction
import org.rsmod.plugins.api.protocol.packet.ObjectClick
import org.rsmod.plugins.api.util.extractBitValue
import javax.inject.Inject

private const val FORCE_RUN_TYPE = 1

data class OpLoc1(val id: Int, val x: Int, val y: Int, val mode: Int) : ClientPacket
data class OpLoc2(val id: Int, val x: Int, val y: Int, val mode: Int) : ClientPacket
data class OpLoc3(val id: Int, val x: Int, val y: Int, val mode: Int) : ClientPacket
data class OpLoc4(val id: Int, val x: Int, val y: Int, val mode: Int) : ClientPacket
data class OpLoc5(val id: Int, val x: Int, val y: Int, val mode: Int) : ClientPacket
data class OpLoc6(val id: Int) : ClientPacket

class OpLoc1Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val objMap: GameObjectMap,
    private val objApSet: GameObjectApSet,
    private val objTypes: ObjectTypeList,
    private val varbitTypes: VarbitTypeList
) : ClientPacketHandler<OpLoc1> {

    override fun handle(client: Client, player: Player, packet: OpLoc1) {
        val coords = Coordinates(packet.x, packet.y, player.coords.level)
        val obj = objMap.find(player, coords, packet.id) ?: return
        val type = obj.type.varType(player, objTypes, varbitTypes)
        val approach = objApSet.contains(type.id)
        val option = ObjectAction.Option1(player, type, obj.shape, obj.rotation, coords)
        val click = ObjectClick(player, packet.mode.moveType(), option, approach)
        actionBus.publish(click)
    }
}

class OpLoc2Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val objMap: GameObjectMap,
    private val objApSet: GameObjectApSet,
    private val objTypes: ObjectTypeList,
    private val varbitTypes: VarbitTypeList
) : ClientPacketHandler<OpLoc2> {

    override fun handle(client: Client, player: Player, packet: OpLoc2) {
        val coords = Coordinates(packet.x, packet.y, player.coords.level)
        val obj = objMap.find(player, coords, packet.id) ?: return
        val type = obj.type.varType(player, objTypes, varbitTypes)
        val approach = objApSet.contains(type.id)
        val option = ObjectAction.Option2(player, type, obj.shape, obj.rotation, coords)
        val click = ObjectClick(player, packet.mode.moveType(), option, approach)
        actionBus.publish(click)
    }
}

class OpLoc3Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val objMap: GameObjectMap,
    private val objApSet: GameObjectApSet,
    private val objTypes: ObjectTypeList,
    private val varbitTypes: VarbitTypeList
) : ClientPacketHandler<OpLoc3> {

    override fun handle(client: Client, player: Player, packet: OpLoc3) {
        val coords = Coordinates(packet.x, packet.y, player.coords.level)
        val obj = objMap.find(player, coords, packet.id) ?: return
        val type = obj.type.varType(player, objTypes, varbitTypes)
        val approach = objApSet.contains(type.id)
        val option = ObjectAction.Option3(player, type, obj.shape, obj.rotation, coords)
        val click = ObjectClick(player, packet.mode.moveType(), option, approach)
        actionBus.publish(click)
    }
}

class OpLoc4Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val objMap: GameObjectMap,
    private val objApSet: GameObjectApSet,
    private val objTypes: ObjectTypeList,
    private val varbitTypes: VarbitTypeList
) : ClientPacketHandler<OpLoc4> {

    override fun handle(client: Client, player: Player, packet: OpLoc4) {
        val coords = Coordinates(packet.x, packet.y, player.coords.level)
        val obj = objMap.find(player, coords, packet.id) ?: return
        val type = obj.type.varType(player, objTypes, varbitTypes)
        val approach = objApSet.contains(type.id)
        val option = ObjectAction.Option4(player, type, obj.shape, obj.rotation, coords)
        val click = ObjectClick(player, packet.mode.moveType(), option, approach)
        actionBus.publish(click)
    }
}

class OpLoc5Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val objMap: GameObjectMap,
    private val objApSet: GameObjectApSet,
    private val objTypes: ObjectTypeList,
    private val varbitTypes: VarbitTypeList
) : ClientPacketHandler<OpLoc5> {

    override fun handle(client: Client, player: Player, packet: OpLoc5) {
        val coords = Coordinates(packet.x, packet.y, player.coords.level)
        val obj = objMap.find(player, coords, packet.id) ?: return
        val type = obj.type.varType(player, objTypes, varbitTypes)
        val approach = objApSet.contains(type.id)
        val option = ObjectAction.Option5(player, type, obj.shape, obj.rotation, coords)
        val click = ObjectClick(player, packet.mode.moveType(), option, approach)
        actionBus.publish(click)
    }
}

class OpLoc6Handler @Inject constructor() : ClientPacketHandler<OpLoc6> {

    override fun handle(client: Client, player: Player, packet: OpLoc6) {
        // TODO: examine object
        player.sendMessage("Nothing interesting happens.")
    }
}

private fun GameObjectMap.find(player: Player, coords: Coordinates, id: Int): GameObject? {
    val objects = this[coords].filter { it.id == id }
    val obj = objects.firstOrNull()
    if (obj == null) {
        player.warn { "Operate object that does not exist (id=$id, coords=$coords)" }
        return null
    }
    return obj
}

private fun ObjectType.varType(player: Player, objs: ObjectTypeList, varbits: VarbitTypeList): ObjectType {
    if (transforms.isEmpty()) return this
    val transformIndex = when {
        varp > 0 -> player.varpMap[varp] ?: 0
        varbit > 0 -> {
            val type = varbits[varbit]
            (player.varpMap[type.varp] ?: 0).extractBitValue(type.lsb, type.msb)
        }
        else -> -1
    }
    if (transformIndex in transforms.indices) {
        val transform = transforms[transformIndex]
        return objs[transform]
    }
    return objs[defaultTransform]
}

private fun Int.moveType(): MoveType = when (this) {
    FORCE_RUN_TYPE -> MoveType.ForceRun
    else -> MoveType.Neutral
}
