package org.rsmod.api.interactions

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.LocContentEvents
import org.rsmod.api.player.events.interact.LocDefaultEvents
import org.rsmod.api.player.events.interact.LocEvents
import org.rsmod.api.player.events.interact.LocUnimplementedEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.protect.withProtectedAccess
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionLoc
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
) {
    public fun triggerOp(player: Player, interaction: InteractionLoc) {
        val loc = interaction.target
        val op = opTrigger(player, loc, locTypes[loc], interaction.opSlot)
        if (op != null) {
            player.withProtectedAccess { eventBus.publish(this, op) }
        }
    }

    public fun opTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        op: Int,
    ): OpEvent? {
        val multiLoc = multiLoc(loc, type, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger = opTrigger(player, multiLoc, multiLocType, op)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }
        val opEvent = loc.toOp(type, op)
        if (opEvent != null && eventBus.contains(opEvent::class.java, type.id)) {
            return opEvent
        }
        val contentEvent = loc.toContentOp(type, type.contentType, op)
        if (contentEvent != null && eventBus.contains(contentEvent::class.java, type.contentType)) {
            return contentEvent
        }
        val unimplOpEvent = loc.toUnimplementedOp(type, op)
        if (unimplOpEvent != null && eventBus.contains(unimplOpEvent::class.java, type.id)) {
            return unimplOpEvent
        }
        val defaultEvent = loc.toDefaultOp(type, op)
        if (defaultEvent != null && eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }
        return null
    }

    public fun hasOpTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        op: Int,
    ): Boolean = opTrigger(player, loc, type, op) != null

    public fun triggerAp(player: Player, interaction: InteractionLoc) {
        val loc = interaction.target
        val ap = apTrigger(player, loc, locTypes[loc], interaction.opSlot)
        if (ap != null) {
            player.withProtectedAccess { eventBus.publish(this, ap) }
        }
    }

    public fun apTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        op: Int,
    ): ApEvent? {
        val multiLoc = multiLoc(loc, type, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger = apTrigger(player, multiLoc, multiLocType, op)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }
        val apEvent = loc.toAp(type, op)
        if (apEvent != null && eventBus.contains(apEvent::class.java, type.id)) {
            return apEvent
        }
        val contentEvent = loc.toContentAp(type, type.contentType, op)
        if (contentEvent != null && eventBus.contains(contentEvent::class.java, type.contentType)) {
            return contentEvent
        }
        val defaultEvent = loc.toDefaultAp(type, op)
        if (defaultEvent != null && eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }
        return null
    }

    public fun hasApTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        op: Int,
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

    private fun BoundLocInfo.toOp(type: UnpackedLocType, op: Int): LocEvents.Op? =
        when (op) {
            1 -> LocEvents.Op1(this, type)
            2 -> LocEvents.Op2(this, type)
            3 -> LocEvents.Op3(this, type)
            4 -> LocEvents.Op4(this, type)
            5 -> LocEvents.Op5(this, type)
            else -> null
        }

    private fun BoundLocInfo.toContentOp(
        type: UnpackedLocType,
        contentType: Int,
        op: Int,
    ): LocContentEvents.Op? =
        when (op) {
            1 -> LocContentEvents.Op1(this, type, contentType)
            2 -> LocContentEvents.Op2(this, type, contentType)
            3 -> LocContentEvents.Op3(this, type, contentType)
            4 -> LocContentEvents.Op4(this, type, contentType)
            5 -> LocContentEvents.Op5(this, type, contentType)
            else -> null
        }

    private fun BoundLocInfo.toUnimplementedOp(
        type: UnpackedLocType,
        op: Int,
    ): LocUnimplementedEvents.Op? =
        when (op) {
            1 -> LocUnimplementedEvents.Op1(this, type)
            2 -> LocUnimplementedEvents.Op2(this, type)
            3 -> LocUnimplementedEvents.Op3(this, type)
            4 -> LocUnimplementedEvents.Op4(this, type)
            5 -> LocUnimplementedEvents.Op5(this, type)
            else -> null
        }

    private fun BoundLocInfo.toDefaultOp(type: UnpackedLocType, op: Int): LocDefaultEvents.Op? =
        when (op) {
            1 -> LocDefaultEvents.Op1(this, type)
            2 -> LocDefaultEvents.Op2(this, type)
            3 -> LocDefaultEvents.Op3(this, type)
            4 -> LocDefaultEvents.Op4(this, type)
            5 -> LocDefaultEvents.Op5(this, type)
            else -> null
        }

    private fun BoundLocInfo.toAp(type: UnpackedLocType, op: Int): LocEvents.Ap? =
        when (op) {
            1 -> LocEvents.Ap1(this, type)
            2 -> LocEvents.Ap2(this, type)
            3 -> LocEvents.Ap3(this, type)
            4 -> LocEvents.Ap4(this, type)
            5 -> LocEvents.Ap5(this, type)
            else -> null
        }

    private fun BoundLocInfo.toContentAp(
        type: UnpackedLocType,
        contentType: Int,
        op: Int,
    ): LocContentEvents.Ap? =
        when (op) {
            1 -> LocContentEvents.Ap1(this, type, contentType)
            2 -> LocContentEvents.Ap2(this, type, contentType)
            3 -> LocContentEvents.Ap3(this, type, contentType)
            4 -> LocContentEvents.Ap4(this, type, contentType)
            5 -> LocContentEvents.Ap5(this, type, contentType)
            else -> null
        }

    private fun BoundLocInfo.toDefaultAp(type: UnpackedLocType, op: Int): LocDefaultEvents.Ap? =
        when (op) {
            1 -> LocDefaultEvents.Ap1(this, type)
            2 -> LocDefaultEvents.Ap2(this, type)
            3 -> LocDefaultEvents.Ap3(this, type)
            4 -> LocDefaultEvents.Ap4(this, type)
            5 -> LocDefaultEvents.Ap5(this, type)
            else -> null
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
