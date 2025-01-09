package org.rsmod.api.type.refs.loc

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.loc.HashedLocType
import org.rsmod.game.type.loc.LocType

public abstract class LocReferences : HashTypeReferences<LocType>(LocType::class.java) {
    override fun find(internal: String, hash: Long?): LocType {
        val type = HashedLocType(hash, internalName = internal)
        cache += type
        return type
    }
}
