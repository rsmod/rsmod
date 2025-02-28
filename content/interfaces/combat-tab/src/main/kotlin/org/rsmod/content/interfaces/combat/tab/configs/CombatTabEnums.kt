package org.rsmod.content.interfaces.combat.tab.configs

import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.api.type.script.dsl.EnumPluginBuilder
import org.rsmod.game.type.obj.WeaponCategory
import org.rsmod.game.type.varbit.VarBitType

typealias combat_enums = CombatTabEnums

object CombatTabEnums : EnumReferences() {
    val weapons_last_stance = find<Int, VarBitType>("weapon_last_stance_varbits")
}

object CombatTabEnumBuilder : EnumBuilder() {
    init {
        build<Int, VarBitType>("weapon_last_stance_varbits") {
            this[WeaponCategory.Unarmed] = combat_varbits.last_style_unarmed
            this[WeaponCategory.Axe] = combat_varbits.last_style_axe
            this[WeaponCategory.Blunt] = combat_varbits.last_style_blunt
            this[WeaponCategory.Bow] = combat_varbits.last_style_bow
            this[WeaponCategory.Claw] = combat_varbits.last_style_claw
            this[WeaponCategory.Crossbow] = combat_varbits.last_style_crossbow
            this[WeaponCategory.Salamander] = combat_varbits.last_style_salamander
            this[WeaponCategory.Chinchompas] = combat_varbits.last_style_chinchompa
            this[WeaponCategory.Gun] = combat_varbits.last_style_gun
            this[WeaponCategory.SlashSword] = combat_varbits.last_style_slash_sword
            this[WeaponCategory.TwoHandedSword] = combat_varbits.last_style_2h_sword
            this[WeaponCategory.Pickaxe] = combat_varbits.last_style_pickaxe
            this[WeaponCategory.Polearm] = combat_varbits.last_style_polearm
            this[WeaponCategory.Polestaff] = combat_varbits.last_style_polestaff
            this[WeaponCategory.Scythe] = combat_varbits.last_style_scythe
            this[WeaponCategory.Spear] = combat_varbits.last_style_spear
            this[WeaponCategory.Spiked] = combat_varbits.last_style_spiked
            this[WeaponCategory.StabSword] = combat_varbits.last_style_stab_sword
            this[WeaponCategory.Staff] = combat_varbits.last_style_staff
            this[WeaponCategory.Thrown] = combat_varbits.last_style_thrown
            this[WeaponCategory.Whip] = combat_varbits.last_style_whip
            this[WeaponCategory.BladedStaff] = combat_varbits.last_style_bladed_staff
            this[WeaponCategory.Banner] = combat_varbits.last_style_banner
            this[WeaponCategory.PoweredStaff] = combat_varbits.last_style_powered_staff
            this[WeaponCategory.Bludgeon] = combat_varbits.last_style_bludgeon
            this[WeaponCategory.Bulwark] = combat_varbits.last_style_bulwark
        }
    }

    private operator fun EnumPluginBuilder<Int, VarBitType>.set(
        category: WeaponCategory,
        varbit: VarBitType,
    ) {
        this[category.id] = varbit
    }
}
