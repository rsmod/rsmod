package org.rsmod.api.player.interact

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.LocTContentEvents
import org.rsmod.api.player.events.interact.LocTDefaultEvents
import org.rsmod.api.player.events.interact.LocTEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionLocT
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.movement.RouteRequestLoc
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.vars.VarPlayerIntMap
import org.rsmod.utils.bits.getBits

public class LocTInteractions
@Inject
constructor(
    private val locTypes: LocTypeList,
    private val varpTypes: VarpTypeList,
    private val varBitTypes: VarBitTypeList,
    private val eventBus: EventBus,
) {
    public fun interact(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
    ) {
        val opTrigger = hasOpTrigger(player, loc, type, objType, component, comsub)
        val apTrigger = hasApTrigger(player, loc, type, objType, component, comsub)
        val interaction =
            InteractionLocT(
                target = loc,
                comsub = comsub,
                objType = objType,
                component = component,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest =
            RouteRequestLoc(
                destination = loc.coords,
                width = type.width,
                length = type.length,
                shape = loc.entity.shape,
                angle = loc.entity.angle,
                forceApproachFlags = type.forceApproachFlags,
            )
        player.clearPendingAction(eventBus)
        player.clearMapFlag()
        player.interaction = interaction
        player.routeRequest = routeRequest
    }

    public fun opTrigger(
        player: Player,
        loc: BoundLocInfo,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
        type: UnpackedLocType = locTypes[loc],
        base: BoundLocInfo = loc,
    ): OpEvent? {
        val multiLoc = multiLoc(loc, type, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger =
                opTrigger(player, multiLoc, objType, component, comsub, multiLocType, base)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }

        val typeEvent = LocTEvents.Op(loc, type, base, objType, comsub, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent = LocTContentEvents.Op(loc, type, base, objType, comsub, component)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val defaultEvent = LocTDefaultEvents.Op(loc, type, base, objType, comsub, component)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasOpTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
    ): Boolean = opTrigger(player, loc, objType, component, comsub, type) != null

    public fun apTrigger(
        player: Player,
        loc: BoundLocInfo,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
        type: UnpackedLocType = locTypes[loc],
        base: BoundLocInfo = loc,
    ): ApEvent? {
        val multiLoc = multiLoc(loc, type, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger =
                apTrigger(player, multiLoc, objType, component, comsub, multiLocType, base)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }

        val typeEvent = LocTEvents.Ap(loc, type, base, objType, comsub, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent = LocTContentEvents.Ap(loc, type, base, objType, comsub, component)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val defaultEvent = LocTDefaultEvents.Ap(loc, type, base, objType, comsub, component)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasApTrigger(
        player: Player,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
    ): Boolean = apTrigger(player, loc, objType, component, comsub, type) != null

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
}
