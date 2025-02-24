package org.rsmod.api.type.refs.stat

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.stat.HashedStatType
import org.rsmod.game.type.stat.StatType

public abstract class StatReferences : HashTypeReferences<StatType>(StatType::class.java) {
    override fun find(internal: String, hash: Long?): StatType {
        val type = HashedStatType(hash, internalName = internal)
        cache += type
        return type
    }
}
