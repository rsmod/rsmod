package org.rsmod.api.shops

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.config.refs.currencies
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stopInvTransmit
import org.rsmod.api.script.onIfClose
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.shops.config.ShopComponents
import org.rsmod.api.shops.config.ShopInterfaces
import org.rsmod.api.shops.operation.ShopOperationMap
import org.rsmod.api.shops.operation.ShopOperations
import org.rsmod.api.shops.operation.StandardGpShopOperations
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.shop.Shop
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.isAssociatedWith
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
