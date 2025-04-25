package org.rsmod.api.shops

import jakarta.inject.Inject
import org.rsmod.api.config.refs.currencies
import org.rsmod.api.player.output.ClientScripts.interfaceInvInit
import org.rsmod.api.player.output.ClientScripts.shopMainInit
import org.rsmod.api.player.startInvTransmit
import org.rsmod.api.player.ui.ifOpenMainSidePair
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.shops.config.ShopComponents
import org.rsmod.api.shops.config.ShopInterfaces
import org.rsmod.api.shops.config.ShopParams
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.shop.Shop
import org.rsmod.game.type.currency.CurrencyType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.inv.InvTypeList

public class Shops
@Inject
constructor(private val invTypes: InvTypeList, private val eventBus: EventBus) {
    public val globalInvs: MutableMap<InvType, Inventory> = mutableMapOf()

    public fun open(
        player: Player,
        activeNpc: Npc,
        title: String,
        shopInv: InvType,
        currency: CurrencyType = currencies.standard_gp,
        subtext: String = DEFAULT_SUBTEXT,
    ) {
        val buyPercentage = activeNpc.type.param(ShopParams.shop_buy_percentage) / 10.0
        val sellPercentage = activeNpc.type.param(ShopParams.shop_sell_percentage) / 10.0
        val changePercentage = activeNpc.type.param(ShopParams.shop_change_percentage) / 10.0
        open(
            player = player,
            title = title,
            shopInv = shopInv,
            buyPercentage = buyPercentage,
            sellPercentage = sellPercentage,
            changePercentage = changePercentage,
            currency = currency,
            subtext = subtext,
        )
    }

    public fun open(
        player: Player,
        title: String,
        shopInv: InvType,
        buyPercentage: Double,
        sellPercentage: Double,
        changePercentage: Double,
        currency: CurrencyType = currencies.standard_gp,
        subtext: String = DEFAULT_SUBTEXT,
    ) {
        val inv = shopInv.toInventory(player)
        open(
            player = player,
            title = title,
            shopInv = inv,
            sideInv = player.inv,
            currency = currency,
            buyPercentage = buyPercentage,
            sellPercentage = sellPercentage,
            changePercentage = changePercentage,
            subtext = subtext,
        )
    }

    public fun open(
        player: Player,
        title: String,
        shopInv: Inventory,
        sideInv: Inventory,
        currency: CurrencyType,
        buyPercentage: Double,
        sellPercentage: Double,
        changePercentage: Double,
        subtext: String,
    ) {
        player.openedShop = Shop(shopInv, currency, buyPercentage, sellPercentage, changePercentage)

        player.startInvTransmit(shopInv)
        player.startInvTransmit(sideInv)
        player.ifOpenMainSidePair(
            main = ShopInterfaces.shop_main,
            side = ShopInterfaces.shop_side,
            colour = -1,
            transparency = -1,
            eventBus = eventBus,
        )
        player.ifSetText(ShopComponents.shop_subtext, subtext)
        shopMainInit(player, shopInv.type, title)

        player.ifSetEvents(
            ShopComponents.shop_inv,
            1..shopInv.size,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op6,
            IfEvent.Op10,
        )

        interfaceInvInit(
            player = player,
            inv = sideInv,
            target = ShopComponents.shop_side_inv,
            objRowCount = 4,
            objColCount = 7,
            op1 = "Value<col=ff9040>",
            op2 = "Sell 1<col=ff9040>",
            op3 = "Sell 5<col=ff9040>",
            op4 = "Sell 10<col=ff9040>",
            op5 = "Sell 50<col=ff9040>",
        )
        player.ifSetEvents(
            ShopComponents.shop_side_inv,
            0 until sideInv.size,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
            IfEvent.Op10,
        )
    }

    private fun InvType.toInventory(observer: Player): Inventory {
        val unpacked = invTypes[this]
        return if (unpacked.scope == InvScope.Shared) {
            sharedInv()
        } else {
            privateInv(observer)
        }
    }

    private fun InvType.sharedInv(): Inventory = globalInvs.getOrPut(this) { createSharedInv() }

    private fun InvType.createSharedInv(): Inventory {
        val unpacked = invTypes[this]
        check(unpacked.scope == InvScope.Shared) {
            "`shopInv` must have shared scope. (shopInv=$unpacked)"
        }
        return Inventory.create(unpacked)
    }

    private fun InvType.privateInv(player: Player): Inventory {
        val unpacked = invTypes[this]
        check(unpacked.scope != InvScope.Shared) {
            "`shopInv` must not have shared scope. (shopInv=$unpacked)"
        }
        return player.invMap.getOrPut(unpacked)
    }

    public companion object {
        public const val DEFAULT_SUBTEXT: String =
            "Right click on shop to buy item - Right-click on inventory to sell item"
    }
}
