package org.runerealm.api.type.refs.loc

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.loc.HashedLocType
import org.runerealm.game.type.loc.LocType

public abstract class LocReferences : HashTypeReferences<LocType>(LocType::class.java) {
    override fun find(internal: String, hash: Long?): LocType {
        val type = HashedLocType(hash, internal)
        cache += type
        return type
    }
}
