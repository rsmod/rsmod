package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.currency.CurrencyReferences

typealias currencies = BaseCurrencies

object BaseCurrencies : CurrencyReferences() {
    val standard_gp = find("standard_gp")
}
