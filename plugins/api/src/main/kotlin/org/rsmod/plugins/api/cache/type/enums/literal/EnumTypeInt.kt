package org.rsmod.plugins.api.cache.type.enums.literal

public object EnumTypeInt : EnumTypeBaseInt<Int> {

    override fun decode(value: Int): Int {
        return value
    }
}
