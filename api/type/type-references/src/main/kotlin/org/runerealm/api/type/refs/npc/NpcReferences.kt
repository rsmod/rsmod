package org.runerealm.api.type.refs.npc

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.npc.HashedNpcType
import org.runerealm.game.type.npc.NpcType

public abstract class NpcReferences : HashTypeReferences<NpcType>(NpcType::class.java) {
    override fun find(internal: String, hash: Long?): NpcType {
        val type = HashedNpcType(hash, internal)
        cache += type
        return type
    }
}
