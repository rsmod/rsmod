package org.runerealm.api.type.refs.hitmark

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.hitmark.HashedHitmarkType
import org.runerealm.game.type.hitmark.HitmarkType

public abstract class HitmarkReferences : HashTypeReferences<HitmarkType>(HitmarkType::class.java) {
    override fun find(internal: String, hash: Long?): HitmarkType {
        val type = HashedHitmarkType(hash, internal)
        cache += type
        return type
    }
}
