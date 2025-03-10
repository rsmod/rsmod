package org.rsmod.api.combat.weapon.types.configs

import org.rsmod.api.combat.commons.types.AttackType
import org.rsmod.api.combat.weapon.types.TypeInternals.PackedTypes
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.api.type.script.dsl.EnumPluginBuilder
import org.rsmod.game.type.obj.WeaponCategory

internal typealias type_enums = TypeEnums

internal object TypeEnums : EnumReferences() {
    val weapon_attack_types = find<Int, Int>("weapon_attack_types")
}

internal object TypeEnumBuilder : EnumBuilder() {
    init {
        build<Int, Int>("weapon_attack_types") {
            types(WeaponCategory.Unarmed, crush, crush, crush)
            types(WeaponCategory.Axe, slash, slash, crush, slash)
            types(WeaponCategory.Blunt, crush, crush, crush)
            types(WeaponCategory.Bow, standard, standard, standard)
            types(WeaponCategory.Claw, slash, slash, stab, slash)
            types(WeaponCategory.Crossbow, heavy, heavy, heavy)
            types(WeaponCategory.Salamander, slash, standard, magic, null)
            types(WeaponCategory.Chinchompas, heavy, heavy, heavy)
            types(WeaponCategory.Gun, null, crush, null, null)
            types(WeaponCategory.SlashSword, slash, slash, stab, slash)
            types(WeaponCategory.TwoHandedSword, slash, slash, crush, slash)
            types(WeaponCategory.Pickaxe, stab, stab, crush, stab)
            types(WeaponCategory.Polearm, stab, slash, stab)
            types(WeaponCategory.Polestaff, crush, crush, crush)
            types(WeaponCategory.Scythe, slash, slash, crush, slash)
            types(WeaponCategory.Spear, stab, slash, crush, stab)
            types(WeaponCategory.Spiked, crush, crush, stab, crush)
            types(WeaponCategory.StabSword, stab, stab, slash, stab)
            types(WeaponCategory.Staff, crush, crush, crush)
            types(WeaponCategory.Thrown, light, light, light)
            types(WeaponCategory.Whip, slash, slash, slash)
            types(WeaponCategory.BladedStaff, stab, slash, crush)
            types(WeaponCategory.Banner, slash, slash, crush, slash)
            types(WeaponCategory.PoweredStaff, magic, magic, magic)
            types(WeaponCategory.Bludgeon, crush, crush, crush)
            types(WeaponCategory.Bulwark, crush)
        }
    }

    private val stab: AttackType
        get() = AttackType.Stab

    private val slash: AttackType
        get() = AttackType.Slash

    private val crush: AttackType
        get() = AttackType.Crush

    private val magic: AttackType
        get() = AttackType.Magic

    private val light: AttackType
        get() = AttackType.Light

    private val standard: AttackType
        get() = AttackType.Standard

    private val heavy: AttackType
        get() = AttackType.Heavy

    private fun EnumPluginBuilder<Int, Int>.types(weapon: WeaponCategory, one: AttackType) {
        types(weapon, one, null, null, null)
    }

    private fun EnumPluginBuilder<Int, Int>.types(
        weapon: WeaponCategory,
        one: AttackType,
        two: AttackType,
        three: AttackType,
    ) {
        types(weapon, one, two, null, three)
    }

    private fun EnumPluginBuilder<Int, Int>.types(
        weapon: WeaponCategory,
        one: AttackType?,
        two: AttackType?,
        three: AttackType?,
        four: AttackType?,
    ) {
        val types = PackedTypes(one?.id ?: 0, two?.id ?: 0, three?.id ?: 0, four?.id ?: 0)
        this[weapon.id] = types.packed
    }
}
