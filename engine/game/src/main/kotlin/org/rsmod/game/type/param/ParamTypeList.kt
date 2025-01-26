package org.rsmod.game.type.param

import it.unimi.dsi.fastutil.ints.IntOpenHashSet

public data class ParamTypeList(public val types: Map<Int, UnpackedParamType<*>>) :
    Map<Int, UnpackedParamType<*>> by types {
    @Suppress("UNCHECKED_CAST")
    public operator fun <T : Any> get(type: HashedParamType<T>): UnpackedParamType<T> {
        val mapped =
            types[type.id] ?: throw NoSuchElementException("Type is missing in the map: $type.")
        compareGenericTypes(type, mapped)
        return mapped as UnpackedParamType<T>
    }

    private fun <T : Any> compareGenericTypes(
        type: HashedParamType<T>,
        cache: UnpackedParamType<*>,
    ) {
        require(type.type == cache.type) {
            "Type for input does not match key type for cache type: input=$type, cache=$cache"
        }
    }

    public fun filterTransmitKeys(): Set<Int> =
        IntOpenHashSet(types.filterValues(UnpackedParamType<*>::transmit).keys)
}
