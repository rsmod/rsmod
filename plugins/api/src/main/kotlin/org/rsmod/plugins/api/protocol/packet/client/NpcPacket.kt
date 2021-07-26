package org.rsmod.plugins.api.protocol.packet.client

import org.rsmod.game.action.ActionBus
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.mob.NpcList
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.npc.type.NpcType
import org.rsmod.game.model.npc.type.NpcTypeList
import org.rsmod.game.model.vars.type.VarbitTypeList
import org.rsmod.plugins.api.protocol.packet.MoveType
import org.rsmod.plugins.api.protocol.packet.NpcAction
import org.rsmod.plugins.api.protocol.packet.NpcClick
import org.rsmod.plugins.api.util.extractBitValue
import javax.inject.Inject

private const val FORCE_RUN_TYPE = 1

data class OpNpc1(val index: Int, val mode: Int) : ClientPacket
data class OpNpc2(val index: Int, val mode: Int) : ClientPacket
data class OpNpc3(val index: Int, val mode: Int) : ClientPacket
data class OpNpc4(val index: Int, val mode: Int) : ClientPacket
data class OpNpc5(val index: Int, val mode: Int) : ClientPacket
data class OpNpc6(val id: Int) : ClientPacket

class OpNpc1Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val npcList: NpcList,
    private val types: NpcTypeList,
    private val varbits: VarbitTypeList
) : ClientPacketHandler<OpNpc1> {

    override fun handle(client: Client, player: Player, packet: OpNpc1) {
        val (index, mode) = packet
        if (index !in npcList.indices) return
        val npc = npcList[index] ?: return
        val type = npc.type.varType(player, types, varbits)
        val option = NpcAction.Option1(player, type, npc)
        val click = NpcClick(player, mode.moveType(), option, approach = false)
        actionBus.publish(click)
    }
}

class OpNpc2Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val npcList: NpcList,
    private val types: NpcTypeList,
    private val varbits: VarbitTypeList
) : ClientPacketHandler<OpNpc2> {

    override fun handle(client: Client, player: Player, packet: OpNpc2) {
        val (index, mode) = packet
        if (index !in npcList.indices) return
        val npc = npcList[index] ?: return
        val type = npc.type.varType(player, types, varbits)
        val option = NpcAction.Option2(player, type, npc)
        val click = NpcClick(player, mode.moveType(), option, approach = false)
        actionBus.publish(click)
    }
}

class OpNpc3Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val npcList: NpcList,
    private val types: NpcTypeList,
    private val varbits: VarbitTypeList
) : ClientPacketHandler<OpNpc3> {

    override fun handle(client: Client, player: Player, packet: OpNpc3) {
        val (index, mode) = packet
        if (index !in npcList.indices) return
        val npc = npcList[index] ?: return
        val type = npc.type.varType(player, types, varbits)
        val option = NpcAction.Option3(player, type, npc)
        val click = NpcClick(player, mode.moveType(), option, approach = false)
        actionBus.publish(click)
    }
}

class OpNpc4Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val npcList: NpcList,
    private val types: NpcTypeList,
    private val varbits: VarbitTypeList
) : ClientPacketHandler<OpNpc4> {

    override fun handle(client: Client, player: Player, packet: OpNpc4) {
        val (index, mode) = packet
        if (index !in npcList.indices) return
        val npc = npcList[index] ?: return
        val type = npc.type.varType(player, types, varbits)
        val option = NpcAction.Option4(player, type, npc)
        val click = NpcClick(player, mode.moveType(), option, approach = false)
        actionBus.publish(click)
    }
}

class OpNpc5Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val npcList: NpcList,
    private val types: NpcTypeList,
    private val varbits: VarbitTypeList
) : ClientPacketHandler<OpNpc5> {

    override fun handle(client: Client, player: Player, packet: OpNpc5) {
        val (index, mode) = packet
        if (index !in npcList.indices) return
        val npc = npcList[index] ?: return
        val type = npc.type.varType(player, types, varbits)
        val option = NpcAction.Option5(player, type, npc)
        val click = NpcClick(player, mode.moveType(), option, approach = false)
        actionBus.publish(click)
    }
}

class OpNpc6Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val types: NpcTypeList,
    private val varbits: VarbitTypeList
) : ClientPacketHandler<OpNpc6> {

    override fun handle(client: Client, player: Player, packet: OpNpc6) {
        val type = types[packet.id].varType(player, types, varbits)
        val click = NpcClick.ExamineAction(player, type)
        actionBus.publish(click)
    }
}

private fun NpcType.varType(player: Player, types: NpcTypeList, varbits: VarbitTypeList): NpcType {
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
        return types[transform]
    }
    return types[defaultTransform]
}

private fun Int.moveType(): MoveType = when (this) {
    FORCE_RUN_TYPE -> MoveType.ForceRun
    else -> MoveType.Neutral
}
