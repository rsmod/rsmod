package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

internal typealias bank_interfaces = BankInterfaces

internal typealias bank_components = BankComponents

internal typealias bank_comsubs = BankSubComponents

object BankInterfaces : InterfaceReferences() {
    val tutorial_overlay = find("screenhighlight", 1206696351)
}

object BankComponents : ComponentReferences() {
    val tutorial_button = find("bankmain:bank_tut", 7135446288139269005)
    val capacity_container = find("bankmain:capacity_layer", 5691092119508665688)
    val capacity_text = find("bankmain:capacity", 71329117100551008)
    val main_inventory = find("bankmain:items", 8484261777138475560)
    val tabs = find("bankmain:tabs", 5066672266370361425)
    val incinerator_confirm = find("bankmain:incinerator_confirm", 7492851284321215925)
    val potionstore_items = find("bankmain:potionstore_items", 801223983307344610)
    val worn_off_stab = find("bankmain:stabatt", 5260990222104296068)
    val worn_off_slash = find("bankmain:slashatt", 9076761768814315787)
    val worn_off_crush = find("bankmain:crushatt", 3669161278669559698)
    val worn_off_magic = find("bankmain:magicatt", 3202069859703581606)
    val worn_off_range = find("bankmain:rangeatt", 7017841406413601325)
    val worn_speed_base = find("bankmain:attackspeedbase", 2484495088051483370)
    val worn_speed = find("bankmain:attackspeedactual", 425574725650768906)
    val worn_def_stab = find("bankmain:stabdef", 2781295599284489243)
    val worn_def_slash = find("bankmain:slashdef", 6597067145994508962)
    val worn_def_crush = find("bankmain:crushdef", 1189466655849752873)
    val worn_def_range = find("bankmain:rangedef", 4538146783593794500)
    val worn_def_magic = find("bankmain:magicdef", 722375236883774781)
    val worn_melee_str = find("bankmain:meleestrength", 301600976464682418)
    val worn_ranged_str = find("bankmain:rangestrength", 4117372523174702137)
    val worn_magic_dmg = find("bankmain:magicdamage", 7933144069884721856)
    val worn_prayer = find("bankmain:prayer", 2525543579739965767)
    val worn_undead = find("bankmain:typemultiplier", 1637677900354895310)
    val worn_slayer = find("bankmain:slayermultiplier", 5453449447064915029)
    val tutorial_overlay_target = find("bankmain:bank_highlight", 5345750290142623060)
    val confirmation_overlay_target = find("bankmain:popup", 7725483948440596985)
    val tooltip = find("bankmain:tooltip", 4370591860360031648)

    val rearrange_mode_swap = find("bankmain:swap", 7319232678072104634)
    val rearrange_mode_insert = find("bankmain:insert", 6869961168519985648)
    val withdraw_mode_item = find("bankmain:item", 2510729662576342869)
    val withdraw_mode_note = find("bankmain:note", 3245570119814374912)
    val always_placehold = find("bankmain:placeholder", 5373906735621325153)
    val deposit_inventory = find("bankmain:depositinv", 2621356993398576010)
    val deposit_worn = find("bankmain:depositworn", 4616617229019892865)
    val quantity_1 = find("bankmain:quantity1", 7134228484220256887)
    val quantity_5 = find("bankmain:quantity5", 3565830576145944234)
    val quantity_10 = find("bankmain:quantity10", 585247277110118155)
    val quantity_x = find("bankmain:quantityx", 6455702818875624943)
    val quantity_all = find("bankmain:quantityall", 2264218406509099907)

    val incinerator_toggle = find("bankmain:incinerator_toggle", 960780673603192134)
    val tutorial_button_toggle = find("bankmain:banktut_toggle", 1837419760172867825)
    val inventory_item_options_toggle = find("bankmain:sideops_toggle", 5357236080421197393)
    val deposit_inv_toggle = find("bankmain:depositinv_toggle", 8877052400669526960)
    val deposit_worn_toggle = find("bankmain:depositworn_toggle", 8877052400669526961)
    val release_placehold = find("bankmain:release_placeholders", 8149480413880982160)
    val bank_fillers_1 = find("bankmain:bank_filler_1", 7857846992199901205)
    val bank_fillers_10 = find("bankmain:bank_filler_10", 934209463580439098)
    val bank_fillers_50 = find("bankmain:bank_filler_50", 500817472611912619)
    val bank_fillers_x = find("bankmain:bank_filler_x", 7532433140407231430)
    val bank_fillers_all = find("bankmain:bank_filler_all", 2462849190149319577)
    val bank_fillers_fill = find("bankmain:bank_filler_confirm", 5934725623241639992)
    val bank_tab_display = find("bankmain:dropdown_content", 7500650136019714139)

    val side_inventory = find("bankside:items", 1885880344080200061)
    val worn_inventory = find("bankside:wornops", 6203990611586493264)
    val lootingbag_inventory = find("bankside:lootingbag_items", 8800055068705330501)
    val league_inventory = find("bankside:league_secondinv_items", 81253577765913503)
    val bankside_highlight = find("bankside:bankside_highlight", 6202930921141607001)

    val tutorial_close_button = find("screenhighlight:pausebutton", 8373824249352593324)
    val tutorial_next_page = find("screenhighlight:continue", 2368578001968595651)
    val tutorial_prev_page = find("screenhighlight:previous", 7461125518300620858)
}

@Suppress("ConstPropertyName")
object BankSubComponents {
    const val main_tab = 10
    val other_tabs = 11..19

    val tab_extended_slots_offset = 19..28
}
