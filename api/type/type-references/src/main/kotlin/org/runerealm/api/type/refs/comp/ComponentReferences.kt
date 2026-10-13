package org.runerealm.api.type.refs.comp

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.comp.HashedComponentType

public abstract class ComponentReferences :
    HashTypeReferences<ComponentType>(ComponentType::class.java) {
    override fun find(internal: String, hash: Long?): ComponentType {
        val type = HashedComponentType(hash, internal)
        cache += type
        return type
    }
}
