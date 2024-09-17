package org.rsmod.api.type.refs.currency

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.currency.CurrencyType
import org.rsmod.game.type.currency.CurrencyTypeBuilder

public abstract class CurrencyReferences :
    NameTypeReferences<CurrencyType>(CurrencyType::class.java) {
    override fun find(internal: String): CurrencyType {
        val type = CurrencyTypeBuilder(internalName = internal).build(id = -1)
        cache += type
        return type
    }
}
