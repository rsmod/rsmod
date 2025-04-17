package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

internal typealias bank_interfaces = BankInterfaces

internal typealias bank_components = BankComponents

internal typealias bank_comsubs = BankSubComponents

object BankInterfaces : InterfaceReferences() {
    val tutorial_overlay = find("bank_tutorial", 1206696351)
}

object BankComponents : ComponentReferences() {
    val tutorial_button = find("bank:bank_tut", 7135446288139269005)
    val capacity_container = find("bank:capacity_layer", 5691092119508665688)
    val capacity_text = find("bank:capacity", 71329117100551008)
    val main_inventory = find("bank:items", 8484261777138475560)
    val tabs = find("bank:tabs", 5066672266370361425)
    val incinerator_confirm = find("bank:incinerator_confirm", 7492851284321215925)
    val potionstore_items = find("bank:potionstore_items", 801223983307344610)
    val worn_off_stab = find("bank:stabatt", 5260990222104296068)
    val worn_off_slash = find("bank:slashatt", 9076761768814315787)
    val worn_off_crush = find("bank:crushatt", 3669161278669559698)
    val worn_off_magic = find("bank:magicatt", 3202069859703581606)
    val worn_off_range = find("bank:rangeatt", 7017841406413601325)
    val worn_speed_base = find("bank:attackspeedbase", 2484495088051483370)
    val worn_speed = find("bank:attackspeedactual", 425574725650768906)
    val worn_def_stab = find("bank:stabdef", 2781295599284489243)
    val worn_def_slash = find("bank:slashdef", 6597067145994508962)
    val worn_def_crush = find("bank:crushdef", 1189466655849752873)
    val worn_def_range = find("bank:rangedef", 4538146783593794500)
    val worn_def_magic = find("bank:magicdef", 722375236883774781)
    val worn_melee_str = find("bank:meleestrength", 301600976464682418)
    val worn_ranged_str = find("bank:rangestrength", 4117372523174702137)
    val worn_magic_dmg = find("bank:magicdamage", 7933144069884721856)
    val worn_prayer = find("bank:prayer", 2525543579739965767)
    val worn_undead = find("bank:typemultiplier", 1637677900354895310)
    val worn_slayer = find("bank:slayermultiplier", 5453449447064915029)
    val tutorial_overlay_target = find("bank:bank_highlight", 5345750290142623060)
    val confirmation_overlay_target = find("bank:popup", 7725483948440596985)
    val tooltip = find("bank:tooltip", 4370591860360031648)

    val rearrange_mode_swap = find("bank:swap", 7319232678072104634)
    val rearrange_mode_insert = find("bank:insert", 6869961168519985648)
    val withdraw_mode_item = find("bank:item", 2510729662576342869)
    val withdraw_mode_note = find("bank:note", 3245570119814374912)
    val always_placehold = find("bank:placeholder", 5373906735621325153)
    val deposit_inventory = find("bank:depositinv", 2621356993398576010)
    val deposit_worn = find("bank:depositworn", 4616617229019892865)
    val quantity_1 = find("bank:quantity1", 7134228484220256887)
    val quantity_5 = find("bank:quantity5", 3565830576145944234)
    val quantity_10 = find("bank:quantity10", 585247277110118155)
    val quantity_x = find("bank:quantityx", 6455702818875624943)
    val quantity_all = find("bank:quantityall", 2264218406509099907)

    val incinerator_toggle = find("bank:incinerator_toggle", 960780673603192134)
    val tutorial_button_toggle = find("bank:banktut_toggle", 1837419760172867825)
    val inventory_item_options_toggle = find("bank:sideops_toggle", 5357236080421197393)
    val deposit_inv_toggle = find("bank:depositinv_toggle", 8877052400669526960)
    val deposit_worn_toggle = find("bank:depositworn_toggle", 8877052400669526961)
    val release_placehold = find("bank:release_placeholders", 8149480413880982160)
    val bank_fillers_1 = find("bank:bank_filler_1", 7857846992199901205)
    val bank_fillers_10 = find("bank:bank_filler_10", 934209463580439098)
    val bank_fillers_50 = find("bank:bank_filler_50", 500817472611912619)
    val bank_fillers_x = find("bank:bank_filler_x", 7532433140407231430)
    val bank_fillers_all = find("bank:bank_filler_all", 2462849190149319577)
    val bank_fillers_fill = find("bank:bank_filler_confirm", 5934725623241639992)
    val bank_tab_display = find("bank:dropdown_content", 7500650136019714139)

    val side_inventory = find("bank_inventory:items", 1885880344080200061)
    val worn_inventory = find("bank_inventory:wornops", 6203990611586493264)
    val lootingbag_inventory = find("bank_inventory:lootingbag_items", 8800055068705330501)
    val league_inventory = find("bank_inventory:league_secondinv_items", 81253577765913503)
    val bankside_highlight = find("bank_inventory:bankside_highlight", 6202930921141607001)

    val tutorial_close_button = find("bank_tutorial:pausebutton", 8373824249352593324)
    val tutorial_next_page = find("bank_tutorial:continue", 2368578001968595651)
    val tutorial_prev_page = find("bank_tutorial:previous", 7461125518300620858)
}

@Suppress("ConstPropertyName")
object BankSubComponents {
    const val main_tab = 10
    val other_tabs = 11..19

    val tab_extended_slots_offset = 19..28
}
