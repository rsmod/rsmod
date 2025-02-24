package org.rsmod.content.interfaces.combat.tab.configs

import org.rsmod.api.type.builders.varbit.VarBitBuilder
import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.game.type.varbit.VarBitType

typealias combat_varbits = CombatTabVarBits

object CombatTabVarBits : VarBitReferences() {
    val last_style_unarmed = find("saved_attackstyle_unarmed")
    val last_style_axe = find("saved_attackstyle_axe")
    val last_style_blunt = find("saved_attackstyle_blunt")
    val last_style_bow = find("saved_attackstyle_bow")
    val last_style_claw = find("saved_attackstyle_claw")
    val last_style_crossbow = find("saved_attackstyle_crossbow")
    val last_style_salamander = find("saved_attackstyle_salamander")
    val last_style_chinchompa = find("saved_attackstyle_chinchompas")
    val last_style_gun = find("saved_attackstyle_gun")
    val last_style_slash_sword = find("saved_attackstyle_slash_sword")
    val last_style_2h_sword = find("saved_attackstyle_2h_sword")
    val last_style_pickaxe = find("saved_attackstyle_pickaxe")
    val last_style_polearm = find("saved_attackstyle_polearm")
    val last_style_polestaff = find("saved_attackstyle_polestaff")
    val last_style_scythe = find("saved_attackstyle_scythe")
    val last_style_spear = find("saved_attackstyle_spear")
    val last_style_spiked = find("saved_attackstyle_spiked")
    val last_style_stab_sword = find("saved_attackstyle_stab_sword")
    val last_style_staff = find("saved_attackstyle_staff")
    val last_style_thrown = find("saved_attackstyle_thrown")
    val last_style_whip = find("saved_attackstyle_whip")
    val last_style_bladed_staff = find("saved_attackstyle_bladed_staff")
    val last_style_banner = find("saved_attackstyle_banner")
    val last_style_powered_staff = find("saved_attackstyle_powered_staff")
    val last_style_bludgeon = find("saved_attackstyle_bludgeon")
    val last_style_bulwark = find("saved_attackstyle_bulwark")
}

object CombatTabVarBitBuilder : VarBitBuilder() {
    init {
        style1(combat_varbits.last_style_unarmed, 0..1)
        style1(combat_varbits.last_style_axe, 2..3)
        style1(combat_varbits.last_style_blunt, 4..5)
        style1(combat_varbits.last_style_bow, 6..7)
        style1(combat_varbits.last_style_claw, 8..9)
        style1(combat_varbits.last_style_crossbow, 10..11)
        style1(combat_varbits.last_style_salamander, 12..13)
        style1(combat_varbits.last_style_chinchompa, 14..15)
        style1(combat_varbits.last_style_gun, 16..17)
        style1(combat_varbits.last_style_slash_sword, 18..19)
        style1(combat_varbits.last_style_2h_sword, 20..21)
        style1(combat_varbits.last_style_pickaxe, 22..23)
        style1(combat_varbits.last_style_polearm, 24..25)
        style1(combat_varbits.last_style_polestaff, 26..27)
        style1(combat_varbits.last_style_scythe, 28..29)
        style1(combat_varbits.last_style_spear, 30..31)

        style2(combat_varbits.last_style_spiked, 0..1)
        style2(combat_varbits.last_style_stab_sword, 2..3)
        style2(combat_varbits.last_style_staff, 4..5)
        style2(combat_varbits.last_style_thrown, 6..7)
        style2(combat_varbits.last_style_whip, 8..9)
        style2(combat_varbits.last_style_bladed_staff, 10..11)
        style2(combat_varbits.last_style_banner, 12..13)
        style2(combat_varbits.last_style_powered_staff, 14..15)
        style2(combat_varbits.last_style_bludgeon, 16..17)
        style2(combat_varbits.last_style_bulwark, 18..19)
    }

    private fun style1(type: VarBitType, bits: IntRange) {
        build(type.internalNameValue) {
            baseVar = combat_varps.saved_attackstyle1
            startBit = bits.first
            endBit = bits.last
        }
    }

    private fun style2(type: VarBitType, bits: IntRange) {
        build(type.internalNameValue) {
            baseVar = combat_varps.saved_attackstyle2
            startBit = bits.first
            endBit = bits.last
        }
    }
}
