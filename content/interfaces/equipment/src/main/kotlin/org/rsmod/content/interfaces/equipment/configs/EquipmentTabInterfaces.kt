package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias equip_components = EquipmentTabComponents

typealias equip_interfaces = EquipmentTabInterfaces

object EquipmentTabComponents : ComponentReferences() {
    val equipment_stats = find("equipment_tab:equipment", 8390474450236369249)
    val guide_prices = find("equipment_tab:pricechecker", 8676904961757321627)
    val items_kept_on_death = find("equipment_tab:deathkeep", 5305681375426955463)
    val call_follower = find("equipment_tab:call_follower", 1419599218945252788)

    val equipment_stats_side_inv = find("equipment_inventory:items", 2324553274148475142)
    val equipment_stats_off_stab = find("equipment_stats:stabatt", 3806835937017477651)
    val equipment_stats_off_slash = find("equipment_stats:slashatt", 7622607483727497370)
    val equipment_stats_off_crush = find("equipment_stats:crushatt", 2215006993582741281)
    val equipment_stats_off_magic = find("equipment_stats:magicatt", 5865756299418192119)
    val equipment_stats_off_range = find("equipment_stats:rangeatt", 458155809273436030)
    val equipment_stats_speed_base = find("equipment_stats:attackspeedbase", 2933472036901113396)
    val equipment_stats_speed = find("equipment_stats:attackspeedactual", 4992392399301827862)
    val equipment_stats_def_stab = find("equipment_stats:stabdef", 1327141314197670826)
    val equipment_stats_def_slash = find("equipment_stats:slashdef", 5142912860907690545)
    val equipment_stats_def_crush = find("equipment_stats:crushdef", 8958684407617710264)
    val equipment_stats_def_range = find("equipment_stats:magicdef", 3386061676598385294)
    val equipment_stats_def_magic = find("equipment_stats:rangedef", 7201833223308405013)
    val equipment_stats_melee_str = find("equipment_stats:meleestrength", 8070818728232639809)
    val equipment_stats_ranged_str = find("equipment_stats:rangestrength", 2663218238087883720)
    val equipment_stats_magic_dmg = find("equipment_stats:magicdamage", 6478989784797903439)
    val equipment_stats_prayer = find("equipment_stats:prayer", 1071389294653147350)
    val equipment_stats_undead = find("equipment_stats:typemultiplier", 183523615268076893)
    val equipment_stats_undead_tooltip = find("equipment_stats:tooltip", 2570685908532145491)
    val equipment_stats_slayer = find("equipment_stats:slayermultiplier", 3999295161978096612)

    val guide_prices_side_inv = find("price_checker_inventory:items", 5117171527860789248)
    val guide_prices_main_inv = find("price_checker:items", 6972858884917101391)
    val guide_prices_search = find("price_checker:other", 8789122821923308634)
    val guide_prices_search_obj = find("price_checker:otheritem", 223131799263771155)
    val guide_prices_add_all = find("price_checker:all", 2337936032262083520)
    val guide_prices_total_price_text = find("price_checker:output", 1168270853491036323)

    val items_kept_on_death_pbutton = find("items_kept_on_death:right", 4415655229161726626)
    val items_kept_on_death_risk = find("items_kept_on_death:value", 1283699627070596867)
}

object EquipmentTabInterfaces : InterfaceReferences() {
    val equipment_stats_main = find("equipment_stats", 56068798)
    val equipment_stats_side = find("equipment_inventory", 118973798)
    val guide_prices_main = find("price_checker", 1786273493)
    val guide_prices_side = find("price_checker_inventory", 9223372034789267669)
    val items_kept_on_death = find("items_kept_on_death", 1119357612)
}
