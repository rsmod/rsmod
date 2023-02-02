package org.rsmod.plugins.api.cache.type.enums.literal

public object EnumTypeBoolean : EnumTypeBaseInt<Boolean> {

    override fun decode(value: Int): Boolean {
        return value == 1
    }
}
