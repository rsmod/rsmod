package org.rsmod.game.type.timer

@DslMarker private annotation class TimerBuilderDsl

@TimerBuilderDsl
public class TimerTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): TimerType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return TimerType(internalId = id, internalName = internalName)
    }
}
