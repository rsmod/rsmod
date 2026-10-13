package org.runerealm.api.type.refs.mod

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.mod.HashedModLevelType
import org.runerealm.game.type.mod.ModLevelType

public abstract class ModLevelReferences :
    HashTypeReferences<ModLevelType>(ModLevelType::class.java) {
    override fun find(internal: String, hash: Long?): ModLevelType {
        val type = HashedModLevelType(hash, internal)
        cache += type
        return type
    }
}
