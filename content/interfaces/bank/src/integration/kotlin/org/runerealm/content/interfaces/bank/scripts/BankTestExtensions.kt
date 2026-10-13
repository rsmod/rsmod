package org.runerealm.content.interfaces.bank.scripts

import org.runerealm.api.config.refs.invs
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.player.vars.enumVarBit
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.interfaces.bank.BankTab
import org.runerealm.content.interfaces.bank.configs.BankComponents
import org.runerealm.content.interfaces.bank.configs.BankSubComponents
import org.runerealm.content.interfaces.bank.configs.BankVarBits
import org.runerealm.content.interfaces.bank.openBank
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.interf.IfButtonOp

typealias bank_components = BankComponents

typealias bank_comsubs = BankSubComponents

typealias bank_varbits = BankVarBits

val SIDE_INV_DEPOSIT_1 = IfButtonOp.Op3
val SIDE_INV_DEPOSIT_5 = IfButtonOp.Op4
val SIDE_INV_DEPOSIT_X = IfButtonOp.Op7
val SIDE_INV_DEPOSIT_ALL = IfButtonOp.Op8

val MAIN_INV_WITHDRAW_1 = IfButtonOp.Op2
val MAIN_INV_WITHDRAW_5 = IfButtonOp.Op3
val MAIN_INV_WITHDRAW_X = IfButtonOp.Op6
val MAIN_INV_WITHDRAW_ALL = IfButtonOp.Op7

var Player.bankTabSizeMain by intVarBit(BankTab.Main.sizeVarBit)
var Player.bankTabSize1 by intVarBit(BankTab.Tab1.sizeVarBit)
var Player.bankTabSize2 by intVarBit(BankTab.Tab2.sizeVarBit)
var Player.bankTabSize3 by intVarBit(BankTab.Tab3.sizeVarBit)
var Player.insertMode by boolVarBit(bank_varbits.rearrange_mode)
var Player.withdrawCert by boolVarBit(bank_varbits.withdraw_mode)
var Player.selectedTab by enumVarBit<BankTab>(bank_varbits.selected_tab)

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
