package org.rsmod.game.type.queue

@DslMarker private annotation class QueueBuilderDsl

@QueueBuilderDsl
public class QueueTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): QueueType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return QueueType(internalId = id, internalName = internalName)
    }
}
