package org.rsmod.game.type.varp

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import org.rsmod.game.type.TypeResolver

public data class VarpTypeList(public val types: MutableMap<Int, UnpackedVarpType>) :
    Map<Int, UnpackedVarpType> by types {
    public operator fun get(type: VarpType): UnpackedVarpType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")

    public fun filterTransmitKeys(): Set<Int> =
        IntOpenHashSet(types.filterValues { !it.transmit.never }.keys)
}
