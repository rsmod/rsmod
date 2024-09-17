package org.rsmod.game.type.currency

@DslMarker private annotation class CurrencyBuilderDsl

@CurrencyBuilderDsl
public class CurrencyTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): CurrencyType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return CurrencyType(internalId = id, internalName = internalName)
    }
}
