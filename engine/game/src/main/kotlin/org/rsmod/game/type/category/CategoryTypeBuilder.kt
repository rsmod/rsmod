package org.rsmod.game.type.category

@DslMarker private annotation class CategoryBuilderDsl

@CategoryBuilderDsl
public class CategoryTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): CategoryType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return CategoryType(internalId = id, internalName = internalName)
    }
}
