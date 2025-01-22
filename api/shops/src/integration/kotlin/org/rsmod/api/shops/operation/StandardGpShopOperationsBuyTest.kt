package org.rsmod.api.shops.operation

import net.rsprot.protocol.game.outgoing.misc.player.MessageGame
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.config.Constants
import org.rsmod.api.config.refs.currencies
import org.rsmod.api.config.refs.objs
import org.rsmod.api.shops.ShopScript
import org.rsmod.api.shops.Shops
import org.rsmod.api.shops.config.ShopComponents
import org.rsmod.api.shops.config.ShopInterfaces
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.scope.GameTestScope
import org.rsmod.api.type.script.dsl.InvPluginBuilder
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.shop.Shop
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.obj.ObjType

class StandardGpShopOperationsBuyTest {
    @Test
    fun GameTestState.`buy single obj successfully`() =
        runGameTest(ShopScript::class) {
            val shop = openGeneralShop()

            player.inv[0] = InvObj(objs.coins, 5)
            shop.inv[0] = InvObj(objs.newcomer_map, 5)

            buyStock(shop, objs.newcomer_map, OP_BUY1)

            assertEquals(1, player.count(objs.newcomer_map))
            assertEquals(4, player.count(objs.coins))
            assertTrue(client.hasNone<MessageGame>())
        }

    @Test
    fun GameTestState.`buy obj fails with no currency`() =
        runGameTest(ShopScript::class) {
            val shop = openGeneralShop()
            shop.inv[0] = InvObj(objs.newcomer_map, 5)
            buyStock(shop, objs.newcomer_map, OP_BUY1)
            assertMessageSent("You don't have enough coins.")
        }

    @Test
    fun GameTestState.`buy obj fails with no currency or stock available`() =
        runGameTest(ShopScript::class) {
            val shop = openGeneralShop()
            shop.inv[0] = InvObj(objs.newcomer_map, 0)
            buyStock(shop, objs.newcomer_map, OP_BUY1)
            assertMessageSent("That item is currently out of stock.")
        }

    @Test
    fun GameTestState.`buy 5 objs with limited inventory space`() =
        runGameTest(ShopScript::class) {
            val shop = openGeneralShop()

            player.fillInv()
            player.inv[0] = null
            player.inv[1] = InvObj(objs.coins, 6)
            shop.inv[0] = InvObj(objs.newcomer_map, 5)

            buyStock(shop, objs.newcomer_map, OP_BUY5)

            assertEquals(1, player.count(objs.newcomer_map))
            assertEquals(4, shop.inv.count(objs.newcomer_map))
            assertMessageSent("You don't have enough inventory space.")
        }

    @Test
    fun GameTestState.`buy 50 objs with sufficient space and currency`() =
        runGameTest(ShopScript::class) {
            val shop = openGeneralShop()

            player.fillInv()
            player.inv[0] = null
            player.inv[1] = null
            player.inv[2] = null
            player.inv[3] = null
            player.inv[4] = null
            player.inv[5] = InvObj(objs.coins, 5)
            shop.inv[0] = InvObj(objs.newcomer_map, 5)

            buyStock(shop, objs.newcomer_map, OP_BUY50)

            assertEquals(0, shop.inv.count(objs.newcomer_map))
            assertEquals(5, player.count(objs.newcomer_map))
            assertTrue(client.hasNone<MessageGame>())
        }

    @Test
    fun GameTestState.`buy 50 objs with no space and insufficient currency`() =
        runGameTest(ShopScript::class) {
            val shop = openGeneralShop()

            player.fillInv()
            player.inv[5] = InvObj(objs.coins, 2)
            shop.inv[0] = InvObj(objs.sos_security_book, 5)

            buyStock(shop, objs.sos_security_book, OP_BUY50)

            assertEquals(4, shop.inv.count(objs.sos_security_book))
            assertEquals(1, player.count(objs.sos_security_book))
            assertMessageSent("You don't have enough coins.")
        }

    @Test
    fun GameTestState.`buy all available stock successfully`() =
        runGameTest(ShopScript::class) {
            val shop = openGeneralShop()

            player.inv[0] = InvObj(objs.coins, Int.MAX_VALUE)
            shop.inv[0] = InvObj(objs.newcomer_map, 5)

            buyStock(shop, objs.newcomer_map, OP_BUY50)

            assertEquals(0, shop.inv.count(objs.newcomer_map))
            assertEquals(5, player.count(objs.newcomer_map))
            assertTrue(client.hasNone<MessageGame>())
        }

    private fun GameTestScope.buyStock(shop: Shop, obj: ObjType, op: IfButtonOp) {
        val slot = shop.inv.indexOfFirst { it?.id == obj.id }
        check(slot != -1) { "Obj not found in stock: obj=$obj, stock=${shop.inv}" }

        val obj = shop.inv.getValue(slot)
        player.ifButton(ShopComponents.shop_inv, comsub = slot + 1, obj = obj.id, op = op)
    }

    private fun GameTestScope.openGeneralShop(): Shop {
        val inventory = createShopInv()
        val invTypes = InvTypeList(mutableMapOf(inventory.type.id to inventory.type))
        val shops = Shops(invTypes, eventBus)

        shops.open(
            player = player,
            title = "",
            shopInv = inventory,
            sideInv = player.inv,
            currency = currencies.standard_gp,
            buyPercentage = 40.0,
            sellPercentage = 130.0,
            changePercentage = 3.0,
            subtext = "",
        )

        // Clear out the player's outgoing messages from opening shop.
        client.clear()

        check(player.ui.containsModal(ShopInterfaces.shop_main))
        check(player.ui.containsModal(ShopInterfaces.shop_side))
        val shop = checkNotNull(player.openedShop)
        return shop
    }

    private fun createShopInv(): Inventory {
        val builder =
            InvPluginBuilder().apply {
                internal = "test_shop"
                scope = InvScope.Shared
                stack = InvStackType.Always
                size = Constants.shop_default_size
                restock = true
                allStock = true
            }
        val type = builder.build(-1)
        return Inventory.create(type)
    }

    private companion object {
        private val OP_BUY1 = IfButtonOp.Op2
        private val OP_BUY5 = IfButtonOp.Op3
        private val OP_BUY50 = IfButtonOp.Op5
    }
}
