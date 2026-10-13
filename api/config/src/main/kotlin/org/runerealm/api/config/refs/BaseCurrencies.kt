@file:Suppress("SpellCheckingInspection", "unused")

package org.runerealm.api.config.refs

import org.runerealm.api.type.refs.currency.CurrencyReferences

typealias currencies = BaseCurrencies

object BaseCurrencies : CurrencyReferences() {
    val standard_gp = find("standard_gp")
}
