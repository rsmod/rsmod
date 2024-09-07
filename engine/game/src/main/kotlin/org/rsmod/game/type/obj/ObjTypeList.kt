package org.rsmod.game.type.obj

import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.TypeResolver

public data class ObjTypeList(public val types: Map<Int, UnpackedObjType>) :
    Map<Int, UnpackedObjType> by types {
    public operator fun get(type: ObjType): UnpackedObjType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")

    public operator fun get(obj: InvObj): UnpackedObjType =
        types[obj.id] ?: throw NoSuchElementException("Type is missing in the map: $obj.")

    public operator fun get(obj: Obj): UnpackedObjType =
        types[obj.type] ?: throw NoSuchElementException("Type is missing in the map: $obj.")
}
