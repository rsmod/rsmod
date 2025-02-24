package org.rsmod.game.type.walktrig

import org.rsmod.game.type.util.GenericPropertySelector.select

@DslMarker private annotation class WalkTriggerBuilderDsl

@WalkTriggerBuilderDsl
public class WalkTriggerTypeBuilder(public var internalName: String? = null) {
    public var priority: WalkTriggerPriority? = null

    public fun build(id: Int): WalkTriggerType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        val priority = priority ?: DEFAULT_PRIORITY
        return WalkTriggerType(
            internalId = id,
            internalName = internalName,
            internalPriority = priority,
        )
    }

    public companion object {
        public val DEFAULT_PRIORITY: WalkTriggerPriority = WalkTriggerPriority.None

        public fun merge(edit: WalkTriggerType, base: WalkTriggerType): WalkTriggerType {
            val priority = select(edit, base, DEFAULT_PRIORITY) { internalPriority }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return WalkTriggerType(
                internalId = internalId ?: -1,
                internalName = internalName,
                internalPriority = priority,
            )
        }
    }
}
