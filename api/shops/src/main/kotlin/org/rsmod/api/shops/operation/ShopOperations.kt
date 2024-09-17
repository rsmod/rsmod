package org.rsmod.api.shops.operation

import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.shop.Shop
import org.rsmod.game.type.interf.IfButtonOp

public interface ShopOperations {
    public fun shopInvOp(player: Player, sideInv: Inventory, shop: Shop, slot: Int, op: IfButtonOp)

    public fun sideInvOp(player: Player, sideInv: Inventory, shop: Shop, slot: Int, op: IfButtonOp)
}
