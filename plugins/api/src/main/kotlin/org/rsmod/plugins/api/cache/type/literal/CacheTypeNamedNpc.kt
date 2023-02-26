package org.rsmod.plugins.api.cache.type.literal

import org.rsmod.plugins.types.NamedNpc

public object CacheTypeNamedNpc : CacheTypeBaseInt<NamedNpc> {

    override fun decode(value: Int): NamedNpc {
        return NamedNpc(value)
    }

    override fun encode(value: NamedNpc): Int {
        return value.id
    }
}
