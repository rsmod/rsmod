package org.rsmod.content.interfaces.bank.scripts

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onIfModalButton
import org.rsmod.content.interfaces.bank.BankFillerMode
import org.rsmod.content.interfaces.bank.QuantityMode
import org.rsmod.content.interfaces.bank.TabDisplayMode
import org.rsmod.content.interfaces.bank.alwaysPlacehold
import org.rsmod.content.interfaces.bank.bankFillerMode
import org.rsmod.content.interfaces.bank.configs.bank_components
import org.rsmod.content.interfaces.bank.depositInvButton
import org.rsmod.content.interfaces.bank.depositWornButton
import org.rsmod.content.interfaces.bank.incinerator
import org.rsmod.content.interfaces.bank.insertMode
import org.rsmod.content.interfaces.bank.invItemOptions
import org.rsmod.content.interfaces.bank.lastQtyInput
import org.rsmod.content.interfaces.bank.leftClickQtyMode
import org.rsmod.content.interfaces.bank.setBanksideExtraOps
import org.rsmod.content.interfaces.bank.tabDisplayMode
import org.rsmod.content.interfaces.bank.tutorialButton
import org.rsmod.content.interfaces.bank.withdrawCert
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class BankSettingsScript
@Inject
constructor(private val bankScript: BankInvScript, private val objTypes: ObjTypeList) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        val comps = bank_components

        onIfModalButton(comps.rearrange_mode_swap) { insertMode = false }
        onIfModalButton(comps.rearrange_mode_insert) { insertMode = true }
        onIfModalButton(comps.withdraw_mode_item) { withdrawCert = false }
        onIfModalButton(comps.withdraw_mode_note) { withdrawCert = true }
        onIfModalButton(comps.always_placehold) { alwaysPlacehold = !alwaysPlacehold }
        onIfModalButton(comps.quantity_1) { leftClickQtyMode = QuantityMode.One }
        onIfModalButton(comps.quantity_5) { leftClickQtyMode = QuantityMode.Five }
        onIfModalButton(comps.quantity_10) { leftClickQtyMode = QuantityMode.Ten }
        onIfModalButton(comps.quantity_all) { leftClickQtyMode = QuantityMode.All }
        onIfModalButton(comps.quantity_x) { selectQuantityX(it.op) }

        onIfModalButton(comps.bank_tab_display) { selectTabDisplay(it.comsub) }
        onIfModalButton(comps.incinerator_toggle) { incinerator = !incinerator }
        onIfModalButton(comps.tutorial_button_toggle) { tutorialButton = !tutorialButton }
        onIfModalButton(comps.inventory_item_options_toggle) { toggleInvItemOptions() }
        onIfModalButton(comps.deposit_inv_toggle) { depositInvButton = !depositInvButton }
        onIfModalButton(comps.deposit_worn_toggle) { depositWornButton = !depositWornButton }
        onIfModalButton(comps.release_placehold) { selectReleasePlaceholders() }
        onIfModalButton(comps.bank_fillers_1) { bankFillerMode = BankFillerMode.One }
        onIfModalButton(comps.bank_fillers_10) { bankFillerMode = BankFillerMode.Ten }
        onIfModalButton(comps.bank_fillers_50) { bankFillerMode = BankFillerMode.Fifty }
        onIfModalButton(comps.bank_fillers_x) { bankFillerMode = BankFillerMode.X }
        onIfModalButton(comps.bank_fillers_all) { bankFillerMode = BankFillerMode.All }
        onIfModalButton(comps.bank_fillers_fill) { selectBankFillerFill() }
    }

    private suspend fun ProtectedAccess.selectQuantityX(op: IfButtonOp) {
        if (op == IfButtonOp.Op2) {
            lastQtyInput = countDialog("Enter amount:")
        }
        leftClickQtyMode = if (lastQtyInput == 0) QuantityMode.One else QuantityMode.X
    }

    private fun ProtectedAccess.selectTabDisplay(comsub: Int) {
        val mode =
            when (comsub) {
                1 -> TabDisplayMode.Obj
                3 -> TabDisplayMode.Digit
                5 -> TabDisplayMode.Roman
                else -> throw NotImplementedError("Unhandled tab display comsub selection: $comsub")
            }
        tabDisplayMode = mode
    }

    private fun ProtectedAccess.toggleInvItemOptions() {
        invItemOptions = !invItemOptions
        player.setBanksideExtraOps(objTypes)
    }

    private suspend fun ProtectedAccess.selectReleasePlaceholders() {
        val containsPlaceholder = bank.any { it != null && objTypes[it].isPlaceholder }
        if (containsPlaceholder) {
            bankScript.releasePlaceholders(this)
        }
    }

    private suspend fun ProtectedAccess.selectBankFillerFill() {
        val count = bankFillerMode.toCount()
        bankScript.addBankFillers(this, count)
    }

    private fun BankFillerMode.toCount(): Int? =
        when (this) {
            BankFillerMode.One -> 1
            BankFillerMode.Ten -> 10
            BankFillerMode.Fifty -> 50
            BankFillerMode.X -> null
            BankFillerMode.All -> Int.MAX_VALUE
        }
}
