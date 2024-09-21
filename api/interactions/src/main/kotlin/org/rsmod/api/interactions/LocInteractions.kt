package org.rsmod.api.interactions

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.LocContentEvents
import org.rsmod.api.player.events.interact.LocDefaultEvents
import org.rsmod.api.player.events.interact.LocEvents
import org.rsmod.api.player.events.interact.LocUnimplementedEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionLoc
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.vars.VariableIntMap
import org.rsmod.utils.bits.getBits

public class LocInteractions
@Inject
constructor(
    private val locTypes: LocTypeList,
    private val varpTypes: VarpTypeList,
    private val varBitTypes: VarBitTypeList,
    private val eventBus: EventBus,
    private val protectedAccess: ProtectedAccessLauncher,
) {
    public fun triggerOp(player: Player, interaction: InteractionLoc) {
        val loc = interaction.target
        val op = opTrigger(player, loc, locTypes[loc], interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun opTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        op: InteractionOp,
    ): OpEvent? {
        val multiLoc = multiLoc(loc, type, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger = opTrigger(player, multiLoc, multiLocType, op)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }

        val typeEvent = loc.toOp(type, op)
        if (eventBus.contains(typeEvent::class.java, type.id)) {
            return typeEvent
        }

        val contentEvent = loc.toContentOp(type, type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, type.contentGroup)) {
            return contentEvent
        }

        val unimplEvent = loc.toUnimplementedOp(type, op)
        if (eventBus.contains(unimplEvent::class.java, type.id)) {
            return unimplEvent
        }

        val defaultEvent = loc.toDefaultOp(type, op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }
        return null
    }

    public fun hasOpTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        op: InteractionOp,
    ): Boolean = opTrigger(player, loc, type, op) != null

    public fun triggerAp(player: Player, interaction: InteractionLoc) {
        val loc = interaction.target
        val ap = apTrigger(player, loc, locTypes[loc], interaction.op)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    public fun apTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        op: InteractionOp,
    ): ApEvent? {
        val multiLoc = multiLoc(loc, type, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger = apTrigger(player, multiLoc, multiLocType, op)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }

        val typeEvent = loc.toAp(type, op)
        if (eventBus.contains(typeEvent::class.java, type.id)) {
            return typeEvent
        }

        val contentEvent = loc.toContentAp(type, type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, type.contentGroup)) {
            return contentEvent
        }

        val defaultEvent = loc.toDefaultAp(type, op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasApTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        op: InteractionOp,
    ): Boolean = apTrigger(player, loc, type, op) != null

    public fun multiLoc(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        vars: VariableIntMap,
    ): BoundLocInfo? {
        if (type.multiLoc.isEmpty() && type.multiLocDefault <= 0) {
            return null
        }
        val varValue = type.multiVarValue(vars) ?: 0
        val multiLoc =
            if (varValue in type.multiLoc.indices) {
                type.multiLoc[varValue].toInt()
            } else {
                type.multiLocDefault
            }
        return if (!locTypes.containsKey(multiLoc)) {
            null
        } else {
            loc.copy(entity = LocEntity(multiLoc and 0xFFFF, loc.shapeId, loc.angleId))
        }
    }

    private fun BoundLocInfo.toOp(type: UnpackedLocType, op: InteractionOp): LocEvents.Op =
        when (op) {
            InteractionOp.Op1 -> LocEvents.Op1(this, type)
            InteractionOp.Op2 -> LocEvents.Op2(this, type)
            InteractionOp.Op3 -> LocEvents.Op3(this, type)
            InteractionOp.Op4 -> LocEvents.Op4(this, type)
            InteractionOp.Op5 -> LocEvents.Op5(this, type)
        }

    private fun BoundLocInfo.toContentOp(
        type: UnpackedLocType,
        contentGroup: Int,
        op: InteractionOp,
    ): LocContentEvents.Op =
        when (op) {
            InteractionOp.Op1 -> LocContentEvents.Op1(this, type, contentGroup)
            InteractionOp.Op2 -> LocContentEvents.Op2(this, type, contentGroup)
            InteractionOp.Op3 -> LocContentEvents.Op3(this, type, contentGroup)
            InteractionOp.Op4 -> LocContentEvents.Op4(this, type, contentGroup)
            InteractionOp.Op5 -> LocContentEvents.Op5(this, type, contentGroup)
        }

    private fun BoundLocInfo.toUnimplementedOp(
        type: UnpackedLocType,
        op: InteractionOp,
    ): LocUnimplementedEvents.Op =
        when (op) {
            InteractionOp.Op1 -> LocUnimplementedEvents.Op1(this, type)
            InteractionOp.Op2 -> LocUnimplementedEvents.Op2(this, type)
            InteractionOp.Op3 -> LocUnimplementedEvents.Op3(this, type)
            InteractionOp.Op4 -> LocUnimplementedEvents.Op4(this, type)
            InteractionOp.Op5 -> LocUnimplementedEvents.Op5(this, type)
        }

    private fun BoundLocInfo.toDefaultOp(
        type: UnpackedLocType,
        op: InteractionOp,
    ): LocDefaultEvents.Op =
        when (op) {
            InteractionOp.Op1 -> LocDefaultEvents.Op1(this, type)
            InteractionOp.Op2 -> LocDefaultEvents.Op2(this, type)
            InteractionOp.Op3 -> LocDefaultEvents.Op3(this, type)
            InteractionOp.Op4 -> LocDefaultEvents.Op4(this, type)
            InteractionOp.Op5 -> LocDefaultEvents.Op5(this, type)
        }

    private fun BoundLocInfo.toAp(type: UnpackedLocType, op: InteractionOp): LocEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> LocEvents.Ap1(this, type)
            InteractionOp.Op2 -> LocEvents.Ap2(this, type)
            InteractionOp.Op3 -> LocEvents.Ap3(this, type)
            InteractionOp.Op4 -> LocEvents.Ap4(this, type)
            InteractionOp.Op5 -> LocEvents.Ap5(this, type)
        }

    private fun BoundLocInfo.toContentAp(
        type: UnpackedLocType,
        contentGroup: Int,
        op: InteractionOp,
    ): LocContentEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> LocContentEvents.Ap1(this, type, contentGroup)
            InteractionOp.Op2 -> LocContentEvents.Ap2(this, type, contentGroup)
            InteractionOp.Op3 -> LocContentEvents.Ap3(this, type, contentGroup)
            InteractionOp.Op4 -> LocContentEvents.Ap4(this, type, contentGroup)
            InteractionOp.Op5 -> LocContentEvents.Ap5(this, type, contentGroup)
        }

    private fun BoundLocInfo.toDefaultAp(
        type: UnpackedLocType,
        op: InteractionOp,
    ): LocDefaultEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> LocDefaultEvents.Ap1(this, type)
            InteractionOp.Op2 -> LocDefaultEvents.Ap2(this, type)
            InteractionOp.Op3 -> LocDefaultEvents.Ap3(this, type)
            InteractionOp.Op4 -> LocDefaultEvents.Ap4(this, type)
            InteractionOp.Op5 -> LocDefaultEvents.Ap5(this, type)
        }

    private fun UnpackedLocType.multiVarValue(vars: VariableIntMap): Int? {
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
