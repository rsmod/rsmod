package org.rsmod.content.interfaces.bank

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.combat.WeaponSpeeds
import org.rsmod.api.player.combat.WornBonuses
import org.rsmod.api.player.output.ClientScripts.statGroupTooltip
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.ui.ifOpenMainSidePair
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.player.vars.intVarp
import org.rsmod.content.interfaces.bank.configs.bank_components
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList

private var Player.extraOpsSpecialBits by intVarp(varps.generic_temp_state_261)
private var Player.extraOpsWearBits by intVarp(varps.generic_temp_state_262)
private var Player.extraOpsConsumableBits by intVarp(varps.generic_temp_state_263)

fun Player.openBank(eventBus: EventBus) {
    ifOpenMainSidePair(interfaces.bank_main, interfaces.bank_side, -1, -2, eventBus)
}

/** Opens bank but does not send any events such as `if_setevent`s */
fun Player.openBankWithoutEvents(eventBus: EventBus) {
    disableIfEvents = true
    ifOpenMainSidePair(interfaces.bank_main, interfaces.bank_side, -1, -2, eventBus)
    disableIfEvents = false
}

internal fun Player.highlightNoClickClear() {
    runClientScript(3407, bank_components.side_com40.packed)
}

internal fun Player.setBanksideExtraOps(objTypes: ObjTypeList) {
    var specialBits = 0
    var wearBits = 0
    var consumableBits = 0

    for (slot in inv.indices) {
        val obj = inv[slot] ?: continue
        val type = objTypes[obj]

        val specialBit = type.paramOrNull(params.bankside_extraop_bit)
        if (specialBit != null) {
            val varbit = type.param(params.bankside_extraop_varbit)
            val flipReq = type.param(params.bankside_extraop_flip)
            val enabled = flipReq && vars[varbit] == 0 || !flipReq && vars[varbit] > 0
            if (enabled) {
                specialBits = specialBits or (1 shl specialBit)
                continue
            }
        }

        val extraOpText = type.param(params.bankside_extraop)
        if (extraOpText.isNotBlank()) {
            continue
        }

        val wearBit = type.wearpos1 != -1
        if (wearBit) {
            val wearOpIndex = type.param(params.wear_op_index)
            val hasWearOp = type.hasInvOp(wearOpIndex)
            if (hasWearOp) {
                wearBits = wearBits or (1 shl slot)
                continue
            }
        }

        val foodBit = type.isContentType(content.food)
        val potionBit = type.isContentType(content.potion)

        val consumableBit = foodBit || potionBit
        if (consumableBit) {
            consumableBits = consumableBits or (1 shl slot)
            continue
        }
    }

    extraOpsSpecialBits = specialBits
    extraOpsWearBits = wearBits
    extraOpsConsumableBits = consumableBits
}

internal fun Player.setBankWornBonuses(wornBonuses: WornBonuses, weaponSpeeds: WeaponSpeeds) {
    val comps = bank_components
    val stats = wornBonuses.calculate(this)
    val speedBase = weaponSpeeds.base(this)
    val speedActual = weaponSpeeds.actual(this)
    val undeadSuffix = stats.undeadSuffix
    val slayerSuffix = stats.slayerSuffix
    ifSetText(comps.worn_off_stab, "Stab: ${stats.offStab.signed}")
    ifSetText(comps.worn_off_slash, "Slash: ${stats.offSlash.signed}")
    ifSetText(comps.worn_off_crush, "Crush: ${stats.offCrush.signed}")
    ifSetText(comps.worn_off_magic, "Magic: ${stats.offMagic.signed}")
    ifSetText(comps.worn_off_range, "Range: ${stats.offRange.signed}")
    ifSetText(comps.worn_speed_base, "Base: ${speedBase.tickToSecs}")
    ifSetText(comps.worn_speed, "Actual: ${speedActual.tickToSecs}")
    ifSetText(comps.worn_def_stab, "Stab: ${stats.defStab.signed}")
    ifSetText(comps.worn_def_slash, "Slash: ${stats.defSlash.signed}")
    ifSetText(comps.worn_def_crush, "Crush: ${stats.defCrush.signed}")
    ifSetText(comps.worn_def_range, "Range: ${stats.defRange.signed}")
    ifSetText(comps.worn_def_magic, "Magic: ${stats.defMagic.signed}")
    ifSetText(comps.worn_melee_str, "Melee STR: ${stats.meleeStr.signed}")
    ifSetText(comps.worn_ranged_str, "Ranged STR: ${stats.rangedStr.signed}")
    ifSetText(comps.worn_magic_dmg, "Magic DMG: ${stats.magicDmg.formatPercent}")
    ifSetText(comps.worn_prayer, "Prayer: ${stats.prayer.signed}")
    ifSetText(comps.worn_undead, "Undead: ${stats.undead.formatWholePercent}$undeadSuffix")
    statGroupTooltip(
        this,
        comps.tooltip,
        comps.worn_undead,
        "Increases your effective accuracy and damage against undead creatures. " +
            "For multi-target Ranged and Magic attacks, this applies only to the " +
            "primary target. It does not stack with the Slayer multiplier.",
    )
    ifSetText(comps.worn_slayer, "Slayer: ${stats.slayer.formatWholePercent}$slayerSuffix")
}

private val Int.signed: String
    get() = if (this < 0) "$this" else "+$this"

private val Int.formatPercent: String
    get() = "+${this / 10.0}%"

private val Int.formatWholePercent: String
    get() = "+${this / 10}%"

private val Int.tickToSecs: String
    get() = "${(this * 600) / 1000.0}s"

// Undead bonus has a trailing whitespace when bonus is at 0.
private val WornBonuses.Bonuses.undeadSuffix: String
    get() = if (undead == 0) " " else if (undeadMeleeOnly) " (melee)" else " (all styles)"

private val WornBonuses.Bonuses.slayerSuffix: String
    get() = if (slayer == 0) "" else if (slayerMeleeOnly) " (melee)" else " (all styles)"
