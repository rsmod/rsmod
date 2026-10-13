package org.runerealm.api.shops.operation

import org.runerealm.game.entity.Player
import org.runerealm.game.inv.Inventory
import org.runerealm.game.shop.Shop
import org.runerealm.game.type.interf.IfButtonOp

public interface ShopOperations {
    public fun shopInvOp(player: Player, sideInv: Inventory, shop: Shop, slot: Int, op: IfButtonOp)

    public fun sideInvOp(player: Player, sideInv: Inventory, shop: Shop, slot: Int, op: IfButtonOp)
}
