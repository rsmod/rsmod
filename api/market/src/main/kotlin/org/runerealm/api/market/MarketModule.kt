package org.runerealm.api.market

import org.runerealm.module.ExtendedModule

public object MarketModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<MarketPrices>(DefaultMarketPrices::class.java)
    }
}
