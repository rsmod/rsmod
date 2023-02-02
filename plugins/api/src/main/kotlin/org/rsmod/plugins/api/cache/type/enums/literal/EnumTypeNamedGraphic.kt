package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.game.types.NamedGraphic

public object EnumTypeNamedGraphic : EnumTypeBaseInt<NamedGraphic> {

    override fun decode(value: Int): NamedGraphic {
        return NamedGraphic(value)
    }
}
