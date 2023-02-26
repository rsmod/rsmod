package org.rsmod.plugins.api.cache.type.literal.codec

import org.rsmod.plugins.types.NamedGraphic

public object CacheTypeNamedGraphic : CacheTypeBaseInt<NamedGraphic> {

    override fun decode(value: Int): NamedGraphic {
        return NamedGraphic(value)
    }

    override fun encode(value: NamedGraphic): Int {
        return value.id
    }
}
