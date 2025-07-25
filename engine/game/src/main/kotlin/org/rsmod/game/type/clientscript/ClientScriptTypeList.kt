package org.rsmod.game.type.clientscript

import org.rsmod.game.type.TypeResolver

public data class ClientScriptTypeList(
    public val types: MutableMap<Int, UnpackedClientScriptType>
) : Map<Int, UnpackedClientScriptType> by types {
    public operator fun get(type: ClientScriptType): UnpackedClientScriptType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
