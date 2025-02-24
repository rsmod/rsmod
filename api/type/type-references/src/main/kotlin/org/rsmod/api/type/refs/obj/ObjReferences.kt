package org.rsmod.api.type.refs.obj

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.obj.HashedObjType
import org.rsmod.game.type.obj.ObjType

public abstract class ObjReferences : HashTypeReferences<ObjType>(ObjType::class.java) {
    override fun find(internal: String, hash: Long?): ObjType {
        val type = HashedObjType(hash, internal)
        cache += type
        return type
    }
}
