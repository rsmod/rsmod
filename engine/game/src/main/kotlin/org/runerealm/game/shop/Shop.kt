package org.runerealm.game.shop

import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.currency.CurrencyType

public data class Shop(
    public val inv: Inventory,
    public val currency: CurrencyType,
    public val buyPercentage: Double,
    public val sellPercentage: Double,
    public val changePercentage: Double,
)
