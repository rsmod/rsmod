package org.rsmod.api.shops.operation

import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.shop.Shop
import org.rsmod.game.type.interf.IfButtonOp

public interface StandardShopOperations : ShopOperations {
    public fun examineShopValue(player: Player, shop: Shop, slot: Int)

    public fun shopBuy(player: Player, sideInv: Inventory, shop: Shop, slot: Int, request: Int)

    public fun examineInvValue(player: Player, sideInv: Inventory, shop: Shop, slot: Int)

    public fun invSell(player: Player, sideInv: Inventory, shop: Shop, slot: Int, request: Int)

    public fun examineDesc(player: Player, inv: Inventory, shop: Shop, slot: Int)

    override fun shopInvOp(
        player: Player,
        sideInv: Inventory,
        shop: Shop,
        slot: Int,
        op: IfButtonOp,
    ) {
        when (op) {
            IfButtonOp.Op1 -> examineShopValue(player, shop, slot)
            IfButtonOp.Op2 -> shopBuy(player, sideInv, shop, slot, request = 1)
            IfButtonOp.Op3 -> shopBuy(player, sideInv, shop, slot, request = 5)
            IfButtonOp.Op4 -> shopBuy(player, sideInv, shop, slot, request = 10)
            IfButtonOp.Op5 -> shopBuy(player, sideInv, shop, slot, request = 50)
            IfButtonOp.Op10 -> examineDesc(player, shop.inv, shop, slot)
            else -> return
        }
    }

    override fun sideInvOp(
        player: Player,
        sideInv: Inventory,
        shop: Shop,
        slot: Int,
        op: IfButtonOp,
    ) {
        when (op) {
            IfButtonOp.Op1 -> examineInvValue(player, sideInv, shop, slot)
            IfButtonOp.Op2 -> invSell(player, sideInv, shop, slot, request = 1)
            IfButtonOp.Op3 -> invSell(player, sideInv, shop, slot, request = 5)
            IfButtonOp.Op4 -> invSell(player, sideInv, shop, slot, request = 10)
            IfButtonOp.Op5 -> invSell(player, sideInv, shop, slot, request = 50)
            IfButtonOp.Op10 -> examineDesc(player, sideInv, shop, slot)
            else -> return
        }
    }
}
