package org.rsmod.game.type.currency

import org.rsmod.game.type.CacheType

public data class CurrencyType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "CurrencyType(internalName='$internalName', internalId=$internalId)"
}
