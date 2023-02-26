package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.plugins.types.NamedGraphic

public object EnumTypeNamedGraphic : EnumTypeBaseInt<NamedGraphic> {

    override fun decode(value: Int): NamedGraphic {
        return NamedGraphic(value)
    }

    override fun encode(value: NamedGraphic): Int {
        return value.id
    }
}
