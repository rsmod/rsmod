package org.rsmod.api.type.refs.struct

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.struct.HashedStructType
import org.rsmod.game.type.struct.StructType

public abstract class StructReferences : HashTypeReferences<StructType>(StructType::class.java) {
    override fun find(internal: String, hash: Long?): StructType {
        val type = HashedStructType(hash, internal)
        cache += type
        return type
    }
}
