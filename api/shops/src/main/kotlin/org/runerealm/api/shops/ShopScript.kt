package org.runerealm.api.shops

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.runerealm.api.config.refs.currencies
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stopInvTransmit
import org.runerealm.api.script.onIfClose
import org.runerealm.api.script.onIfModalButton
import org.runerealm.api.shops.config.ShopComponents
import org.runerealm.api.shops.config.ShopInterfaces
import org.runerealm.api.shops.operation.ShopOperationMap
import org.runerealm.api.shops.operation.ShopOperations
import org.runerealm.api.shops.operation.StandardGpShopOperations
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.shop.Shop
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.isAssociatedWith
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class ShopScript
@Inject
constructor(
    private val operationMap: ShopOperationMap,
    private val standardGpOperations: StandardGpShopOperations,
) : PluginScript() {
    private val logger = InlineLogger()

    override fun ScriptContext.startup() {
        registerDefaultCurrency()
        onIfModalButton(ShopComponents.shop_inv) { shopInvButton(it.comsub, it.op, it.obj) }
        onIfModalButton(ShopComponents.shop_side_inv) {
            shopSideInvButton(it.comsub, it.op, it.obj)
        }
        onIfClose(ShopInterfaces.shop_main) { player.closeShop() }
    }

    private fun registerDefaultCurrency() {
        operationMap.register(currencies.standard_gp, standardGpOperations)
    }

    private fun ProtectedAccess.shopInvButton(sub: Int, op: IfButtonOp, clientObj: ObjType?) {
        val objSlot = sub - 1
        val shop = player.openedShop ?: return
        val shopObj = shop.inv[objSlot] ?: return
        if (isClientObjInvalid(shopObj, clientObj)) {
            return
        }
        val operations = shop.operations() ?: return
        operations.shopInvOp(player, player.inv, shop, objSlot, op)
    }

    private fun ProtectedAccess.shopSideInvButton(sub: Int, op: IfButtonOp, clientObj: ObjType?) {
        val invObj = player.inv[sub] ?: return
        if (isClientObjInvalid(invObj, clientObj)) {
            return
        }
        val shop = player.openedShop ?: return
        val operations = shop.operations() ?: return
        operations.sideInvOp(player, player.inv, shop, sub, op)
    }

    private fun Player.closeShop() {
        val shop = openedShop ?: return
        stopInvTransmit(shop.inv)
        openedShop = null
    }

    private fun Shop.operations(): ShopOperations? {
        val operations = operationMap[currency]
        if (operations != null) {
            return operations
        }
        logger.error { "Currency `${currency}` does not have valid operations. (shop=$this)" }
        return null
    }

    private fun isClientObjInvalid(invObj: InvObj, clientObj: ObjType?): Boolean =
        clientObj == null || !clientObj.isAssociatedWith(invObj)
}
