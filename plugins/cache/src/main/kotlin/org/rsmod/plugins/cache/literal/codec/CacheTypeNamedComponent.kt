package org.rsmod.plugins.cache.literal.codec

import org.rsmod.plugins.types.NamedComponent

public object CacheTypeNamedComponent : CacheTypeBaseInt<NamedComponent>(NamedComponent::class.java) {

    override fun decode(value: Int): NamedComponent {
        return NamedComponent(value)
    }

    override fun encode(value: NamedComponent): Int {
        return value.packed
    }
}
