package org.runerealm.api.type.refs.obj

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.obj.HashedObjType
import org.runerealm.game.type.obj.ObjType

public abstract class ObjReferences : HashTypeReferences<ObjType>(ObjType::class.java) {
    override fun find(internal: String, hash: Long?): ObjType {
        val type = HashedObjType(hash, internal)
        cache += type
        return type
    }
}
