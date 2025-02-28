package org.rsmod.api.type.refs.varnbit

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.varnbit.HashedVarnBitType
import org.rsmod.game.type.varnbit.VarnBitType

public abstract class VarnBitReferences : NameTypeReferences<VarnBitType>(VarnBitType::class.java) {
    public override fun find(internal: String): VarnBitType {
        // For now, can't see a realistic situation where identity hash verification is required.
        // Though maybe at some point plugins may require support for this to ensure any base/core
        // varnbits are not changed. Can reconsider this decision in the future.
        val type = HashedVarnBitType(null, internal)
        cache += type
        return type
    }
}
