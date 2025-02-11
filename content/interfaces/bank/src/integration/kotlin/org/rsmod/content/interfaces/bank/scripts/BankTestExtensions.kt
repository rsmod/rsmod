package org.rsmod.content.interfaces.bank.scripts

import org.rsmod.api.config.refs.invs
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.player.vars.enumVarp
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.testing.GameTestState
import org.rsmod.content.interfaces.bank.BankTab
import org.rsmod.content.interfaces.bank.configs.BankComponents
import org.rsmod.content.interfaces.bank.configs.BankSubComponents
import org.rsmod.content.interfaces.bank.configs.BankVarBits
import org.rsmod.content.interfaces.bank.openBank
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.interf.IfButtonOp

typealias bank_components = BankComponents

typealias bank_comsubs = BankSubComponents

typealias bank_varbits = BankVarBits

val SIDE_INV_DEPOSIT_1 = IfButtonOp.Op3
val SIDE_INV_DEPOSIT_5 = IfButtonOp.Op4
val SIDE_INV_DEPOSIT_X = IfButtonOp.Op7

var Player.bankTabSizeMain by intVarp(BankTab.Main.sizeVarBit)
var Player.bankTabSize1 by intVarp(BankTab.Tab1.sizeVarBit)
var Player.bankTabSize2 by intVarp(BankTab.Tab2.sizeVarBit)
var Player.bankTabSize3 by intVarp(BankTab.Tab3.sizeVarBit)
var Player.insertMode by boolVarp(bank_varbits.rearrange_mode)
var Player.selectedTab by enumVarp<BankTab>(bank_varbits.selected_tab)

fun GameTestState.bank(player: Player): Inventory {
    val bankType = cacheTypes.invs[invs.bank]
    return player.invMap.getOrPut(bankType)
}

fun GameTestState.openBank(player: Player): Inventory {
    val bank = bank(player)
    player.openBank(eventBus)
    return bank
}

fun BankTab.occupiedSpace(player: Player): Int {
    return player.vars[sizeVarBit]
}
