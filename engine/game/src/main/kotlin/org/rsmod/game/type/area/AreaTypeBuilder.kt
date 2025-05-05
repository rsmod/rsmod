package org.rsmod.game.type.area

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder

public class AreaTypeBuilder(public var internal: String? = null) {
    public var colour: Int? = null

    public fun build(id: Int): UnpackedAreaType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val colour = colour ?: DEFAULT_COLOUR
        return UnpackedAreaType(colour = colour, internalId = id, internalName = internal)
    }

    public companion object : MergeableCacheBuilder<UnpackedAreaType> {
        public const val DEFAULT_COLOUR: Int = 0

        override fun merge(edit: UnpackedAreaType, base: UnpackedAreaType): UnpackedAreaType {
            val colour = select(edit, base, DEFAULT_COLOUR) { colour }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedAreaType(
                colour = colour,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
