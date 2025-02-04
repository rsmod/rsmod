package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.type.builders.varbit.VarBitBuilder
import org.rsmod.api.type.builders.varp.VarpBuilder
import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.api.type.refs.varp.VarpReferences

internal typealias bank_varbits = BankVarBits

object BankVarBits : VarBitReferences() {
    val rearrange_mode = find("bank_rearrange_mode", 898617779)
    val withdraw_mode = find("bank_withdraw_mode", 898390798)
    val placeholders = find("bank_always_set_placeholder", 852423333)
    val last_quantity_input = find("bank_last_deposit_amount", 898850372)
    val left_click_quantity = find("bank_left_click_amount", 1495812476)
    val bank_filler_quantity = find("bank_filler_quantity_option", 1425773740)
    val tab_display = find("bank_display_type", 946609163)
    val incinerator = find("bank_incinerator_button", 1158174304)
    val tutorial_button = find("bank_tutorial_button", 2346177730)
    val inventory_item_options = find("bank_inventory_item_options", 2287805793)
    val deposit_inventory_button = find("bank_deposit_inventory_items_button", 1895775568)
    val deposit_worn_items_button = find("bank_deposit_worn_items_button", 1217639544)
    val always_deposit_to_potion_store = find("bank_always_deposit_to_potion_store", 2596091375)
    val tutorial_current_page = find("bank_tutorial_current_page", 2339750709)
    val tutorial_total_pages = find("bank_tutorial_total_pages", 2340000382)

    val tab_size1 = find("bank_tab_size_1", 946738483)
    val tab_size2 = find("bank_tab_size_2", 947014630)
    val tab_size3 = find("bank_tab_size_3", 947192445)
    val tab_size4 = find("bank_tab_size_4", 947468592)
    val tab_size5 = find("bank_tab_size_5", 947646407)
    val tab_size6 = find("bank_tab_size_6", 947922554)
    val tab_size7 = find("bank_tab_size_7", 948100369)
    val tab_size8 = find("bank_tab_size_8", 948376516)
    val tab_size9 = find("bank_tab_size_9", 948554331)
    val tab_size_main = find("bank_tab_size_main")

    val selected_tab = find("bank_selected_tab", 941986461)

    val disable_ifevents = find("bank_disable_ifevents")
}

internal object BankVarBitBuilder : VarBitBuilder() {
    init {
        build("bank_tab_size_main") {
            baseVar = BankVarps.bank_serverside_vars
            startBit = 0
            endBit = 12
        }
        build("bank_disable_ifevents") {
            baseVar = BankVarps.bank_serverside_vars
            startBit = 13
            endBit = 13
        }
    }
}

object BankVarps : VarpReferences() {
    val bank_serverside_vars = find("bank_serverside_vars")
}

internal object BankVarpBuilder : VarpBuilder() {
    init {
        build("bank_serverside_vars")
    }
}
