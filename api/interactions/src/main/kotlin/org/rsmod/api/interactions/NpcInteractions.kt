package org.rsmod.api.interactions

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.NpcContentEvents
import org.rsmod.api.player.events.interact.NpcDefaultEvents
import org.rsmod.api.player.events.interact.NpcEvents
import org.rsmod.api.player.events.interact.NpcUnimplementedEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionNpc
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.vars.VariableIntMap
import org.rsmod.utils.bits.getBits

public class NpcInteractions
@Inject
constructor(
    private val npcTypes: NpcTypeList,
    private val varpTypes: VarpTypeList,
    private val varBitTypes: VarBitTypeList,
    private val eventBus: EventBus,
    private val protectedAccess: ProtectedAccessLauncher,
) {
    public fun triggerOp(player: Player, interaction: InteractionNpc) {
        val npc = interaction.target
        val op = opTrigger(player, npc, npc.type, interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun opTrigger(
        player: Player,
        npc: Npc,
        type: UnpackedNpcType,
        op: InteractionOp,
    ): OpEvent? {
        val multiNpcType = multiNpc(type, player.vars)
        if (multiNpcType != null) {
            val multiNpcTrigger = opTrigger(player, npc, multiNpcType, op)
            if (multiNpcTrigger != null) {
                return multiNpcTrigger
            }
        }
        val opEvent = npc.toOp(op)
        if (opEvent != null && eventBus.contains(opEvent::class.java, type.id)) {
            return opEvent
        }
        val contentEvent = npc.toContentOp(type.contentGroup, op)
        if (
            contentEvent != null && eventBus.contains(contentEvent::class.java, type.contentGroup)
        ) {
            return contentEvent
        }
        val unimplOpEvent = npc.toUnimplementedOp(op)
        if (unimplOpEvent != null && eventBus.contains(unimplOpEvent::class.java, type.id)) {
            return unimplOpEvent
        }
        val defaultEvent = npc.toDefaultOp(op)
        if (defaultEvent != null && eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }
        return null
    }

    public fun hasOpTrigger(player: Player, npc: Npc, op: InteractionOp): Boolean =
        opTrigger(player, npc, npc.type, op) != null

    public fun triggerAp(player: Player, interaction: InteractionNpc) {
        val npc = interaction.target
        val ap = apTrigger(player, npc, npc.type, interaction.op)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    public fun apTrigger(
        player: Player,
        npc: Npc,
        type: UnpackedNpcType,
        op: InteractionOp,
    ): ApEvent? {
        val multiNpcType = multiNpc(type, player.vars)
        if (multiNpcType != null) {
            val multiNpcType = npcTypes[multiNpcType]
            val multiNpcTrigger = apTrigger(player, npc, multiNpcType, op)
            if (multiNpcTrigger != null) {
                return multiNpcTrigger
            }
        }
        val apEvent = npc.toAp(op)
        if (apEvent != null && eventBus.contains(apEvent::class.java, type.id)) {
            return apEvent
        }
        val contentEvent = npc.toContentAp(type.contentGroup, op)
        if (
            contentEvent != null && eventBus.contains(contentEvent::class.java, type.contentGroup)
        ) {
            return contentEvent
        }
        val defaultEvent = npc.toDefaultAp(op)
        if (defaultEvent != null && eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }
        return null
    }

    public fun hasApTrigger(player: Player, npc: Npc, op: InteractionOp): Boolean =
        apTrigger(player, npc, npc.type, op) != null

    public fun multiNpc(type: UnpackedNpcType, vars: VariableIntMap): UnpackedNpcType? {
        if (type.multiNpc.isEmpty() && type.multiNpcDefault <= 0) {
            return null
        }
        val varValue = type.multiVarValue(vars) ?: 0
        val multiNpc =
            if (varValue in type.multiNpc.indices) {
                type.multiNpc[varValue].toInt()
            } else {
                type.multiNpcDefault
            }
        return npcTypes.getOrDefault(multiNpc and 0xFFFF, null)
    }

    private fun Npc.toOp(op: InteractionOp): NpcEvents.Op? =
        when (op) {
            InteractionOp.Op1 -> NpcEvents.Op1(this)
            InteractionOp.Op2 -> NpcEvents.Op2(this)
            InteractionOp.Op3 -> NpcEvents.Op3(this)
            InteractionOp.Op4 -> NpcEvents.Op4(this)
            InteractionOp.Op5 -> NpcEvents.Op5(this)
        }

    private fun Npc.toContentOp(contentGroup: Int, op: InteractionOp): NpcContentEvents.Op? =
        when (op) {
            InteractionOp.Op1 -> NpcContentEvents.Op1(this, contentGroup)
            InteractionOp.Op2 -> NpcContentEvents.Op2(this, contentGroup)
            InteractionOp.Op3 -> NpcContentEvents.Op3(this, contentGroup)
            InteractionOp.Op4 -> NpcContentEvents.Op4(this, contentGroup)
            InteractionOp.Op5 -> NpcContentEvents.Op5(this, contentGroup)
        }

    private fun Npc.toUnimplementedOp(op: InteractionOp): NpcUnimplementedEvents.Op? =
        when (op) {
            InteractionOp.Op1 -> NpcUnimplementedEvents.Op1(this)
            InteractionOp.Op2 -> NpcUnimplementedEvents.Op2(this)
            InteractionOp.Op3 -> NpcUnimplementedEvents.Op3(this)
            InteractionOp.Op4 -> NpcUnimplementedEvents.Op4(this)
            InteractionOp.Op5 -> NpcUnimplementedEvents.Op5(this)
        }

    private fun Npc.toDefaultOp(op: InteractionOp): NpcDefaultEvents.Op? =
        when (op) {
            InteractionOp.Op1 -> NpcDefaultEvents.Op1(this)
            InteractionOp.Op2 -> NpcDefaultEvents.Op2(this)
            InteractionOp.Op3 -> NpcDefaultEvents.Op3(this)
            InteractionOp.Op4 -> NpcDefaultEvents.Op4(this)
            InteractionOp.Op5 -> NpcDefaultEvents.Op5(this)
        }

    private fun Npc.toAp(op: InteractionOp): NpcEvents.Ap? =
        when (op) {
            InteractionOp.Op1 -> NpcEvents.Ap1(this)
            InteractionOp.Op2 -> NpcEvents.Ap2(this)
            InteractionOp.Op3 -> NpcEvents.Ap3(this)
            InteractionOp.Op4 -> NpcEvents.Ap4(this)
            InteractionOp.Op5 -> NpcEvents.Ap5(this)
        }

    private fun Npc.toContentAp(contentGroup: Int, op: InteractionOp): NpcContentEvents.Ap? =
        when (op) {
            InteractionOp.Op1 -> NpcContentEvents.Ap1(this, contentGroup)
            InteractionOp.Op2 -> NpcContentEvents.Ap2(this, contentGroup)
            InteractionOp.Op3 -> NpcContentEvents.Ap3(this, contentGroup)
            InteractionOp.Op4 -> NpcContentEvents.Ap4(this, contentGroup)
            InteractionOp.Op5 -> NpcContentEvents.Ap5(this, contentGroup)
        }

    private fun Npc.toDefaultAp(op: InteractionOp): NpcDefaultEvents.Ap? =
        when (op) {
            InteractionOp.Op1 -> NpcDefaultEvents.Ap1(this)
            InteractionOp.Op2 -> NpcDefaultEvents.Ap2(this)
            InteractionOp.Op3 -> NpcDefaultEvents.Ap3(this)
            InteractionOp.Op4 -> NpcDefaultEvents.Ap4(this)
            InteractionOp.Op5 -> NpcDefaultEvents.Ap5(this)
        }

    private fun UnpackedNpcType.multiVarValue(vars: VariableIntMap): Int? {
        if (multiVarp > 0) {
            val varp = varpTypes[multiVarp] ?: return null
            return vars[varp]
        } else if (multiVarBit > 0) {
            val varBit = varBitTypes[multiVarBit] ?: return null
            val packed = vars[varBit.baseVar] ?: return null
            return packed.getBits(varBit.bits)
        }
        return null
    }
}
