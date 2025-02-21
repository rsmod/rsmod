package org.rsmod.content.interfaces.bank.scripts

import jakarta.inject.Inject
import org.rsmod.api.combat.player.WeaponSpeeds
import org.rsmod.api.combat.player.WornBonuses
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.invs
import org.rsmod.api.player.output.ClientScripts.mesLayerClose
import org.rsmod.api.player.output.ClientScripts.tooltip
import org.rsmod.api.player.startInvTransmit
import org.rsmod.api.player.stopInvTransmit
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.script.onIfClose
import org.rsmod.api.script.onIfOpen
import org.rsmod.content.interfaces.bank.bankCapacity
import org.rsmod.content.interfaces.bank.configs.bank_components
import org.rsmod.content.interfaces.bank.configs.bank_comsubs
import org.rsmod.content.interfaces.bank.configs.bank_constants
import org.rsmod.content.interfaces.bank.configs.bank_interfaces
import org.rsmod.content.interfaces.bank.configs.bank_queues
import org.rsmod.content.interfaces.bank.configs.bank_varbits
import org.rsmod.content.interfaces.bank.disableIfEvents
import org.rsmod.content.interfaces.bank.highlightNoClickClear
import org.rsmod.content.interfaces.bank.setBankWornBonuses
import org.rsmod.content.interfaces.bank.setBanksideExtraOps
import org.rsmod.content.interfaces.bank.util.offset
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class BankOpenScript
@Inject
constructor(
    private val invTypes: InvTypeList,
    private val objTypes: ObjTypeList,
    private val wornBonuses: WornBonuses,
    private val weaponSpeeds: WeaponSpeeds,
) : PluginScript() {
    private val Player.bank
        get() = invMap.getOrPut(invTypes[invs.bank])

    private var Player.withdrawCert by boolVarBit(bank_varbits.withdraw_mode)

    override fun ScriptContext.startUp() {
        // `onBankOpen` occurs on `bank_side` trigger for emulation purposes.
        onIfOpen(interfaces.bank_side) { player.onBankOpen() }
        onIfClose(interfaces.bank_main) { player.onBankClose() }
    }

    private fun Player.onBankOpen() {
        if (!disableIfEvents) {
            val capacityIncrease = bank_constants.purchasable_capacity
            withdrawCert = false
            setBanksideExtraOps(objTypes)
            setBankIfEvents()
            setBankWornBonuses(wornBonuses, weaponSpeeds)
            ifSetText(bank_components.capacity_text, bankCapacity.toString())
            tooltip(
                this,
                "Members' capacity: ${bank_constants.default_capacity}<br>" +
                    "A banker can sell you up to $capacityIncrease more.",
                bank_components.capacity_container,
                bank_components.tooltip,
            )
        }

        startInvTransmit(bank)
    }

    private fun Player.onBankClose() {
        stopInvTransmit(bank)
        mesLayerClose(this, constants.meslayer_mode_objsearch)
        if (!ui.containsOverlay(bank_interfaces.tutorial_overlay)) {
            highlightNoClickClear()
        }
        queue(bank_queues.bank_compress, 1)
    }

    private fun Player.setBankIfEvents() {
        val lastIndex = bank.indices.last
        ifSetEvents(
            bank_components.main_inventory,
            bank.indices,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op6,
            IfEvent.Op7,
            IfEvent.Op8,
            IfEvent.Op9,
            IfEvent.Op10,
            IfEvent.Depth2,
            IfEvent.DragTarget,
        )
        ifSetEvents(bank_components.main_inventory, lastIndex + 10..lastIndex + 18, IfEvent.Op1)

        // When dragging an item to a tab beyond its current size, these are the subcomponent ids
        // the server will receive from the client.
        val extendedTabOffsets = bank_comsubs.tab_extended_slots_offset
        val extendedTabSlots = extendedTabOffsets.offset(lastIndex)
        ifSetEvents(bank_components.main_inventory, extendedTabSlots, IfEvent.DragTarget)

        ifSetEvents(
            bank_components.tabs,
            bank_comsubs.main_tab..bank_comsubs.main_tab,
            IfEvent.Op1,
            IfEvent.Op7,
            IfEvent.DragTarget,
        )
        ifSetEvents(
            bank_components.tabs,
            bank_comsubs.other_tabs,
            IfEvent.Op1,
            IfEvent.Op6,
            IfEvent.Op7,
            IfEvent.Depth1,
            IfEvent.DragTarget,
        )

        ifSetEvents(
            bank_components.side_inventory,
            inv.indices,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op6,
            IfEvent.Op7,
            IfEvent.Op8,
            IfEvent.Op9,
            IfEvent.Op10,
            IfEvent.Depth1,
            IfEvent.DragTarget,
        )
        ifSetEvents(
            bank_components.side_com11,
            inv.indices,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op6,
            IfEvent.Op7,
            IfEvent.Op10,
        )
        ifSetEvents(
            bank_components.side_com18,
            inv.indices,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op6,
            IfEvent.Op7,
            IfEvent.Op10,
        )
        ifSetEvents(
            bank_components.worn_inventory,
            inv.indices,
            IfEvent.Op1,
            IfEvent.Op9,
            IfEvent.Op10,
            IfEvent.Depth1,
            IfEvent.DragTarget,
        )

        ifSetEvents(bank_components.incinerator_confirm, 1..bank.size, IfEvent.Op1)
        ifSetEvents(bank_components.bank_tab_display, 0..8, IfEvent.Op1)
    }
}
