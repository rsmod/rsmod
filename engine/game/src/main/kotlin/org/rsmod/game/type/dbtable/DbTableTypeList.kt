package org.rsmod.game.type.dbtable

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import org.rsmod.game.type.TypeResolver

public data class DbTableTypeList(public val types: MutableMap<Int, UnpackedDbTableType>) :
    Map<Int, UnpackedDbTableType> by types {
    public operator fun get(type: DbTableType): UnpackedDbTableType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")

    public fun filterTransmitKeys(): Set<Int> =
        IntOpenHashSet(types.filterValues(UnpackedDbTableType::clientSide).keys)
}
