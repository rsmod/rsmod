package org.rsmod.content.interfaces.bank

import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.enumVarBit
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.content.interfaces.bank.configs.bank_varbits
import org.rsmod.game.entity.Player

var ProtectedAccess.selectedTab by enumVarBit<BankTab>(bank_varbits.selected_tab)

var ProtectedAccess.insertMode by boolVarBit(bank_varbits.rearrange_mode)
var ProtectedAccess.withdrawCert by boolVarBit(bank_varbits.withdraw_mode)
var ProtectedAccess.alwaysPlacehold by boolVarBit(bank_varbits.placeholders)
var ProtectedAccess.lastQtyInput by intVarBit(bank_varbits.last_quantity_input)
var ProtectedAccess.leftClickQtyMode by enumVarBit<QuantityMode>(bank_varbits.left_click_quantity)

var ProtectedAccess.tabDisplayMode by enumVarBit<TabDisplayMode>(bank_varbits.tab_display)
var ProtectedAccess.incinerator by boolVarBit(bank_varbits.incinerator)
var ProtectedAccess.tutorialButton by boolVarBit(bank_varbits.tutorial_button)
var ProtectedAccess.invItemOptions by boolVarBit(bank_varbits.inventory_item_options)
var ProtectedAccess.depositInvButton by boolVarBit(bank_varbits.deposit_inventory_button)
var ProtectedAccess.depositWornButton by boolVarBit(bank_varbits.deposit_worn_items_button)
var ProtectedAccess.bankFillerMode by enumVarBit<BankFillerMode>(bank_varbits.bank_filler_quantity)

internal var Player.disableIfEvents by boolVarBit(bank_varbits.disable_ifevents)

val ProtectedAccess.bankCapacity by intVarBit(varbits.bank_capacity)
var Player.bankCapacity by intVarBit(varbits.bank_capacity)
