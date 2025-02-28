package org.rsmod.api.type.refs.varn

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.varn.HashedVarnType
import org.rsmod.game.type.varn.VarnType

public abstract class VarnReferences : NameTypeReferences<VarnType>(VarnType::class.java) {
    public override fun find(internal: String): VarnType {
        // For now, can't see a realistic situation where identity hash verification is required.
        // Though maybe at some point plugins may require support for this to ensure any base/core
        // varns are not changed. Can reconsider this decision in the future.
        val type = HashedVarnType(null, internal)
        cache += type
        return type
    }
}
