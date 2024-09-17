package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.currency.CurrencyReferences
import org.rsmod.game.type.currency.CurrencyType

public typealias currencies = BaseCurrencies

public object BaseCurrencies : CurrencyReferences() {
    public val standard_gp: CurrencyType = find("standard_gp")
}
