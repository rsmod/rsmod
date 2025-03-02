package org.rsmod.api.type.refs.hitmark

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.hitmark.HashedHitmarkType
import org.rsmod.game.type.hitmark.HitmarkType

public abstract class HitmarkReferences : HashTypeReferences<HitmarkType>(HitmarkType::class.java) {
    override fun find(internal: String, hash: Long?): HitmarkType {
        val type = HashedHitmarkType(hash, internal)
        cache += type
        return type
    }
}
