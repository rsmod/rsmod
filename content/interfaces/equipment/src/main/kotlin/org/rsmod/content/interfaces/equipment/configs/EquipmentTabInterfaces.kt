package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias equip_components = EquipmentTabComponents

typealias equip_interfaces = EquipmentTabInterfaces

object EquipmentTabComponents : ComponentReferences() {
    val equipment_stats = find("equipment_tab_com1", 8390474450236369249)
    val guide_prices = find("equipment_tab_com3", 8676904961757321627)
    val items_kept_on_death = find("equipment_tab_com5", 5305681375426955463)
    val call_follower = find("equipment_tab_com7", 1419599218945252788)

    val equipment_stats_side_inv = find("equipment_inventory_com0", 2324553274148475142)
    val equipment_stats_off_stab = find("equipment_stats_com24", 3806835937017477651)
    val equipment_stats_off_slash = find("equipment_stats_com25", 7622607483727497370)
    val equipment_stats_off_crush = find("equipment_stats_com26", 2215006993582741281)
    val equipment_stats_off_magic = find("equipment_stats_com27", 5865756299418192119)
    val equipment_stats_off_range = find("equipment_stats_com28", 458155809273436030)
    val equipment_stats_speed_base = find("equipment_stats_com53", 2933472036901113396)
    val equipment_stats_speed = find("equipment_stats_com54", 4992392399301827862)
    val equipment_stats_def_stab = find("equipment_stats_com30", 1327141314197670826)
    val equipment_stats_def_slash = find("equipment_stats_com31", 5142912860907690545)
    val equipment_stats_def_crush = find("equipment_stats_com32", 8958684407617710264)
    val equipment_stats_def_range = find("equipment_stats_com33", 3386061676598385294)
    val equipment_stats_def_magic = find("equipment_stats_com34", 7201833223308405013)
    val equipment_stats_melee_str = find("equipment_stats_com36", 8070818728232639809)
    val equipment_stats_ranged_str = find("equipment_stats_com37", 2663218238087883720)
    val equipment_stats_magic_dmg = find("equipment_stats_com38", 6478989784797903439)
    val equipment_stats_prayer = find("equipment_stats_com39", 1071389294653147350)
    val equipment_stats_undead = find("equipment_stats_com41", 183523615268076893)
    val equipment_stats_undead_tooltip = find("equipment_stats_com52", 2570685908532145491)
    val equipment_stats_slayer = find("equipment_stats_com42", 3999295161978096612)

    val guide_prices_side_inv = find("price_checker_inventory_com0", 5117171527860789248)
    val guide_prices_main_inv = find("price_checker_com2", 6972858884917101391)
    val guide_prices_search = find("price_checker_com5", 8789122821923308634)
    val guide_prices_search_obj = find("price_checker_com8", 223131799263771155)
    val guide_prices_add_all = find("price_checker_com10", 2337936032262083520)
    val guide_prices_total_price_text = find("price_checker_com12", 1168270853491036323)

    val items_kept_on_death_pbutton = find("items_kept_on_death_com12", 4415655229161726626)
    val items_kept_on_death_risk = find("items_kept_on_death_com18", 1283699627070596867)
}

object EquipmentTabInterfaces : InterfaceReferences() {
    val equipment_stats_main = find("equipment_stats", 56068798)
    val equipment_stats_side = find("equipment_inventory", 118973798)
    val guide_prices_main = find("price_checker", 1786273493)
    val guide_prices_side = find("price_checker_inventory", 9223372034789267669)
    val items_kept_on_death = find("items_kept_on_death", 1119357612)
}
