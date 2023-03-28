package org.rsmod.plugins.cache.literal.codec

import org.rsmod.plugins.types.NamedAnimation

public object CacheTypeNamedAnimation : CacheTypeBaseInt<NamedAnimation>(NamedAnimation::class.java) {

    override fun decode(value: Int): NamedAnimation {
        return NamedAnimation(value)
    }

    override fun encode(value: NamedAnimation): Int {
        return value.id
    }
}
