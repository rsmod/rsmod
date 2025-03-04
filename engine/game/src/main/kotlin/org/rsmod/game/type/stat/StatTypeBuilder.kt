package org.rsmod.game.type.stat

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder

@DslMarker private annotation class StatBuilderDsl

@StatBuilderDsl
public class StatTypeBuilder(public var internalName: String? = null) {
    public var maxLevel: Int? = null
    public var displayName: String? = null
    public var unreleased: Boolean? = null

    public fun build(id: Int): UnpackedStatType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        val maxLevel = maxLevel ?: DEFAULT_MAX_LEVEL
        val displayName = displayName ?: internalName.toDisplayName()
        val unreleased = unreleased ?: false
        return UnpackedStatType(
            unreleased = unreleased,
            internalMaxLevel = maxLevel,
            internalDisplayName = displayName,
            internalId = id,
            internalName = internalName,
        )
    }

    private fun String.toDisplayName(): String = replaceFirstChar(Char::titlecase)

    public companion object : MergeableCacheBuilder<UnpackedStatType> {
        public const val DEFAULT_MAX_LEVEL: Int = 99

        override fun merge(edit: UnpackedStatType, base: UnpackedStatType): UnpackedStatType {
            val maxLevel = select(edit, base, DEFAULT_MAX_LEVEL) { maxLevel }
            val displayName = select(edit, base, default = null) { displayName }
            val unreleased = select(edit, base, default = false) { unreleased }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedStatType(
                unreleased = unreleased,
                internalMaxLevel = maxLevel,
                internalDisplayName = displayName,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
