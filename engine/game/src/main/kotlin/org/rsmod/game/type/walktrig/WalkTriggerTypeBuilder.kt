package org.rsmod.game.type.walktrig

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder

@DslMarker private annotation class WalkTriggerBuilderDsl

@WalkTriggerBuilderDsl
public class WalkTriggerTypeBuilder(public var internalName: String? = null) {
    public var priority: WalkTriggerPriority? = null

    public fun build(id: Int): WalkTriggerType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        val priority = priority ?: DEFAULT_PRIORITY
        return WalkTriggerType(
            internalPriority = priority,
            internalId = id,
            internalName = internalName,
        )
    }

    public companion object : MergeableCacheBuilder<WalkTriggerType> {
        public val DEFAULT_PRIORITY: WalkTriggerPriority = WalkTriggerPriority.None

        override fun merge(edit: WalkTriggerType, base: WalkTriggerType): WalkTriggerType {
            val priority = select(edit, base, DEFAULT_PRIORITY) { internalPriority }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return WalkTriggerType(
                internalPriority = priority,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
