package org.rsmod.api.type.refs.area

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.area.AreaType
import org.rsmod.game.type.area.HashedAreaType

public abstract class AreaReferences : HashTypeReferences<AreaType>(AreaType::class.java) {
    override fun find(internal: String, hash: Long?): AreaType {
        val type = HashedAreaType(hash, internal)
        cache += type
        return type
    }
}
