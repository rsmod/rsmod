package org.rsmod.game.type.seq

import org.rsmod.game.type.TypeResolver

public data class SeqTypeList(public val types: MutableMap<Int, UnpackedSeqType>) :
    Map<Int, UnpackedSeqType> by types {
    public operator fun get(type: SeqType): UnpackedSeqType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
