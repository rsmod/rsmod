package org.runerealm.api.type.refs.interf

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.interf.HashedInterfaceType
import org.runerealm.game.type.interf.InterfaceType

public abstract class InterfaceReferences :
    HashTypeReferences<InterfaceType>(InterfaceType::class.java) {
    override fun find(internal: String, hash: Long?): InterfaceType {
        val type = HashedInterfaceType(hash, internal)
        cache += type
        return type
    }
}
