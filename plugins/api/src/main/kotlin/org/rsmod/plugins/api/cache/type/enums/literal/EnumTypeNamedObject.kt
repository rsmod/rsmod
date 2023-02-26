package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.plugins.types.NamedObject

public object EnumTypeNamedObject : EnumTypeBaseInt<NamedObject> {

    override fun decode(value: Int): NamedObject {
        return NamedObject(value)
    }

    override fun encode(value: NamedObject): Int {
        return value.id
    }
}
