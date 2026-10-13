package org.runerealm.api.type.refs.inv

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.inv.HashedInvType
import org.runerealm.game.type.inv.InvType

public abstract class InvReferences : HashTypeReferences<InvType>(InvType::class.java) {
    override fun find(internal: String, hash: Long?): InvType {
        val type = HashedInvType(hash, internal)
        cache += type
        return type
    }
}
