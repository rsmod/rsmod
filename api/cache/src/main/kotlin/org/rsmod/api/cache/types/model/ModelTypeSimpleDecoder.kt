package org.rsmod.api.cache.types.model

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.cache.Cache
import org.openrs2.cache.Js5Index
import org.rsmod.api.cache.Js5Archives
import org.rsmod.game.type.model.ModelTypeList
import org.rsmod.game.type.model.SimpleModelType

public object ModelTypeSimpleDecoder {
    public fun decodeAll(cache: Cache): ModelTypeList {
        val types = Int2ObjectOpenHashMap<SimpleModelType>()
        val groups = cache.list(Js5Archives.MODELS)
        for (group in groups) {
            val type = toModelType(group)
            types[group.id] = type
        }
        return ModelTypeList(types)
    }

    private fun toModelType(group: Js5Index.Group<*>): SimpleModelType {
        return SimpleModelType(
            checksum = group.checksum,
            internalId = group.id,
            internalName = null,
        )
    }
}
