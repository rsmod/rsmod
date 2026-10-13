package org.runerealm.api.type.refs.varp

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.varp.HashedVarpType
import org.runerealm.game.type.varp.VarpType

public abstract class VarpReferences : HashTypeReferences<VarpType>(VarpType::class.java) {
    override fun find(internal: String, hash: Long?): VarpType {
        val type = HashedVarpType(hash, internal)
        cache += type
        return type
    }
}
