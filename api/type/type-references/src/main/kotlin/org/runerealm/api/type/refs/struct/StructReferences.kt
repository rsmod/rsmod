package org.runerealm.api.type.refs.struct

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.struct.HashedStructType
import org.runerealm.game.type.struct.StructType

public abstract class StructReferences : HashTypeReferences<StructType>(StructType::class.java) {
    override fun find(internal: String, hash: Long?): StructType {
        val type = HashedStructType(hash, internal)
        cache += type
        return type
    }
}
