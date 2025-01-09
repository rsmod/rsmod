package org.rsmod.api.type.refs.interf

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.interf.HashedInterfaceType
import org.rsmod.game.type.interf.InterfaceType

public abstract class InterfaceReferences :
    HashTypeReferences<InterfaceType>(InterfaceType::class.java) {
    override fun find(internal: String, hash: Long?): InterfaceType {
        val type = HashedInterfaceType(hash, internalName = internal)
        cache += type
        return type
    }
}
