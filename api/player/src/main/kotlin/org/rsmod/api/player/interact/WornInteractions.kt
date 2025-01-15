package org.rsmod.api.player.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.events.interact.WornObjContentEvents
import org.rsmod.api.player.events.interact.WornObjEvents
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.player.protect.ifClear
import org.rsmod.api.player.worn.WornUnequipOp
import org.rsmod.api.player.worn.WornUnequipResult
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isType
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

public class WornInteractions
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val unequipOp: WornUnequipOp,
    private val marketPrices: MarketPrices,
) {
    private val logger = InlineLogger()

    public fun interact(player: Player, worn: Inventory, wornSlot: Int, op: IfButtonOp) {
        val obj = worn[wornSlot] ?: return
        interact(player, worn, wornSlot, player.inv, obj, objTypes[obj], op)
    }

    private fun interact(
        player: Player,
        worn: Inventory,
        wornSlot: Int,
        into: Inventory,
        obj: InvObj,
        type: UnpackedObjType,
        op: IfButtonOp,
    ) {
        if (op == IfButtonOp.Op10) {
            player.objExamine(type, obj.count, marketPrices[type] ?: 0)
            return
        }

        if (player.isDelayed || !obj.isType(type)) {
            return
        }

        player.ifClear(eventBus)

        @Suppress("KotlinConstantConditions")
        when (op) {
            IfButtonOp.Op1 -> player.wornOp1(wornSlot, worn, into)
            IfButtonOp.Op2 -> player.wornOp2(obj, type, wornSlot)
            IfButtonOp.Op3 -> player.wornOp3(obj, type, wornSlot)
            IfButtonOp.Op4 -> player.wornOp4(obj, type, wornSlot)
            IfButtonOp.Op5 -> player.wornOp5(obj, type, wornSlot)
            IfButtonOp.Op6 -> player.wornOp6(obj, type, wornSlot)
            IfButtonOp.Op7 -> player.wornOp7(obj, type, wornSlot)
            IfButtonOp.Op8 -> player.wornOp8(obj, type, wornSlot)
            IfButtonOp.Op9 -> player.wornOp9(obj, type, wornSlot)
            IfButtonOp.Op10 -> throw IllegalStateException("Unreachable.")
        }
    }

    private fun Player.wornOp1(wornSlot: Int, worn: Inventory, into: Inventory) {
        val result = unequipOp.unequip(this, wornSlot, worn, into)
        if (result is WornUnequipResult.Fail) {
            result.message?.let(::mes)
            return
        }
        // TODO: sound_synth(type.param(params.unequip_sound))
    }

    private fun Player.wornOp2(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.keyed[WornObjEvents.Op2::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op2(this, wornSlot, obj))
            return
        }
        val groupScript = eventBus.keyed[WornObjContentEvents.Op2::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op2(this, wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "WornOp2 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.wornOp3(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.keyed[WornObjEvents.Op3::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op3(this, wornSlot, obj))
            return
        }
        val groupScript = eventBus.keyed[WornObjContentEvents.Op3::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op3(this, wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "WornOp3 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.wornOp4(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.keyed[WornObjEvents.Op4::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op4(this, wornSlot, obj))
            return
        }
        val groupScript = eventBus.keyed[WornObjContentEvents.Op4::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op4(this, wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "WornOp4 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.wornOp5(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.keyed[WornObjEvents.Op5::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op5(this, wornSlot, obj))
            return
        }
        val groupScript = eventBus.keyed[WornObjContentEvents.Op5::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op5(this, wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "WornOp5 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.wornOp6(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.keyed[WornObjEvents.Op6::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op6(this, wornSlot, obj))
            return
        }
        val groupScript = eventBus.keyed[WornObjContentEvents.Op6::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op6(this, wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "WornOp6 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.wornOp7(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.keyed[WornObjEvents.Op7::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op7(this, wornSlot, obj))
            return
        }
        val groupScript = eventBus.keyed[WornObjContentEvents.Op7::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op7(this, wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "WornOp7 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.wornOp8(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.keyed[WornObjEvents.Op8::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op8(this, wornSlot, obj))
            return
        }
        val groupScript = eventBus.keyed[WornObjContentEvents.Op8::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op8(this, wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "WornOp8 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.wornOp9(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.keyed[WornObjEvents.Op9::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op9(this, wornSlot, obj))
            return
        }
        val groupScript = eventBus.keyed[WornObjContentEvents.Op9::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op9(this, wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "WornOp9 for `${type.name}` is not implemented: type=$type" }
    }
}
