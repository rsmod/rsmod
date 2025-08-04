package org.rsmod.game.type.model

import org.rsmod.game.type.TypeResolver

public data class ModelTypeList(public val types: MutableMap<Int, SimpleModelType>) :
    Map<Int, SimpleModelType> by types {
    public operator fun get(type: ModelType): SimpleModelType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
