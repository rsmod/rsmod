package org.runerealm.api.type.refs.currency

import org.runerealm.api.type.refs.NameTypeReferences
import org.runerealm.game.type.currency.CurrencyType
import org.runerealm.game.type.currency.CurrencyTypeBuilder

public abstract class CurrencyReferences :
    NameTypeReferences<CurrencyType>(CurrencyType::class.java) {
    override fun find(internal: String): CurrencyType {
        val type = CurrencyTypeBuilder(internalName = internal).build(id = -1)
        cache += type
        return type
    }
}
