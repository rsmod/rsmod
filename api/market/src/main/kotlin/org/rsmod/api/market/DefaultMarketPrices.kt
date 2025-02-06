package org.rsmod.api.market

import org.rsmod.game.type.obj.UnpackedObjType

public object DefaultMarketPrices : MarketPrices {
    override fun get(type: UnpackedObjType): Int = type.cost
}
