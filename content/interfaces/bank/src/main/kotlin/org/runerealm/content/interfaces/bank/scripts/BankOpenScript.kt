package org.runerealm.content.interfaces.bank.scripts

import jakarta.inject.Inject
import org.runerealm.api.combat.weapon.WeaponSpeeds
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.invs
import org.runerealm.api.player.bonus.WornBonuses
import org.runerealm.api.player.output.ClientScripts.mesLayerClose
import org.runerealm.api.player.output.ClientScripts.tooltip
import org.runerealm.api.player.startInvTransmit
import org.runerealm.api.player.stopInvTransmit
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.ui.ifSetText
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.script.onIfClose
import org.runerealm.api.script.onIfOpen
import org.runerealm.content.interfaces.bank.bankCapacity
import org.runerealm.content.interfaces.bank.configs.bank_components
import org.runerealm.content.interfaces.bank.configs.bank_comsubs
import org.runerealm.content.interfaces.bank.configs.bank_constants
import org.runerealm.content.interfaces.bank.configs.bank_interfaces
import org.runerealm.content.interfaces.bank.configs.bank_queues
import org.runerealm.content.interfaces.bank.configs.bank_varbits
import org.runerealm.content.interfaces.bank.disableIfEvents
import org.runerealm.content.interfaces.bank.highlightNoClickClear
import org.runerealm.content.interfaces.bank.setBankWornBonuses
import org.runerealm.content.interfaces.bank.setBanksideExtraOps
import org.runerealm.content.interfaces.bank.util.offset
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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

    override fun ScriptContext.startup() {
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
            // The client's `bankmain_build` redraws the title on every bank update; its text is
            // rebranded in the client cache (see `ClientTextOverrides`). Set it here as well so the
            // title is never left blank before (or without) the first rebuild.
            ifSetText(bank_components.title, bank_constants.title)
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
            bank_components.lootingbag_inventory,
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
            bank_components.league_inventory,
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
