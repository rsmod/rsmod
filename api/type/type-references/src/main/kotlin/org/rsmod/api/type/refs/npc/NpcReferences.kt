package org.rsmod.api.type.refs.npc

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.npc.HashedNpcType
import org.rsmod.game.type.npc.NpcType

public abstract class NpcReferences : HashTypeReferences<NpcType>(NpcType::class.java) {
    override fun find(internal: String, hash: Long?): NpcType {
        val type = HashedNpcType(hash, internalName = internal)
        cache += type
        return type
    }
}
