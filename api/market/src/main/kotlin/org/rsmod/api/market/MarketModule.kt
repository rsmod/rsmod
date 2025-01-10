package org.rsmod.api.market

import com.google.inject.AbstractModule

public object MarketModule : AbstractModule() {
    override fun configure() {
        bind(MarketPrices::class.java).toInstance(DefaultMarketPrices)
    }
}
