package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias equip_components = EquipmentTabComponents

typealias equip_interfaces = EquipmentTabInterfaces

object EquipmentTabComponents : ComponentReferences() {
    val equipment_stats = find("equipment_tab_com1")
    val guide_prices = find("equipment_tab_com3")
    val items_kept_on_death = find("equipment_tab_com5")
    val call_follower = find("equipment_tab_com7")

    val equipment_stats_side_inv = find("equipment_inventory_com0")
    val equipment_stats_off_stab = find("equipment_stats_com24")
    val equipment_stats_off_slash = find("equipment_stats_com25")
    val equipment_stats_off_crush = find("equipment_stats_com26")
    val equipment_stats_off_magic = find("equipment_stats_com27")
    val equipment_stats_off_range = find("equipment_stats_com28")
    val equipment_stats_speed_base = find("equipment_stats_com53")
    val equipment_stats_speed = find("equipment_stats_com54")
    val equipment_stats_def_stab = find("equipment_stats_com30")
    val equipment_stats_def_slash = find("equipment_stats_com31")
    val equipment_stats_def_crush = find("equipment_stats_com32")
    val equipment_stats_def_range = find("equipment_stats_com33")
    val equipment_stats_def_magic = find("equipment_stats_com34")
    val equipment_stats_melee_str = find("equipment_stats_com36")
    val equipment_stats_ranged_str = find("equipment_stats_com37")
    val equipment_stats_magic_dmg = find("equipment_stats_com38")
    val equipment_stats_prayer = find("equipment_stats_com39")
    val equipment_stats_undead = find("equipment_stats_com41")
    val equipment_stats_undead_tooltip = find("equipment_stats_com52")
    val equipment_stats_slayer = find("equipment_stats_com42")
}

object EquipmentTabInterfaces : InterfaceReferences() {
    val equipment_stats_main = find("equipment_stats")
    val equipment_stats_side = find("equipment_inventory")
}
