package org.runerealm.content.interfaces.trade

import jakarta.inject.Inject
import org.runerealm.api.config.refs.invs
import org.runerealm.api.invtx.invTransaction
import org.runerealm.api.invtx.invTransfer
import org.runerealm.api.invtx.moveAll
import org.runerealm.api.invtx.select
import org.runerealm.api.market.MarketPrices
import org.runerealm.api.player.ironman.isIronman
import org.runerealm.api.player.output.ChatType
import org.runerealm.api.player.output.GameMessage
import org.runerealm.api.player.output.UpdateInventory.resendSlot
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.output.objExamine
import org.runerealm.api.player.output.runClientScript
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.startInvTransmit
import org.runerealm.api.player.stopInvTransmit
import org.runerealm.api.player.ui.ifClose
import org.runerealm.api.player.ui.ifCloseSub
import org.runerealm.api.player.ui.ifOpenMain
import org.runerealm.api.player.ui.ifOpenMainSidePair
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.ui.ifSetText
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.script.advanced.onOpPlayer4
import org.runerealm.api.script.onIfClose
import org.runerealm.api.script.onIfModalButton
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerLogout
import org.runerealm.api.utils.format.formatAmount
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.objtx.TransactionResult
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Player-to-player trading, following the OSRS Wiki
 * ([Trading](https://oldschool.runescape.wiki/w/Trading)):
 * - "Trade with" on another player sends "Sending trade offer..." to the requester and "<name>
 *   wishes to trade with you." to the other player, who accepts by trading back (clicking the
 *   request message does the same).
 * - A player with an interface open, or already trading, is "busy at the moment"; Ironmen "stand
 *   alone" and cannot trade.
 * - The first screen lets each player offer and remove objs from their inventory (1, 5, 10, all or
 *   X); any change to either offer resets both players' acceptance. Once both accept, the second
 *   (confirmation) screen opens, and once both accept that the objs are swapped atomically.
 * - Declining, closing the screen, walking away or logging out cancels the trade, returns every
 *   offered obj and tells the other player "Other player declined trade.".
 */
class TradeScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val invTypes: InvTypeList,
    private val marketPrices: MarketPrices,
) : PluginScript() {
    /** Pending requests, keyed by the requester, valued by who they asked to trade. */
    private val requests = HashMap<Player, Player>()
    private val sessions = HashMap<Player, TradeSession>()

    private val Player.offer: Inventory
        get() = invMap.getOrPut(invTypes[invs.tradeoffer])

    private var Player.thisPlayerRemoved by boolVarBit(trade_varbits.this_player_removed)
    private var Player.otherPlayerRemoved by boolVarBit(trade_varbits.other_player_removed)

    override fun ScriptContext.startup() {
        onOpPlayer4 { requestTrade(it.target) }

        onIfModalButton(trade_components.side_inventory) { sideInvOp(it.comsub, it.op) }
        onIfModalButton(trade_components.main_your_offer) { yourOfferOp(it.comsub, it.op) }
        onIfModalButton(trade_components.main_other_offer) { otherOfferOp(it.comsub, it.op) }
        onIfModalButton(trade_components.main_accept) { acceptOffer() }
        onIfModalButton(trade_components.main_decline) { player.ifClose(eventBus) }
        onIfModalButton(trade_components.confirm_accept) { acceptConfirm() }
        onIfModalButton(trade_components.confirm_decline) { player.ifClose(eventBus) }
        onIfModalButton(trade_components.confirm_close) { player.ifClose(eventBus) }

        onIfClose(trade_interfaces.main) { onScreenClosed(player, TradeStage.Offer) }
        onIfClose(trade_interfaces.confirm) { onScreenClosed(player, TradeStage.Confirm) }

        onPlayerLogin { player.returnOffer() }
        onPlayerLogout {
            requests.remove(player)
            requests.values.removeAll { it === player }
            cancel(player)
        }
    }

    /* Requests */

    private fun ProtectedAccess.requestTrade(target: Player) {
        if (player.isIronman) {
            mes(IRONMAN_SELF_MESSAGE)
            return
        }
        if (target.isIronman) {
            mes(ironmanTargetMessage(target))
            return
        }
        if (target.isTradeBusy() || player in sessions) {
            mes(BUSY_MESSAGE)
            return
        }
        if (requests[target] === player) {
            requests.remove(target)
            requests.remove(player)
            open(target, player)
            return
        }
        requests[player] = target
        mes(SENDING_MESSAGE)
        GameMessage.requestMes(
            player = target,
            text = "${player.displayName} wishes to trade with you.",
            name = player.displayName,
            type = ChatType.TradeReq,
        )
    }

    private fun Player.isTradeBusy(): Boolean = this in sessions || ui.modals.isNotEmpty()

    /* First screen */

    private fun open(first: Player, second: Player) {
        val firstSession = TradeSession(second)
        val secondSession = TradeSession(first)
        sessions[first] = firstSession
        sessions[second] = secondSession
        first.openMainScreen(second)
        second.openMainScreen(first)
    }

    private fun Player.openMainScreen(partner: Player) {
        ifClose(eventBus)
        returnOffer()
        thisPlayerRemoved = false
        otherPlayerRemoved = false
        runClientScript(TradeClientScripts.TRADE_PARTNER_SET, partner.displayName)
        ifOpenMainSidePair(trade_interfaces.main, trade_interfaces.side, -1, -1, eventBus)
        ifSetText(trade_components.main_title, "Trading With: ${partner.displayName}")
        ifSetText(trade_components.main_status, "")
        runClientScript(
            TradeClientScripts.INTERFACE_INV_INIT_BIG,
            trade_components.side_inventory.packed,
            inv.type.id,
            4,
            7,
            0,
            -1,
            "Offer",
            "Offer-5",
            "Offer-10",
            "Offer-All",
            "Offer-X",
            "Value",
            "",
            "",
            "",
        )
        ifSetEvents(
            trade_components.side_inventory,
            inv.indices,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op6,
            IfEvent.Op10,
        )
        ifSetEvents(
            trade_components.main_your_offer,
            offer.indices,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op10,
        )
        ifSetEvents(trade_components.main_other_offer, offer.indices, IfEvent.Op10)
        startInvTransmit(offer)
        updateOtherInv(partner.offer)
        refreshOfferTexts(partner)
    }

    /** Offer values in the headers and the partner's free inventory space. */
    private fun Player.refreshOfferTexts(partner: Player) {
        ifSetText(
            trade_components.main_your_offer_header,
            "Your offer:<br>(Value: <col=ffffff>${offer.value().formatAmount}</col> coins)",
        )
        ifSetText(
            trade_components.main_other_offer_header,
            "${partner.displayName} offers:<br>" +
                "(Value: <col=ffffff>${partner.offer.value().formatAmount}</col> coins)",
        )
        val free = partner.inv.freeSpace()
        val slots = if (free == 1) "slot" else "slots"
        ifSetText(
            trade_components.main_free_space,
            "${partner.displayName} has $free free<br>inventory $slots.",
        )
    }

    private suspend fun ProtectedAccess.sideInvOp(slot: Int, op: IfButtonOp) {
        if (op == IfButtonOp.Op10) {
            objExamine(inv, slot)
            return
        }
        val session = sessions[player] ?: return
        if (session.stage != TradeStage.Offer) {
            return
        }
        val obj = inv[slot] ?: return resendSlot(inv, 0)
        val type = objTypes[obj]
        if (op == IfButtonOp.Op6) {
            val normalized = objTypes.normalize(type)
            val price = (marketPrices[normalized] ?: 0).toLong() * obj.count
            mes("${normalized.name}: ${price.formatAmount} coins.")
            return
        }
        if (!type.tradeable || !objTypes[objTypes.uncert(obj)].tradeable) {
            mes(UNTRADEABLE_MESSAGE)
            return
        }
        val count =
            when (op) {
                IfButtonOp.Op1 -> 1
                IfButtonOp.Op2 -> 5
                IfButtonOp.Op3 -> 10
                IfButtonOp.Op4 -> Int.MAX_VALUE
                IfButtonOp.Op5 -> countDialog()
                else -> return
            }
        // `countDialog` suspends; the trade may have ended in the meantime.
        if (sessions[player] !== session || session.stage != TradeStage.Offer) {
            return
        }
        if (count <= 0 || inv[slot] == null) {
            return
        }
        val result = player.invTransfer(inv, slot, count, player.offer, strict = false)
        if (!result.success || result.completed() == 0) {
            return
        }
        onOfferChanged(player, session)
    }

    private suspend fun ProtectedAccess.yourOfferOp(slot: Int, op: IfButtonOp) {
        val offer = player.offer
        if (op == IfButtonOp.Op10) {
            objExamine(offer, slot)
            return
        }
        val session = sessions[player] ?: return
        if (session.stage != TradeStage.Offer) {
            return
        }
        offer[slot] ?: return resendSlot(offer, 0)
        val count =
            when (op) {
                IfButtonOp.Op1 -> 1
                IfButtonOp.Op2 -> 5
                IfButtonOp.Op3 -> 10
                IfButtonOp.Op4 -> Int.MAX_VALUE
                IfButtonOp.Op5 -> countDialog()
                else -> return
            }
        if (sessions[player] !== session || session.stage != TradeStage.Offer) {
            return
        }
        if (count <= 0 || offer[slot] == null) {
            return
        }
        val result = player.invTransfer(offer, slot, count, inv, strict = false)
        if (result[0] == TransactionResult.NotEnoughSpace) {
            mes("You don't have enough inventory space.")
            return
        }
        if (!result.success || result.completed() == 0) {
            return
        }
        player.compactOffer()
        val partner = session.partner
        player.thisPlayerRemoved = true
        partner.otherPlayerRemoved = true
        player.runClientScript(TradeClientScripts.TRADE_SLOT_CHANGED, 0, slot)
        partner.runClientScript(TradeClientScripts.TRADE_SLOT_CHANGED, 1, slot)
        onOfferChanged(player, session)
    }

    private fun ProtectedAccess.otherOfferOp(slot: Int, op: IfButtonOp) {
        if (op != IfButtonOp.Op10) {
            return
        }
        val partner = sessions[player]?.partner ?: return
        val obj = partner.offer[slot] ?: return
        val normalized = objTypes.normalize(objTypes[obj])
        player.objExamine(normalized, obj.count, marketPrices[normalized] ?: 0)
    }

    private fun Player.compactOffer() {
        val objs = offer.objs.filterNotNull()
        for (i in offer.indices) {
            val expected = objs.getOrNull(i)
            if (offer[i] != expected) {
                offer[i] = expected
            }
        }
    }

    /** Any change to either offer resets both players' acceptance. */
    private fun onOfferChanged(player: Player, session: TradeSession) {
        val partner = session.partner
        val partnerSession = sessions[partner] ?: return
        session.accepted = false
        partnerSession.accepted = false
        player.ifSetText(trade_components.main_status, "")
        partner.ifSetText(trade_components.main_status, "")
        partner.updateOtherInv(player.offer)
        player.refreshOfferTexts(partner)
        partner.refreshOfferTexts(player)
    }

    private fun ProtectedAccess.acceptOffer() {
        val session = sessions[player] ?: return
        if (session.stage != TradeStage.Offer || session.accepted) {
            return
        }
        val partner = session.partner
        val partnerSession = sessions[partner] ?: return
        if (!checkSpace(player, partner, trade_components.main_status)) {
            return
        }
        session.accepted = true
        if (!partnerSession.accepted) {
            player.ifSetText(trade_components.main_status, WAITING_MESSAGE)
            partner.ifSetText(trade_components.main_status, OTHER_ACCEPTED_MESSAGE)
            return
        }
        session.stage = TradeStage.Confirm
        partnerSession.stage = TradeStage.Confirm
        session.accepted = false
        partnerSession.accepted = false
        player.openConfirmScreen(partner)
        partner.openConfirmScreen(player)
    }

    /* Second screen */

    private fun Player.openConfirmScreen(partner: Player) {
        ifOpenMain(trade_interfaces.confirm, eventBus)
        ifCloseSub(trade_interfaces.side, eventBus)
        ifSetText(trade_components.confirm_title, CONFIRM_TITLE)
        ifSetText(
            trade_components.confirm_you_will_give,
            "You are about to give:<br>" +
                "(Value: <col=ffffff>${offer.value().formatAmount}</col> coins)",
        )
        ifSetText(
            trade_components.confirm_you_will_receive,
            "In return you will receive:<br>" +
                "(Value: <col=ffffff>${partner.offer.value().formatAmount}</col> coins)",
        )
        ifSetText(trade_components.confirm_opponent, "Trading with:<br>${partner.displayName}")
        updateOtherInv(partner.offer)
    }

    private fun ProtectedAccess.acceptConfirm() {
        val session = sessions[player] ?: return
        if (session.stage != TradeStage.Confirm || session.accepted) {
            return
        }
        val partner = session.partner
        val partnerSession = sessions[partner] ?: return
        if (!checkSpace(player, partner, trade_components.confirm_title)) {
            return
        }
        session.accepted = true
        if (!partnerSession.accepted) {
            player.ifSetText(trade_components.confirm_title, WAITING_MESSAGE)
            partner.ifSetText(trade_components.confirm_title, OTHER_ACCEPTED_MESSAGE)
            return
        }
        complete(player, partner)
    }

    private fun complete(player: Player, partner: Player) {
        if (!swapOffers(player, partner, commit = true)) {
            // Space was checked when accepting; this only fails if an inventory changed since.
            player.mes(NO_SPACE_MESSAGE)
            partner.mes(OTHER_NO_SPACE_MESSAGE)
            cancel(player, notifyPartner = false)
            return
        }
        sessions.remove(player)
        sessions.remove(partner)
        player.endTrade()
        partner.endTrade()
        player.mes(ACCEPTED_MESSAGE)
        partner.mes(ACCEPTED_MESSAGE)
    }

    /**
     * Swaps both offers into the other player's inventory in a single transaction, so either every
     * obj moves or nothing does. When [commit] is `false` this only checks that both inventories
     * have the space.
     */
    private fun swapOffers(player: Player, partner: Player, commit: Boolean): Boolean {
        val playerOffer = player.offer
        val partnerOffer = partner.offer
        var complete = false
        val result =
            player.invTransaction(playerOffer, null, autoCommit = false) {
                val fromPlayer = select(playerOffer)
                val fromPartner = select(partnerOffer)
                val intoPlayer = select(player.inv)
                val intoPartner = select(partner.inv)
                if (fromPlayer.image.any { it != null }) {
                    moveAll(from = fromPlayer, into = intoPartner)
                }
                if (fromPartner.image.any { it != null }) {
                    moveAll(from = fromPartner, into = intoPlayer)
                }
                complete =
                    fromPlayer.image.all { it == null } && fromPartner.image.all { it == null }
            }
        if (!complete || result.err != null) {
            return false
        }
        if (commit) {
            result.commitAll()
        }
        return true
    }

    /**
     * Checks that each player can hold what the other offered (their own offer will have left their
     * inventory). Shows the failure in [statusComponent] and the chatbox.
     */
    private fun checkSpace(
        player: Player,
        partner: Player,
        statusComponent: ComponentType,
    ): Boolean {
        if (swapOffers(player, partner, commit = false)) {
            return true
        }
        val playerHasSpace = canReceive(player, partner)
        val message = if (!playerHasSpace) NO_SPACE_MESSAGE else OTHER_NO_SPACE_MESSAGE
        player.ifSetText(statusComponent, message)
        player.mes(message)
        return false
    }

    /** `true` if [player] has the space for [partner]'s offer once their own has left. */
    private fun canReceive(player: Player, partner: Player): Boolean {
        val partnerOffer = partner.offer
        var complete = false
        player.invTransaction(partnerOffer, null, autoCommit = false) {
            val fromPartner = select(partnerOffer)
            val intoPlayer = select(player.inv)
            if (fromPartner.image.any { it != null }) {
                moveAll(from = fromPartner, into = intoPlayer)
            }
            complete = fromPartner.image.all { it == null }
        }
        return complete
    }

    /* Cancelling */

    private fun onScreenClosed(player: Player, stage: TradeStage) {
        val session = sessions[player] ?: return
        // Moving from the first to the second screen replaces the first screen's modal.
        if (session.stage != stage) {
            return
        }
        cancel(player)
    }

    /**
     * Cancels [player]'s trade: both offers go back to their owners' inventories and both trade
     * screens close. The other player is told "Other player declined trade.".
     */
    private fun cancel(player: Player, notifyPartner: Boolean = true) {
        val session = sessions.remove(player) ?: return
        val partner = session.partner
        sessions.remove(partner)
        player.returnOffer()
        partner.returnOffer()
        player.endTrade()
        partner.endTrade()
        partner.ifClose(eventBus)
        if (notifyPartner) {
            partner.mes(DECLINED_MESSAGE)
        }
    }

    private fun Player.endTrade() {
        ifCloseSub(trade_interfaces.main, eventBus)
        ifCloseSub(trade_interfaces.side, eventBus)
        ifCloseSub(trade_interfaces.confirm, eventBus)
        stopInvTransmit(offer)
        runClientScript(TradeClientScripts.TRADE_PARTNER_SET, "")
    }

    /** Moves anything left in the trade offer back into the inventory. */
    private fun Player.returnOffer() {
        val offer = offer
        if (offer.isEmpty()) {
            return
        }
        invTransaction(offer, null) {
            val from = select(offer)
            val into = select(inv)
            moveAll(from = from, into = into)
        }
    }

    private fun Inventory.value(): Long {
        var total = 0L
        for (obj in this) {
            obj ?: continue
            val normalized = objTypes.normalize(objTypes[obj])
            total += (marketPrices[normalized] ?: 0).toLong() * obj.count
        }
        return total
    }

    private fun ironmanTargetMessage(target: Player): String {
        val pronoun =
            when (target.appearance.pronoun) {
                0 -> "He"
                1 -> "She"
                else -> "They"
            }
        return "${target.displayName} is an Ironman. $pronoun stands alone."
    }

    internal companion object {
        const val SENDING_MESSAGE = "Sending trade offer..."
        const val BUSY_MESSAGE = "Other player is busy at the moment."
        const val DECLINED_MESSAGE = "Other player declined trade."
        const val IRONMAN_SELF_MESSAGE = "You are an Ironman. You stand alone."
        const val UNTRADEABLE_MESSAGE = "You can't trade this item."
        const val WAITING_MESSAGE = "Waiting for other player..."
        const val OTHER_ACCEPTED_MESSAGE = "Other player has accepted."
        const val CONFIRM_TITLE = "Are you sure you want to make this trade?"
        const val NO_SPACE_MESSAGE = "You don't have enough inventory space for this trade."
        const val OTHER_NO_SPACE_MESSAGE =
            "Other player doesn't have enough inventory space for this trade."
        const val ACCEPTED_MESSAGE = "Accepted trade."
    }
}

internal enum class TradeStage {
    Offer,
    Confirm,
}

internal class TradeSession(val partner: Player) {
    var stage: TradeStage = TradeStage.Offer
    var accepted: Boolean = false
}
