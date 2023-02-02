package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.game.types.NamedComponent

public object EnumTypeNamedComponent : EnumTypeBaseInt<NamedComponent> {

    override fun decode(value: Int): NamedComponent {
        return NamedComponent(value)
    }
}
