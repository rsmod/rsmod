package org.rsmod.content.interfaces.equipment.stats

import jakarta.inject.Inject
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.player.interact.WornInteractions
import org.rsmod.api.player.output.ClientScripts.statGroupTooltip
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.IfModalDrag
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onIfModalDrag
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.content.interfaces.equipment.configs.equip_components
import org.rsmod.content.interfaces.equipment.configs.equip_enums
import org.rsmod.content.interfaces.equipment.configs.equip_interfaces
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EquipmentStats
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val eventBus: EventBus,
    private val enumResolver: EnumTypeMapResolver,
    private val protectedAccess: ProtectedAccessLauncher,
    private val wornInteractions: WornInteractions,
    private val wornBonuses: WornBonuses,
    private val weaponSpeeds: WeaponSpeeds,
    private val marketPrices: MarketPrices,
) : PluginScript() {
    override fun ScriptContext.startUp() {
        onIfOverlayButton(equip_components.equipment_stats) { player.selectStats() }

        val componentWornSlots = enumResolver[equip_enums.mapped_wearpos].filterValuesNotNull()
        for ((slot, component) in componentWornSlots) {
            onIfModalButton(component) { opWornMain(slot, it.op) }
        }

        onIfModalButton(equip_components.equipment_stats_side_inv) { opHeldSide(it.comsub, it.op) }
        onIfModalDrag(equip_components.equipment_stats_side_inv) { dragHeldButton(it) }
    }

    private fun Player.selectStats() {
        ifClose(eventBus)
        protectedAccess.launch(this) { openStats() }
    }

    private fun ProtectedAccess.openStats() {
        stopAction()
        resetAnim()
        resetSpotanim()
        invTransmit(inv)
        ifOpenMainSidePair(
            main = equip_interfaces.equipment_stats_main,
            side = equip_interfaces.equipment_stats_side,
        )
        interfaceInvInit(
            inv = inv,
            target = equip_components.equipment_stats_side_inv,
            objRowCount = 4,
            objColCount = 7,
            dragType = 1,
            op1 = "Equip",
        )
        ifSetEvents(
            target = equip_components.equipment_stats_side_inv,
            range = inv.indices,
            IfEvent.Op1,
            IfEvent.Op10,
            IfEvent.Depth1,
            IfEvent.DragTarget,
        )
        updateBonuses()
    }

    private fun ProtectedAccess.updateBonuses() {
        val comps = equip_components
        val stats = wornBonuses.calculate(player)
        val speedBase = weaponSpeeds.base(player)
        val speedActual = weaponSpeeds.actual(player)
        val magicDmg = stats.finalMagicDmg
        val magicDmgSuffix = stats.magicDmgSuffix
        val undeadSuffix = stats.undeadSuffix
        val slayerSuffix = stats.slayerSuffix
        ifSetText(comps.equipment_stats_off_stab, "Stab: ${stats.offStab.signed}")
        ifSetText(comps.equipment_stats_off_slash, "Slash: ${stats.offSlash.signed}")
        ifSetText(comps.equipment_stats_off_crush, "Crush: ${stats.offCrush.signed}")
        ifSetText(comps.equipment_stats_off_magic, "Magic: ${stats.offMagic.signed}")
        ifSetText(comps.equipment_stats_off_range, "Range: ${stats.offRange.signed}")
        ifSetText(comps.equipment_stats_speed_base, "Base: ${speedBase.tickToSecs}")
        ifSetText(comps.equipment_stats_speed, "Actual: ${speedActual.tickToSecs}")
        ifSetText(comps.equipment_stats_def_stab, "Stab: ${stats.defStab.signed}")
        ifSetText(comps.equipment_stats_def_slash, "Slash: ${stats.defSlash.signed}")
        ifSetText(comps.equipment_stats_def_crush, "Crush: ${stats.defCrush.signed}")
        ifSetText(comps.equipment_stats_def_range, "Range: ${stats.defRange.signed}")
        ifSetText(comps.equipment_stats_def_magic, "Magic: ${stats.defMagic.signed}")
        ifSetText(comps.equipment_stats_melee_str, "Melee STR: ${stats.meleeStr.signed}")
        ifSetText(comps.equipment_stats_ranged_str, "Ranged STR: ${stats.rangedStr.signed}")
        ifSetText(comps.equipment_stats_magic_dmg, "Magic DMG: $magicDmg$magicDmgSuffix")
        ifSetText(comps.equipment_stats_prayer, "Prayer: ${stats.prayer.signed}")
        ifSetText(
            comps.equipment_stats_undead,
            "Undead: ${stats.undead.formatWholePercent}$undeadSuffix",
        )
        statGroupTooltip(
            player,
            comps.equipment_stats_undead_tooltip,
            comps.equipment_stats_undead,
            "Increases your effective accuracy and damage against undead creatures. " +
                "For multi-target Ranged and Magic attacks, this applies only to the " +
                "primary target. It does not stack with the Slayer multiplier.",
        )
        ifSetText(
            comps.equipment_stats_slayer,
            "Slayer: ${stats.slayer.formatWholePercent}$slayerSuffix",
        )
    }

    private suspend fun ProtectedAccess.opWornMain(wornSlot: Int, op: IfButtonOp) {
        val obj = worn[wornSlot] ?: return resendSlot(worn, wornSlot)
        wornInteractions.interact(this, worn, wornSlot, op)

        if (op == IfButtonOp.Op1) {
            // Cheap way of checking if obj was unequipped.
            val unequipped = obj != worn[wornSlot]
            if (unequipped) {
                updateBonuses()
            }
        }
    }

    private suspend fun ProtectedAccess.opHeldSide(invSlot: Int, op: IfButtonOp) {
        val obj = inv[invSlot] ?: return resendSlot(inv, invSlot)
        if (op == IfButtonOp.Op10) {
            val type = objTypes[obj]
            val price = marketPrices[type] ?: 0
            player.objExamine(type, obj.count, price)
            return
        }

        if (op == IfButtonOp.Op1) {
            if (!objTypes[obj].isEquipable) {
                mes("You can't equip that.")
                return
            }

            opHeld2(invSlot)

            // Cheap way of checking if obj was equipped.
            val equipped = obj != inv[invSlot]
            if (equipped) {
                updateBonuses()
            }
            return
        }

        throw IllegalStateException("Op not allowed: $op (obj=$obj, invSlot=$invSlot, inv=$inv)")
    }

    private fun ProtectedAccess.dragHeldButton(drag: IfModalDrag) {
        val fromSlot = drag.selectedSlot ?: return
        val intoSlot = drag.targetSlot ?: return
        invMoveToSlot(inv, inv, fromSlot, intoSlot)
    }
}

private val Int.signed: String
    get() = if (this < 0) "$this" else "+$this"

private val Int.formatPercent: String
    get() = "+${this / 10.0}%"

private val Int.formatWholePercent: String
    get() = "+${this / 10}%"

private val Int.tickToSecs: String
    get() = "${(this * 600) / 1000.0}s"

private val WornBonuses.Bonuses.finalMagicDmg: String
    get() = multipliedMagicDmg.formatPercent

private val WornBonuses.Bonuses.magicDmgSuffix: String
    get() = if (magicDmgAdditive == 0) "" else "<col=be66f4> ($magicDmgAdditive%)</col>"

// Undead bonus has a trailing whitespace when bonus is at 0.
private val WornBonuses.Bonuses.undeadSuffix: String
    get() = if (undead == 0) " " else if (undeadMeleeOnly) " (melee)" else " (all styles)"

private val WornBonuses.Bonuses.slayerSuffix: String
    get() = if (slayer == 0) "" else if (slayerMeleeOnly) " (melee)" else " (all styles)"
