package org.rsmod.api.player.interact

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.NpcTContentEvents
import org.rsmod.api.player.events.interact.NpcTDefaultEvents
import org.rsmod.api.player.events.interact.NpcTEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionNpcT
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.vars.VarPlayerIntMap
import org.rsmod.utils.bits.getBits

public class NpcTInteractions
@Inject
constructor(
    private val npcTypes: NpcTypeList,
    private val varpTypes: VarpTypeList,
    private val varBitTypes: VarBitTypeList,
    private val eventBus: EventBus,
) {
    public fun interact(
        player: Player,
        npc: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ) {
        val opTrigger = hasOpTrigger(player, npc, component, comsub, objType)
        val apTrigger = hasApTrigger(player, npc, component, comsub, objType)
        val interaction =
            InteractionNpcT(
                target = npc,
                comsub = comsub,
                objType = objType,
                component = component,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestPathingEntity(npc.avatar)
        player.faceNpc(npc)
        player.interaction = interaction
        player.routeRequest = routeRequest
    }

    public fun opTrigger(
        player: Player,
        npc: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
        type: UnpackedNpcType = npc.visType,
    ): OpEvent? {
        val multiNpcType = multiNpc(type, player.vars)
        if (multiNpcType != null) {
            val multiNpcTrigger = opTrigger(player, npc, component, comsub, objType, multiNpcType)
            if (multiNpcTrigger != null) {
                return multiNpcTrigger
            }
        }

        val typeEvent = NpcTEvents.Op(npc, comsub, objType, type, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent = NpcTContentEvents.Op(npc, comsub, objType, component, type.contentGroup)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val defaultEvent = NpcTDefaultEvents.Op(npc, comsub, objType, type, component)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasOpTrigger(
        player: Player,
        npc: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): Boolean = opTrigger(player, npc, component, comsub, objType) != null

    public fun apTrigger(
        player: Player,
        npc: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
        type: UnpackedNpcType = npc.visType,
    ): ApEvent? {
        val multiNpcType = multiNpc(type, player.vars)
        if (multiNpcType != null) {
            val multiNpcTrigger = apTrigger(player, npc, component, comsub, objType, multiNpcType)
            if (multiNpcTrigger != null) {
                return multiNpcTrigger
            }
        }

        val typeEvent = NpcTEvents.Ap(npc, comsub, objType, type, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent = NpcTContentEvents.Ap(npc, comsub, objType, component, type.contentGroup)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val defaultEvent = NpcTDefaultEvents.Ap(npc, comsub, objType, type, component)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasApTrigger(
        player: Player,
        npc: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): Boolean = apTrigger(player, npc, component, comsub, objType) != null

    public fun multiNpc(type: UnpackedNpcType, vars: VarPlayerIntMap): UnpackedNpcType? {
        if (type.multiNpc.isEmpty() && type.multiNpcDefault <= 0) {
            return null
        }
        val varValue = type.multiVarValue(vars) ?: 0
        val multiNpc =
            if (varValue in type.multiNpc.indices) {
                type.multiNpc[varValue].toInt() and 0xFFFF
            } else {
                type.multiNpcDefault
            }
        return if (!npcTypes.containsKey(multiNpc)) {
            null
        } else {
            npcTypes.getOrDefault(multiNpc, null)
        }
    }

    private fun UnpackedNpcType.multiVarValue(vars: VarPlayerIntMap): Int? {
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
