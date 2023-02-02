package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.game.types.NamedObject

public object EnumTypeNamedObject : EnumTypeBaseInt<NamedObject> {

    override fun decode(value: Int): NamedObject {
        return NamedObject(value)
    }
}
