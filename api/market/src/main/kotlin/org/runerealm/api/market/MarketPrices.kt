package org.runerealm.api.market

import org.runerealm.game.type.obj.UnpackedObjType

public interface MarketPrices {
    public operator fun get(type: UnpackedObjType): Int?
}
