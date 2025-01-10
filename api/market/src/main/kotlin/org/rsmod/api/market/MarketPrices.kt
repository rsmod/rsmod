package org.rsmod.api.market

import org.rsmod.game.type.obj.UnpackedObjType

public interface MarketPrices {
    public operator fun get(type: UnpackedObjType): Int?
}
