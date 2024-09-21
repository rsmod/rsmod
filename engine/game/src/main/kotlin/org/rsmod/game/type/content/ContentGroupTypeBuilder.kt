package org.rsmod.game.type.content

@DslMarker private annotation class ContentGroupBuilderDsl

@ContentGroupBuilderDsl
public class ContentGroupTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): ContentGroupType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return ContentGroupType(internalId = id, internalName = internalName)
    }
}
