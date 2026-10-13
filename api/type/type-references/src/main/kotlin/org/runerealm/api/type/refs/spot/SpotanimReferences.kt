package org.runerealm.api.type.refs.spot

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.spot.HashedSpotanimType
import org.runerealm.game.type.spot.SpotanimType

public abstract class SpotanimReferences :
    HashTypeReferences<SpotanimType>(SpotanimType::class.java) {
    override fun find(internal: String, hash: Long?): SpotanimType {
        val type = HashedSpotanimType(hash, internal)
        cache += type
        return type
    }
}
