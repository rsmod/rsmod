package org.rsmod.plugins.api.cache.type.literal.codec

import org.rsmod.plugins.types.NamedNpc

public object CacheTypeNamedNpc : CacheTypeBaseInt<NamedNpc>(NamedNpc::class.java) {

    override fun decode(value: Int): NamedNpc {
        return NamedNpc(value)
    }

    override fun encode(value: NamedNpc): Int {
        return value.id
    }
}
