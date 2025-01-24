package org.rsmod.content.interfaces.equipment.prices

import jakarta.inject.Inject
import org.rsmod.api.config.refs.invs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.invtx.invMoveAll
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.output.ClientScripts.ifSetTextAlign
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.script.onIfClose
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.utils.format.formatAmount
import org.rsmod.content.interfaces.equipment.configs.equip_components
import org.rsmod.content.interfaces.equipment.configs.equip_interfaces
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class GuicePriceScript
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val eventBus: EventBus,
    private val protectedAccess: ProtectedAccessLauncher,
    private val marketPrices: MarketPrices,
) : PluginScript() {
    private val UnpackedObjType.price: Int
        get() = marketPrices[this] ?: 1

    override fun ScriptContext.startUp() {
        onIfOverlayButton(equip_components.guide_prices) { player.selectGuidePrices() }
        onIfModalButton(equip_components.guide_prices_add_all) { addAllFromInv() }
        onIfModalButton(equip_components.guide_prices_side_inv) { addFromSlot(it.comsub, it.op) }
        onIfModalButton(equip_components.guide_prices_main_inv) { takeFromSlot(it.comsub, it.op) }
        onIfModalButton(equip_components.guide_prices_search) { searchObj() }
        onIfClose(equip_interfaces.guide_prices_main) { player.closeGuide() }
    }

    private fun Player.selectGuidePrices() {
        ifClose(eventBus)
        protectedAccess.launch(this) { openGuide() }
    }

    private fun ProtectedAccess.openGuide() {
        invClear(tempInv)
        invTransmit(tempInv)
        invTransmit(inv)
        ifOpenMainSidePair(
            main = equip_interfaces.guide_prices_main,
            side = equip_interfaces.guide_prices_side,
        )

        player.updateGuidePrices()
        ifSetObj(equip_components.guide_prices_search_obj, objs.null_item_placeholder, zoom = 1)
        ifSetEvents(
            target = equip_components.guide_prices_main_inv,
            range = tempInv.indices,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op10,
        )
        interfaceInvInit(
            inv = inv,
            target = equip_components.guide_prices_side_inv,
            objRowCount = 4,
            objColCount = 7,
            op1 = "Add<col=ff9040>",
            op2 = "Add-5<col=ff9040>",
            op3 = "Add-10<col=ff9040>",
            op4 = "Add-All<col=ff9040>",
            op5 = "Add-X<col=ff9040>",
        )
        ifSetEvents(
            target = equip_components.guide_prices_side_inv,
            range = inv.indices,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op10,
        )
    }

    private fun Player.updateGuidePrices() {
        val tempInv =
            checkNotNull(invMap[invs.tempinv]) {
                "`tempInv` must be transmitted. (`startInvTransmit`)"
            }
        val prices = tempInv.toPriceList()
        updatePrices(prices)
        updateTotalPrice(prices)
    }

    private fun Player.updateTotalPrice(prices: List<Int>) {
        val total = prices.sum().formatAmount
        ifSetTextAlign(
            player = this,
            target = equip_components.guide_prices_total_price_text,
            alignH = 1,
            alignV = 1,
            lineHeight = 15,
        )
        ifSetText(
            target = equip_components.guide_prices_total_price_text,
            text = "Total guide price:<br><col=ffffff>$total</col>",
        )
    }

    private fun Player.updateSearchPrice(type: UnpackedObjType) {
        ifSetTextAlign(
            player = this,
            target = equip_components.guide_prices_total_price_text,
            alignH = 1,
            alignV = 1,
            lineHeight = 15,
        )
        ifSetText(
            target = equip_components.guide_prices_total_price_text,
            text = "${type.name}:<br><col=ffffff>${type.price.formatAmount} coins</col>",
        )
    }

    private fun Player.updatePrices(prices: List<Int>) {
        check(prices.size == 28) { "ClientScript takes 28 exact prices." }
        runClientScript(785, *prices.toTypedArray())
    }

    private fun Iterable<InvObj?>.toPriceList(): List<Int> {
        val prices = mutableListOf<Int>()
        for (obj in this) {
            if (obj == null) {
                prices += 0
                continue
            }
            val type = objTypes[obj]
            prices += type.price * obj.count
        }
        return prices
    }

    private suspend fun ProtectedAccess.searchObj() {
        val search = objDialog("Select an item to ask about its price:")
        ifSetObj(equip_components.guide_prices_search_obj, search, zoom = 1)
        player.updateSearchPrice(search)
    }

    private fun ProtectedAccess.addAllFromInv() {
        if (inv.isEmpty()) {
            mes("You have no items that can be checked.")
            soundSynth(synths.pillory_wrong)
            return
        }
        val untradableSlots = inv.mapSlots { slot, obj -> obj != null && !ocTradable(obj) }
        val transaction = invMoveInv(inv, tempInv, untradableSlots)

        if (transaction.noneCompleted()) {
            mes("You have items that cannot be traded.")
            soundSynth(synths.pillory_wrong)
            return
        }

        player.updateGuidePrices()
    }

    private suspend fun ProtectedAccess.addFromSlot(fromSlot: Int, op: IfButtonOp) {
        val obj = inv[fromSlot] ?: return
        if (op == IfButtonOp.Op10) {
            player.examine(obj)
            return
        }

        if (!ocTradable(obj)) {
            mes("You cannot trade that item.")
            return
        }

        val count = resolveCount(op) ?: error("Invalid op: $op (slot=$fromSlot)")
        invMoveFromSlot(from = inv, into = tempInv, fromSlot = fromSlot, count = count)
        player.updateGuidePrices()
    }

    private suspend fun ProtectedAccess.takeFromSlot(fromSlot: Int, op: IfButtonOp) {
        if (op == IfButtonOp.Op10) {
            val obj = tempInv[fromSlot] ?: return
            player.examine(obj)
            return
        }
        val count = resolveCount(op) ?: error("Invalid op: $op (slot=$fromSlot)")
        invMoveFromSlot(from = tempInv, into = inv, fromSlot = fromSlot, count = count)
        tempInv.compact()
        player.updateGuidePrices()
    }

    private suspend fun ProtectedAccess.resolveCount(op: IfButtonOp): Int? =
        when (op) {
            IfButtonOp.Op1 -> 1
            IfButtonOp.Op2 -> 5
            IfButtonOp.Op3 -> 10
            IfButtonOp.Op4 -> Int.MAX_VALUE
            IfButtonOp.Op5 -> countDialog()
            else -> null
        }

    private fun Player.closeGuide() {
        val tempInv = invMap[invs.tempinv] ?: return
        val result = invMoveAll(from = tempInv, into = inv)
        check(result.success) { "Could not move `tempInv` into `inv`: $tempInv" }
    }

    private fun Player.examine(obj: InvObj) {
        val type = objTypes[obj]
        objExamine(type, obj.count, type.price)
    }
}
