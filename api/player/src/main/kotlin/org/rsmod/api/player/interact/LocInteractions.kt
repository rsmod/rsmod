package org.rsmod.api.player.interact

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.LocContentEvents
import org.rsmod.api.player.events.interact.LocDefaultEvents
import org.rsmod.api.player.events.interact.LocEvents
import org.rsmod.api.player.events.interact.LocUnimplementedEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.route.BoundValidator
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionLocOp
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.vars.VarPlayerIntMap
import org.rsmod.utils.bits.getBits

public class LocInteractions
@Inject
constructor(
    private val locTypes: LocTypeList,
    private val varpTypes: VarpTypeList,
    private val varBitTypes: VarBitTypeList,
    private val boundValidator: BoundValidator,
    private val eventBus: EventBus,
) {
    public fun interact(
        player: Player,
        loc: BoundLocInfo,
        op: InteractionOp,
        type: UnpackedLocType = locTypes[loc],
    ) {
        val opTrigger = hasOpTrigger(player, loc, op, type)
        val apTrigger = hasApTrigger(player, loc, op, type)
        val interaction =
            InteractionLocOp(
                target = loc,
                op = op,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        player.interaction = interaction

        // The _current_ multiloc dimensions are used for the op range condition here. However,
        // at the engine-interaction level, this is **not** the case - it uses the base loc for
        // distance checks.
        val visLoc = multiLoc(loc, type, player.vars) ?: loc
        if (!player.isWithinOpRange(visLoc)) {
            player.walk(visLoc.coords)
        }
    }

    private fun Player.isWithinOpRange(loc: BoundLocInfo): Boolean =
        boundValidator.collides(avatar, loc) || boundValidator.touches(avatar, loc)

    public fun opTrigger(
        player: Player,
        loc: BoundLocInfo,
        op: InteractionOp,
        type: UnpackedLocType = locTypes[loc],
        base: BoundLocInfo = loc,
    ): OpEvent? {
        val multiLoc = multiLoc(loc, type, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger = opTrigger(player, multiLoc, op, multiLocType, base)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }

        val typeEvent = toOp(base, loc, type, op)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent = toContentOp(base, loc, type, type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val unimplEvent = toUnimplementedOp(base, loc, type, op)
        if (eventBus.contains(unimplEvent::class.java, unimplEvent.id)) {
            return unimplEvent
        }

        val defaultEvent = toDefaultOp(base, loc, type, op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }
        return null
    }

    public fun hasOpTrigger(
        player: Player,
        loc: BoundLocInfo,
        op: InteractionOp,
        type: UnpackedLocType = locTypes[loc],
    ): Boolean = opTrigger(player, loc, op, type) != null

    public fun apTrigger(
        player: Player,
        loc: BoundLocInfo,
        op: InteractionOp,
        type: UnpackedLocType = locTypes[loc],
        base: BoundLocInfo = loc,
    ): ApEvent? {
        val multiLoc = multiLoc(loc, type, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger = apTrigger(player, multiLoc, op, multiLocType, base)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }

        val typeEvent = toAp(base, loc, type, op)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent = toContentAp(base, loc, type, type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val defaultEvent = toDefaultAp(base, loc, type, op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasApTrigger(
        player: Player,
        loc: BoundLocInfo,
        op: InteractionOp,
        type: UnpackedLocType = locTypes[loc],
    ): Boolean = apTrigger(player, loc, op, type) != null

    public fun multiLoc(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        vars: VarPlayerIntMap,
    ): BoundLocInfo? {
        if (type.multiLoc.isEmpty() && type.multiLocDefault <= 0) {
            return null
        }
        val varValue = type.multiVarValue(vars) ?: 0
        val multiLoc =
            if (varValue in type.multiLoc.indices) {
                type.multiLoc[varValue].toInt() and 0xFFFF
            } else {
                type.multiLocDefault
            }
        return if (!locTypes.containsKey(multiLoc)) {
            null
        } else {
            loc.copy(entity = LocEntity(multiLoc, loc.shapeId, loc.angleId))
        }
    }

    private fun toOp(
        base: BoundLocInfo,
        vis: BoundLocInfo,
        type: UnpackedLocType,
        op: InteractionOp,
    ): LocEvents.Op =
        when (op) {
            InteractionOp.Op1 -> LocEvents.Op1(base, vis, type)
            InteractionOp.Op2 -> LocEvents.Op2(base, vis, type)
            InteractionOp.Op3 -> LocEvents.Op3(base, vis, type)
            InteractionOp.Op4 -> LocEvents.Op4(base, vis, type)
            InteractionOp.Op5 -> LocEvents.Op5(base, vis, type)
        }

    private fun toContentOp(
        base: BoundLocInfo,
        vis: BoundLocInfo,
        type: UnpackedLocType,
        contentGroup: Int,
        op: InteractionOp,
    ): LocContentEvents.Op =
        when (op) {
            InteractionOp.Op1 -> LocContentEvents.Op1(base, vis, type, contentGroup)
            InteractionOp.Op2 -> LocContentEvents.Op2(base, vis, type, contentGroup)
            InteractionOp.Op3 -> LocContentEvents.Op3(base, vis, type, contentGroup)
            InteractionOp.Op4 -> LocContentEvents.Op4(base, vis, type, contentGroup)
            InteractionOp.Op5 -> LocContentEvents.Op5(base, vis, type, contentGroup)
        }

    private fun toUnimplementedOp(
        base: BoundLocInfo,
        vis: BoundLocInfo,
        type: UnpackedLocType,
        op: InteractionOp,
    ): LocUnimplementedEvents.Op =
        when (op) {
            InteractionOp.Op1 -> LocUnimplementedEvents.Op1(base, vis, type)
            InteractionOp.Op2 -> LocUnimplementedEvents.Op2(base, vis, type)
            InteractionOp.Op3 -> LocUnimplementedEvents.Op3(base, vis, type)
            InteractionOp.Op4 -> LocUnimplementedEvents.Op4(base, vis, type)
            InteractionOp.Op5 -> LocUnimplementedEvents.Op5(base, vis, type)
        }

    private fun toDefaultOp(
        base: BoundLocInfo,
        vis: BoundLocInfo,
        type: UnpackedLocType,
        op: InteractionOp,
    ): LocDefaultEvents.Op =
        when (op) {
            InteractionOp.Op1 -> LocDefaultEvents.Op1(base, vis, type)
            InteractionOp.Op2 -> LocDefaultEvents.Op2(base, vis, type)
            InteractionOp.Op3 -> LocDefaultEvents.Op3(base, vis, type)
            InteractionOp.Op4 -> LocDefaultEvents.Op4(base, vis, type)
            InteractionOp.Op5 -> LocDefaultEvents.Op5(base, vis, type)
        }

    private fun toAp(
        base: BoundLocInfo,
        vis: BoundLocInfo,
        type: UnpackedLocType,
        op: InteractionOp,
    ): LocEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> LocEvents.Ap1(base, vis, type)
            InteractionOp.Op2 -> LocEvents.Ap2(base, vis, type)
            InteractionOp.Op3 -> LocEvents.Ap3(base, vis, type)
            InteractionOp.Op4 -> LocEvents.Ap4(base, vis, type)
            InteractionOp.Op5 -> LocEvents.Ap5(base, vis, type)
        }

    private fun toContentAp(
        base: BoundLocInfo,
        vis: BoundLocInfo,
        type: UnpackedLocType,
        contentGroup: Int,
        op: InteractionOp,
    ): LocContentEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> LocContentEvents.Ap1(base, vis, type, contentGroup)
            InteractionOp.Op2 -> LocContentEvents.Ap2(base, vis, type, contentGroup)
            InteractionOp.Op3 -> LocContentEvents.Ap3(base, vis, type, contentGroup)
            InteractionOp.Op4 -> LocContentEvents.Ap4(base, vis, type, contentGroup)
            InteractionOp.Op5 -> LocContentEvents.Ap5(base, vis, type, contentGroup)
        }

    private fun toDefaultAp(
        base: BoundLocInfo,
        vis: BoundLocInfo,
        type: UnpackedLocType,
        op: InteractionOp,
    ): LocDefaultEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> LocDefaultEvents.Ap1(base, vis, type)
            InteractionOp.Op2 -> LocDefaultEvents.Ap2(base, vis, type)
            InteractionOp.Op3 -> LocDefaultEvents.Ap3(base, vis, type)
            InteractionOp.Op4 -> LocDefaultEvents.Ap4(base, vis, type)
            InteractionOp.Op5 -> LocDefaultEvents.Ap5(base, vis, type)
        }

    private fun UnpackedLocType.multiVarValue(vars: VarPlayerIntMap): Int? {
        if (multiVarp > 0) {
            val varp = varpTypes[multiVarp] ?: return null
            return vars[varp]
        } else if (multiVarBit > 0) {
            val varBit = varBitTypes[multiVarBit] ?: return null
            val packed = vars[varBit.baseVar]
            return packed.getBits(varBit.bits)
        }
        return null
    }

    public fun hasOp(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        vars: VarPlayerIntMap,
        op: InteractionOp,
    ): Boolean {
        val multiLoc = multiLoc(loc, type, vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            return multiLocType.hasOp(op)
        }
        return type.hasOp(op)
    }
}
