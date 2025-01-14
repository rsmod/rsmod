package org.rsmod.game.type.droptrig

@DslMarker private annotation class DropTriggerBuilderDsl

@DropTriggerBuilderDsl
public class DropTriggerTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): DropTriggerType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return DropTriggerType(internalId = id, internalName = internalName)
    }
}
