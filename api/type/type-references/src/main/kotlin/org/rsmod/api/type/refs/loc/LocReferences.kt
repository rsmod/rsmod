package org.rsmod.api.type.refs.loc

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.loc.HashedLocType
import org.rsmod.game.type.loc.LocType

public abstract class LocReferences : HashTypeReferences<LocType>(LocType::class.java) {
    override fun find(hash: Long): LocType {
        val type = HashedLocType(hash)
        cache += type
        return type
    }
}
