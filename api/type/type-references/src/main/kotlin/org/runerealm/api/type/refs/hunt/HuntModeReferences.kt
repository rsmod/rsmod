package org.runerealm.api.type.refs.hunt

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.hunt.HashedHuntModeType
import org.runerealm.game.type.hunt.HuntModeType

public abstract class HuntModeReferences :
    HashTypeReferences<HuntModeType>(HuntModeType::class.java) {
    override fun find(internal: String, hash: Long?): HuntModeType {
        val type = HashedHuntModeType(hash, internal)
        cache += type
        return type
    }
}
