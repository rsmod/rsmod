package org.rsmod.game.type.stat

@DslMarker private annotation class StatBuilderDsl

@StatBuilderDsl
public class StatTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): StatType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return StatType(internalId = id, internalName = internalName)
    }
}
