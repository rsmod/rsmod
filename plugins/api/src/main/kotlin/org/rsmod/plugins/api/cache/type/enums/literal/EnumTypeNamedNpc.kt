package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.game.types.NamedNpc

public object EnumTypeNamedNpc : EnumTypeBaseInt<NamedNpc> {

    override fun decode(value: Int): NamedNpc {
        return NamedNpc(value)
    }
}
