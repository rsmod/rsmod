package org.rsmod.api.type.refs.inv

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.inv.HashedInvType
import org.rsmod.game.type.inv.InvType

public abstract class InvReferences : HashTypeReferences<InvType>(InvType::class.java) {
    override fun find(internal: String, hash: Long?): InvType {
        val type = HashedInvType(hash, internal)
        cache += type
        return type
    }
}
