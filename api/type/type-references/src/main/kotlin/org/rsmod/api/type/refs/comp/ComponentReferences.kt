package org.rsmod.api.type.refs.comp

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.comp.HashedComponentType

public abstract class ComponentReferences :
    HashTypeReferences<ComponentType>(ComponentType::class.java) {
    override fun find(hash: Long): ComponentType {
        val type = HashedComponentType(hash)
        cache += type
        return type
    }
}
