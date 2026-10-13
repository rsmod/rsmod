package org.runerealm.content.interfaces.trade

import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences

typealias trade_interfaces = TradeInterfaces

typealias trade_components = TradeComponents

typealias trade_varbits = TradeVarBits

object TradeInterfaces : InterfaceReferences() {
    val main = find("trademain")
    val side = find("tradeside")
    val confirm = find("tradeconfirm")
}

object TradeComponents : ComponentReferences() {
    val main_title = find("trademain:title")
    val main_status = find("trademain:status")
    val main_accept = find("trademain:accept")
    val main_decline = find("trademain:decline")
    val main_free_space = find("trademain:free_space_text")
    val main_your_offer_header = find("trademain:your_offer_header")
    val main_your_offer = find("trademain:your_offer")
    val main_other_offer_header = find("trademain:other_offer_header")
    val main_other_offer = find("trademain:other_offer")

    val side_inventory = find("tradeside:side_layer")

    val confirm_title = find("tradeconfirm:title")
    val confirm_accept = find("tradeconfirm:trade2accept")
    val confirm_decline = find("tradeconfirm:trade2decline")
    val confirm_close = find("tradeconfirm:com_32")
    val confirm_you_will_give = find("tradeconfirm:you_will_give")
    val confirm_you_will_receive = find("tradeconfirm:you_will_receive")
    val confirm_opponent = find("tradeconfirm:tradeopponent")
}

object TradeVarBits : VarBitReferences() {
    val this_player_removed = find("trade_this_player_removed")
    val other_player_removed = find("trade_other_player_removed")
}

/** Client script ids used by the trade screens (see the trade cs2 dumps). */
internal object TradeClientScripts {
    /** `interface_inv_init_big`: draws an inv on a layer with up to nine ops. */
    const val INTERFACE_INV_INIT_BIG = 150

    /** `trade_partner_set`: sets `%tradepartner` for the Trade chat filter. */
    const val TRADE_PARTNER_SET = 209

    /** `trade_slot_changed`: flashes the exclamation mark where an obj was removed. */
    const val TRADE_SLOT_CHANGED = 765
}
