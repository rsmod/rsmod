package org.rsmod.game.type.content

@DslMarker private annotation class ContentBuilderDsl

@ContentBuilderDsl
public class ContentTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): ContentType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return ContentType(internalId = id, internalName = internalName)
    }
}
