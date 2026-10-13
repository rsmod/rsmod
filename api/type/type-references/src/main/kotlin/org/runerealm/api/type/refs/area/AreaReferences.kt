package org.runerealm.api.type.refs.area

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.area.AreaType
import org.runerealm.game.type.area.HashedAreaType

public abstract class AreaReferences : HashTypeReferences<AreaType>(AreaType::class.java) {
    override fun find(internal: String, hash: Long?): AreaType {
        val type = HashedAreaType(hash, internal)
        cache += type
        return type
    }
}
