package org.rsmod.content.interfaces.bank

import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.player.vars.enumVarp
import org.rsmod.api.player.vars.intVarp
import org.rsmod.content.interfaces.bank.configs.bank_varbits
import org.rsmod.game.entity.Player

var ProtectedAccess.selectedTab by enumVarp<BankTab>(bank_varbits.selected_tab)

var ProtectedAccess.insertMode by boolVarp(bank_varbits.rearrange_mode)
var ProtectedAccess.withdrawCert by boolVarp(bank_varbits.withdraw_mode)
var ProtectedAccess.alwaysPlacehold by boolVarp(bank_varbits.placeholders)
var ProtectedAccess.lastQtyInput by intVarp(bank_varbits.last_quantity_input)
var ProtectedAccess.leftClickQtyMode by enumVarp<QuantityMode>(bank_varbits.left_click_quantity)

var ProtectedAccess.tabDisplayMode by enumVarp<TabDisplayMode>(bank_varbits.tab_display)
var ProtectedAccess.incinerator by boolVarp(bank_varbits.incinerator)
var ProtectedAccess.tutorialButton by boolVarp(bank_varbits.tutorial_button)
var ProtectedAccess.invItemOptions by boolVarp(bank_varbits.inventory_item_options)
var ProtectedAccess.depositInvButton by boolVarp(bank_varbits.deposit_inventory_button)
var ProtectedAccess.depositWornButton by boolVarp(bank_varbits.deposit_worn_items_button)
var ProtectedAccess.depositToPotionStore by boolVarp(bank_varbits.always_deposit_to_potion_store)
var ProtectedAccess.bankFillerMode by enumVarp<BankFillerMode>(bank_varbits.bank_filler_quantity)

internal var Player.disableIfEvents by boolVarp(bank_varbits.disable_ifevents)

val ProtectedAccess.bankCapacity by intVarp(varbits.bank_capacity)
var Player.bankCapacity by intVarp(varbits.bank_capacity)
