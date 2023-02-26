package org.rsmod.plugins.api.cache.type.literal.codec

import org.rsmod.plugins.types.NamedAnimation

public object CacheTypeNamedAnimation : CacheTypeBaseInt<NamedAnimation> {

    override fun decode(value: Int): NamedAnimation {
        return NamedAnimation(value)
    }

    override fun encode(value: NamedAnimation): Int {
        return value.id
    }
}
