package org.runerealm.api.type.refs.headbar

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.headbar.HashedHeadbarType
import org.runerealm.game.type.headbar.HeadbarType

public abstract class HeadbarReferences : HashTypeReferences<HeadbarType>(HeadbarType::class.java) {
    override fun find(internal: String, hash: Long?): HeadbarType {
        val type = HashedHeadbarType(hash, internal)
        cache += type
        return type
    }
}
