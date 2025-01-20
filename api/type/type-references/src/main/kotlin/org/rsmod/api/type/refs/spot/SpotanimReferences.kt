package org.rsmod.api.type.refs.spot

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.spot.HashedSpotanimType
import org.rsmod.game.type.spot.SpotanimType

public abstract class SpotanimReferences :
    HashTypeReferences<SpotanimType>(SpotanimType::class.java) {
    override fun find(internal: String, hash: Long?): SpotanimType {
        val type = HashedSpotanimType(hash, internalName = internal)
        cache += type
        return type
    }
}
