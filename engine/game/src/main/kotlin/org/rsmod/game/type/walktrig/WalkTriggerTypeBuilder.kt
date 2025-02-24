package org.rsmod.game.type.walktrig

@DslMarker private annotation class WalkTriggerBuilderDsl

@WalkTriggerBuilderDsl
public class WalkTriggerTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): WalkTriggerType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return WalkTriggerType(internalId = id, internalName = internalName)
    }
}
