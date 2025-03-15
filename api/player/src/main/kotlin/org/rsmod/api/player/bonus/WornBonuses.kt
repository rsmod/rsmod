package org.rsmod.api.player.bonus

import jakarta.inject.Inject
import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.ObjTypeList

public class WornBonuses @Inject constructor(private val objTypes: ObjTypeList) {
    public fun calculate(player: Player): Bonuses = calculate(player.worn)

    // Note(combat): We should consider caching these bonuses and recalculating them only when
    // necessary. This should be simple as most equipment changes will go through 2 functions
    // which both post WearposChange events. The only exception would be when the worn inventory
    // is manually modified (e.g., worn[1] = null), in which case we should provide and make note
    // of a function that should be used to invalidate any cached bonuses.
    public fun strengthBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.meleeStr
    }

    public fun rangedStrengthBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.rangedStr
    }

    public fun magicDamageBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.magicDmg
    }

    public fun offensiveStabBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.offStab
    }

    public fun offensiveSlashBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.offSlash
    }

    public fun offensiveCrushBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.offCrush
    }

    public fun defensiveCrushBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.defCrush
    }

    public fun defensiveStabBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.defStab
    }

    public fun defensiveSlashBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.defSlash
    }

    public fun defensiveMagic(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.defMagic
    }

    public fun defensiveRangedBonus(player: Player): Int {
        val bonuses = calculate(player)
        return bonuses.defRange
    }

    public fun calculate(inventory: Inventory): Bonuses {
        var offStab = 0
        var offSlash = 0
        var offCrush = 0
        var offMagic = 0
        var offRange = 0
        var defStab = 0
        var defSlash = 0
        var defCrush = 0
        var defRange = 0
        var defMagic = 0
        var meleeStr = 0
        var rangedStr = 0
        var magicDmg = 0
        var prayer = 0
        var undead = 0
        var slayer = 0
        var undeadMeleeOnly = false
        var slayerMeleeOnly = false

        for (obj in inventory) {
            val type = obj?.let(objTypes::get) ?: continue
            offStab += type.param(params.attack_stab)
            offSlash += type.param(params.attack_slash)
            offCrush += type.param(params.attack_crush)
            offMagic += type.param(params.attack_magic)
            offRange += type.param(params.attack_ranged)
            defStab += type.param(params.defence_stab)
            defSlash += type.param(params.defence_slash)
            defCrush += type.param(params.defence_crush)
            defRange += type.param(params.defence_ranged)
            defMagic += type.param(params.defence_magic)
            meleeStr += type.param(params.melee_strength)
            rangedStr += type.param(params.ranged_strength)
            magicDmg += type.param(params.magic_damage)
            prayer += type.param(params.item_prayer_bonus)
            undead += type.param(params.bonus_undead_buff)
            slayer += type.param(params.bonus_slayer_buff)
            undeadMeleeOnly = type.param(params.bonus_undead_meleeonly)
            slayerMeleeOnly = type.param(params.bonus_slayer_meleeonly)
        }

        return Bonuses(
            offStab = offStab,
            offSlash = offSlash,
            offCrush = offCrush,
            offMagic = offMagic,
            offRange = offRange,
            defStab = defStab,
            defSlash = defSlash,
            defCrush = defCrush,
            defRange = defRange,
            defMagic = defMagic,
            meleeStr = meleeStr,
            rangedStr = rangedStr,
            magicDmg = magicDmg,
            prayer = prayer,
            undead = undead,
            slayer = slayer,
            undeadMeleeOnly = undeadMeleeOnly,
            slayerMeleeOnly = slayerMeleeOnly,
        )
    }

    public data class Bonuses(
        val offStab: Int,
        val offSlash: Int,
        val offCrush: Int,
        val offMagic: Int,
        val offRange: Int,
        val defStab: Int,
        val defSlash: Int,
        val defCrush: Int,
        val defRange: Int,
        val defMagic: Int,
        val meleeStr: Int,
        val rangedStr: Int,
        val magicDmg: Int,
        val prayer: Int,
        val undead: Int,
        val slayer: Int,
        val undeadMeleeOnly: Boolean,
        val slayerMeleeOnly: Boolean,
    )
}
