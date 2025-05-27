package org.rsmod.game.type.mod

import org.rsmod.game.type.TypeResolver

public data class ModLevelTypeList(public val types: MutableMap<Int, UnpackedModLevelType>) :
    Map<Int, UnpackedModLevelType> by types {
    private var default: UnpackedModLevelType? = null

    public operator fun get(type: ModLevelType): UnpackedModLevelType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")

    public fun default(): UnpackedModLevelType {
        val cached = default
        if (cached != null) {
            return cached
        }
        val default = types.values.first { it.id == 0 }
        this.default = default
        return default
    }
}
