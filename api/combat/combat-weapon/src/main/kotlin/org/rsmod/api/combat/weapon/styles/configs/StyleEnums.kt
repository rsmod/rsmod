package org.rsmod.api.combat.weapon.styles.configs

import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.weapon.styles.PackedStyles
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.api.type.script.dsl.EnumPluginBuilder
import org.rsmod.game.type.obj.WeaponCategory

internal typealias style_enums = StyleEnums

internal object StyleEnums : EnumReferences() {
    val weapon_attack_styles = find<Int, Int>("weapon_attack_styles")
}

internal object StyleEnumBuilder : EnumBuilder() {
    init {
        build<Int, Int>("weapon_attack_styles") {
            styles(WeaponCategory.Unarmed, accurate, aggressive, defensive)
            styles(WeaponCategory.Axe, accurate, aggressive, aggressive, defensive)
            styles(WeaponCategory.Blunt, accurate, aggressive, defensive)
            styles(WeaponCategory.Bow, accurateRanged, rapid, longRange)
            styles(WeaponCategory.Claw, accurate, aggressive, controlled, defensive)
            styles(WeaponCategory.Crossbow, accurateRanged, rapid, longRange)
            styles(WeaponCategory.Salamander, aggressive, rapid, defensive, null)
            styles(WeaponCategory.Chinchompas, accurateRanged, rapid, longRange)
            styles(WeaponCategory.Gun, null, aggressive, null, null)
            styles(WeaponCategory.SlashSword, accurate, aggressive, controlled, defensive)
            styles(WeaponCategory.TwoHandedSword, accurate, aggressive, aggressive, defensive)
            styles(WeaponCategory.Pickaxe, accurate, aggressive, aggressive, defensive)
            styles(WeaponCategory.Polearm, controlled, aggressive, defensive)
            styles(WeaponCategory.Polestaff, accurate, aggressive, defensive)
            styles(WeaponCategory.Scythe, accurate, aggressive, aggressive, defensive)
            styles(WeaponCategory.Spear, controlled, controlled, controlled, defensive)
            styles(WeaponCategory.Spiked, accurate, aggressive, controlled, defensive)
            styles(WeaponCategory.StabSword, accurate, aggressive, aggressive, defensive)
            styles(WeaponCategory.Staff, accurate, aggressive, defensive)
            styles(WeaponCategory.Thrown, accurateRanged, rapid, longRange)
            styles(WeaponCategory.Whip, accurate, controlled, defensive)
            styles(WeaponCategory.BladedStaff, accurate, aggressive, defensive)
            styles(WeaponCategory.Banner, accurate, aggressive, aggressive, defensive)
            styles(WeaponCategory.PoweredStaff, accurateRanged, accurateRanged, longRange)
            styles(WeaponCategory.Bludgeon, aggressive, aggressive, aggressive)
            styles(WeaponCategory.Bulwark, accurate, null, null, aggressive)
        }
    }

    private val accurate
        get() = AttackStyle.AccurateMelee

    private val aggressive
        get() = AttackStyle.AggressiveMelee

    private val defensive
        get() = AttackStyle.DefensiveMelee

    private val controlled
        get() = AttackStyle.ControlledMelee

    private val accurateRanged
        get() = AttackStyle.AccurateRanged

    private val rapid
        get() = AttackStyle.RapidRanged

    private val longRange
        get() = AttackStyle.LongRangeRanged

    private fun EnumPluginBuilder<Int, Int>.styles(
        weapon: WeaponCategory,
        one: AttackStyle,
        two: AttackStyle,
        three: AttackStyle,
    ) {
        styles(weapon, one, two, null, three)
    }

    private fun EnumPluginBuilder<Int, Int>.styles(
        weapon: WeaponCategory,
        one: AttackStyle?,
        two: AttackStyle?,
        three: AttackStyle?,
        four: AttackStyle?,
    ) {
        val styles = PackedStyles(one?.id ?: 0, two?.id ?: 0, three?.id ?: 0, four?.id ?: 0)
        this[weapon.id] = styles.packed
    }
}
