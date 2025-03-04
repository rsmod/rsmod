package org.rsmod.api.type.refs.headbar

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.headbar.HashedHeadbarType
import org.rsmod.game.type.headbar.HeadbarType

public abstract class HeadbarReferences : HashTypeReferences<HeadbarType>(HeadbarType::class.java) {
    override fun find(internal: String, hash: Long?): HeadbarType {
        val type = HashedHeadbarType(hash, internal)
        cache += type
        return type
    }
}
