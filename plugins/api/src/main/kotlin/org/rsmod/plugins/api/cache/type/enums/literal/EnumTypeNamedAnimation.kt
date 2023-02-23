package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.plugins.types.NamedAnimation

public object EnumTypeNamedAnimation : EnumTypeBaseInt<NamedAnimation> {

    override fun decode(value: Int): NamedAnimation {
        return NamedAnimation(value)
    }
}
