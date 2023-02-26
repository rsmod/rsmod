package org.rsmod.plugins.api.cache.type.enums.literal

public object EnumTypeString : EnumTypeBaseString<String> {

    override fun decode(value: String): String {
        return value
    }

    override fun encode(value: String): String {
        return value
    }
}
