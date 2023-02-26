package org.rsmod.plugins.api.cache.type.literal

import org.rsmod.plugins.types.NamedComponent

public object CacheTypeNamedComponent : CacheTypeBaseInt<NamedComponent> {

    override fun decode(value: Int): NamedComponent {
        return NamedComponent(value)
    }

    override fun encode(value: NamedComponent): Int {
        return value.id
    }
}
