package org.rsmod.api.shops

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.output.ClientScripts.interfaceInvInit
import org.rsmod.api.player.output.ClientScripts.shopMainInit
import org.rsmod.api.player.output.ClientScripts.topLevelMainModalBackground
import org.rsmod.api.player.output.updateInvFull
import org.rsmod.api.player.ui.ifOpenMainSidePair
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.inv.InvTypeList

public class Shops
@Inject
constructor(private val invTypes: InvTypeList, private val eventBus: EventBus) {
    private val globalInvs = mutableMapOf<InvType, Inventory>()

    public fun open(
        player: Player,
        title: String,
        shopInv: InvType,
        subtext: String = DEFAULT_SUBTEXT,
    ): Unit =
        with(player) {
            val sharedInv = shopInv.sharedInv()
            updateInvFull(sharedInv)
            updateInvFull(player.inv)
            topLevelMainModalBackground()
            ifOpenMainSidePair(interfaces.shop_main, interfaces.shop_side, eventBus)
            ifSetText(components.shop_subtext, subtext)
            shopMainInit(shopInv, title)
            ifSetEvents(
                components.shop_inv,
                1..sharedInv.size,
                IfEvent.Op1,
                IfEvent.Op2,
                IfEvent.Op3,
                IfEvent.Op4,
                IfEvent.Op5,
                IfEvent.Op6,
                IfEvent.Op10,
            )
            interfaceInvInit(
                inv = player.inv,
                target = components.shop_side_inv,
                objRowCount = 4,
                objColCount = 7,
                op1 = "Value<col=ff9040>",
                op2 = "Sell 1<col=ff9040>",
                op3 = "Sell 5<col=ff9040>",
                op4 = "Sell 10<col=ff9040>",
                op5 = "Sell 50<col=ff9040>",
            )
            ifSetEvents(
                components.shop_side_inv,
                0 until player.inv.size,
                IfEvent.Op1,
                IfEvent.Op2,
                IfEvent.Op3,
                IfEvent.Op4,
                IfEvent.Op5,
                IfEvent.Op10,
            )
        }

    private fun InvType.sharedInv(): Inventory = globalInvs.getOrPut(this) { createInv() }

    private fun InvType.createInv(): Inventory {
        val unpacked = invTypes[this]
        check(unpacked.scope == InvScope.Shared) {
            "`shopInv` must have shared scope. (shopInv=$unpacked)"
        }
        return Inventory.create(unpacked)
    }

    public companion object {
        public const val DEFAULT_SUBTEXT: String =
            "Right click on shop to buy item - Right-click on inventory to sell item"
    }
}

public fun Player.openShop(
    shops: Shops,
    title: String,
    shopInv: InvType,
    subtext: String = Shops.DEFAULT_SUBTEXT,
): Unit = shops.open(this, title, shopInv, subtext)
