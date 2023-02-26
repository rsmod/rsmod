package org.rsmod.plugins.api.cache.type.enums.literal

public object EnumTypeBoolean : EnumTypeBaseInt<Boolean> {

    override fun decode(value: Int): Boolean {
        return value == 1
    }

    override fun encode(value: Boolean): Int {
        return if (value) 1 else 0
    }
}
