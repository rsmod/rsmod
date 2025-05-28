package org.rsmod.api.type.refs.mod

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.mod.HashedModLevelType
import org.rsmod.game.type.mod.ModLevelType

public abstract class ModLevelReferences :
    HashTypeReferences<ModLevelType>(ModLevelType::class.java) {
    override fun find(internal: String, hash: Long?): ModLevelType {
        val type = HashedModLevelType(hash, internal)
        cache += type
        return type
    }
}
