package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias equip_components = EquipmentTabComponents

typealias equip_interfaces = EquipmentTabInterfaces

object EquipmentTabComponents : ComponentReferences() {
    val equipment = find("wornitems:equipment", 8390474450236369249)
    val guide_prices = find("wornitems:pricechecker", 8676904961757321627)
    val items_kept_on_death = find("wornitems:deathkeep", 5305681375426955463)
    val call_follower = find("wornitems:call_follower", 1419599218945252788)

    val equipment_stats_side_inv = find("equipment_side:items", 2324553274148475142)
    val equipment_stats_off_stab = find("equipment:stabatt", 3806835937017477651)
    val equipment_stats_off_slash = find("equipment:slashatt", 7622607483727497370)
    val equipment_stats_off_crush = find("equipment:crushatt", 2215006993582741281)
    val equipment_stats_off_magic = find("equipment:magicatt", 5865756299418192119)
    val equipment_stats_off_range = find("equipment:rangeatt", 458155809273436030)
    val equipment_stats_speed_base = find("equipment:attackspeedbase", 2933472036901113396)
    val equipment_stats_speed = find("equipment:attackspeedactual", 4992392399301827862)
    val equipment_stats_def_stab = find("equipment:stabdef", 1327141314197670826)
    val equipment_stats_def_slash = find("equipment:slashdef", 5142912860907690545)
    val equipment_stats_def_crush = find("equipment:crushdef", 8958684407617710264)
    val equipment_stats_def_range = find("equipment:magicdef", 3386061676598385294)
    val equipment_stats_def_magic = find("equipment:rangedef", 7201833223308405013)
    val equipment_stats_melee_str = find("equipment:meleestrength", 8070818728232639809)
    val equipment_stats_ranged_str = find("equipment:rangestrength", 2663218238087883720)
    val equipment_stats_magic_dmg = find("equipment:magicdamage", 6478989784797903439)
    val equipment_stats_prayer = find("equipment:prayer", 1071389294653147350)
    val equipment_stats_undead = find("equipment:typemultiplier", 183523615268076893)
    val equipment_stats_undead_tooltip = find("equipment:tooltip", 2570685908532145491)
    val equipment_stats_slayer = find("equipment:slayermultiplier", 3999295161978096612)

    val guide_prices_side_inv = find("ge_pricechecker_side:items", 5117171527860789248)
    val guide_prices_main_inv = find("ge_pricechecker:items", 6972858884917101391)
    val guide_prices_search = find("ge_pricechecker:other", 8789122821923308634)
    val guide_prices_search_obj = find("ge_pricechecker:otheritem", 223131799263771155)
    val guide_prices_add_all = find("ge_pricechecker:all", 2337936032262083520)
    val guide_prices_total_price_text = find("ge_pricechecker:output", 1168270853491036323)

    val items_kept_on_death_pbutton = find("deathkeep:right", 4415655229161726626)
    val items_kept_on_death_risk = find("deathkeep:value", 1283699627070596867)
}

object EquipmentTabInterfaces : InterfaceReferences() {
    val equipment_stats_main = find("equipment", 56068798)
    val equipment_stats_side = find("equipment_side", 118973798)
    val guide_prices_main = find("ge_pricechecker", 1786273493)
    val guide_prices_side = find("ge_pricechecker_side", 9223372034789267669)
    val deathkeep = find("deathkeep", 1119357612)
}
