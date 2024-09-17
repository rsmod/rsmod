package org.rsmod.game.shop

import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.currency.CurrencyType

public data class Shop(
    public val inv: Inventory,
    public val currency: CurrencyType,
    public val buyPercentage: Double,
    public val sellPercentage: Double,
    public val changePercentage: Double,
)
