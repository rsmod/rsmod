package org.rsmod.api.market

import jakarta.inject.Inject
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

public class DefaultMarketPrices @Inject constructor(private val objTypes: ObjTypeList) :
    MarketPrices {
    override fun get(type: UnpackedObjType): Int = objTypes.uncert(type).cost
}
