package org.rsmod.api.type.refs.hunt

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.hunt.HashedHuntModeType
import org.rsmod.game.type.hunt.HuntModeType

public abstract class HuntModeReferences :
    HashTypeReferences<HuntModeType>(HuntModeType::class.java) {
    override fun find(internal: String, hash: Long?): HuntModeType {
        val type = HashedHuntModeType(hash, internal)
        cache += type
        return type
    }
}
