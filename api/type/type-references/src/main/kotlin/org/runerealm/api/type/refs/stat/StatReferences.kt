package org.runerealm.api.type.refs.stat

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.stat.HashedStatType
import org.runerealm.game.type.stat.StatType

public abstract class StatReferences : HashTypeReferences<StatType>(StatType::class.java) {
    override fun find(internal: String, hash: Long?): StatType {
        val type = HashedStatType(hash, internal)
        cache += type
        return type
    }
}
