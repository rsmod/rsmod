package org.rsmod.game.type.obj

import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.TypeResolver

public data class ObjTypeList(public val types: MutableMap<Int, UnpackedObjType>) :
    Map<Int, UnpackedObjType> by types {
    public operator fun get(type: ObjType): UnpackedObjType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")

    public operator fun get(obj: InvObj): UnpackedObjType =
        types[obj.id] ?: throw NoSuchElementException("Type is missing in the map: $obj.")

    public operator fun get(obj: Obj): UnpackedObjType =
        types[obj.type] ?: throw NoSuchElementException("Type is missing in the map: $obj.")

    public fun cert(type: UnpackedObjType): UnpackedObjType {
        if (!type.canCert) {
            return type
        }
        val link = type.certlink
        return types[link] ?: throw NoSuchElementException("Type is missing in the map: $link.")
    }

    public fun uncert(type: UnpackedObjType): UnpackedObjType {
        if (!type.isCert) {
            return type
        }
        val link = type.certlink
        return types[link] ?: throw NoSuchElementException("Type is missing in the map: $link.")
    }

    public fun cert(obj: InvObj): InvObj {
        require(obj.vars == 0) { "Cannot cert obj with vars: $obj" }
        val type = this[obj]
        if (!type.canCert) {
            return obj
        }
        val link = type.certlink
        val certType =
            types[link] ?: throw NoSuchElementException("Type is missing in the map: $link.")
        return InvObj(certType, obj.count)
    }

    public fun uncert(obj: InvObj): InvObj {
        require(obj.vars == 0) { "Cannot uncert obj with vars: $obj" }
        val type = this[obj]
        if (!type.isCert) {
            return obj
        }
        val link = type.certlink
        val uncertType =
            types[link] ?: throw NoSuchElementException("Type is missing in the map: $link.")
        return InvObj(uncertType, obj.count)
    }

    public fun placeholder(type: UnpackedObjType): UnpackedObjType {
        if (!type.hasPlaceholder) {
            return type
        }
        val link = type.placeholderlink
        return types[link] ?: throw NoSuchElementException("Type is missing in the map: $link.")
    }

    public fun unplacehold(type: UnpackedObjType): UnpackedObjType {
        if (!type.isPlaceholder) {
            return type
        }
        val link = type.placeholderlink
        return types[link] ?: throw NoSuchElementException("Type is missing in the map: $link.")
    }

    public fun untransform(type: UnpackedObjType): UnpackedObjType {
        if (!type.isTransformation) {
            return type
        }
        val link = type.transformlink
        return types[link] ?: throw NoSuchElementException("Type is missing in the map: $link.")
    }

    public fun normalize(type: UnpackedObjType): UnpackedObjType {
        val uncert = uncert(type)
        return unplacehold(uncert)
    }
}
