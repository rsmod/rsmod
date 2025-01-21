package org.rsmod.game.type.jingle

@DslMarker private annotation class JingleBuilderDsl

@JingleBuilderDsl
public class JingleTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): JingleType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return JingleType(internalId = id, internalName = internalName)
    }
}
