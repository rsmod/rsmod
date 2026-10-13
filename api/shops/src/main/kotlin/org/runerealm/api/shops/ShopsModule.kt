package org.runerealm.api.shops

import org.runerealm.api.shops.operation.ShopOperationMap
import org.runerealm.api.shops.operation.StandardGpShopOperations
import org.runerealm.api.shops.restock.ShopRestockProcess
import org.runerealm.plugin.module.PluginModule

public class ShopsModule : PluginModule() {
    override fun bind() {
        bindInstance<ShopOperationMap>()
        bindInstance<ShopRestockProcess>()
        bindInstance<Shops>()

        bindInstance<StandardGpShopOperations>()
    }
}
