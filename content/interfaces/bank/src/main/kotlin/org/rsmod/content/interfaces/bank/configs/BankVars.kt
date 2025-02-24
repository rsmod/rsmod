package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.type.builders.varbit.VarBitBuilder
import org.rsmod.api.type.builders.varp.VarpBuilder
import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.api.type.refs.varp.VarpReferences

internal typealias bank_varbits = BankVarBits

object BankVarBits : VarBitReferences() {
    val rearrange_mode = find("bank_rearrange_mode", 15663065870705)
    val withdraw_mode = find("bank_withdraw_mode", 5925715116475)
    val placeholders = find("bank_always_set_placeholder", 54251826376568)
    val last_quantity_input = find("bank_last_deposit_amount", 15663065876318)
    val left_click_quantity = find("bank_left_click_amount", 85833815760704)
    val bank_filler_quantity = find("bank_filler_quantity_option", 49821074180449)
    val tab_display = find("bank_display_type", 44669036734552)
    val incinerator = find("bank_incinerator_button", 54200306011118)
    val tutorial_button = find("bank_tutorial_button", 58785619319353)
    val inventory_item_options = find("bank_inventory_item_options", 58785619281276)
    val deposit_inventory_button = find("bank_deposit_inventory_items_button", 58785619245511)
    val deposit_worn_items_button = find("bank_deposit_worn_items_button", 54251826381959)
    val always_deposit_to_potion_store = find("bank_always_deposit_to_potion_store", 92376903411390)
    val tutorial_current_page = find("bank_tutorial_current_page", 138642199507322)
    val tutorial_total_pages = find("bank_tutorial_total_pages", 138642199530015)

    val tab_size1 = find("bank_tab_size_1", 44669036636892)
    val tab_size2 = find("bank_tab_size_2", 44669036686059)
    val tab_size3 = find("bank_tab_size_3", 54200305893679)
    val tab_size4 = find("bank_tab_size_4", 54200305942846)
    val tab_size5 = find("bank_tab_size_5", 54251826268042)
    val tab_size6 = find("bank_tab_size_6", 54251826317209)
    val tab_size7 = find("bank_tab_size_7", 92376903295184)
    val tab_size8 = find("bank_tab_size_8", 92376903344351)
    val tab_size9 = find("bank_tab_size_9", 193202275919663)
    val tab_size_main = find("bank_tab_size_main")

    val selected_tab = find("bank_selected_tab", 5925715131978)

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
