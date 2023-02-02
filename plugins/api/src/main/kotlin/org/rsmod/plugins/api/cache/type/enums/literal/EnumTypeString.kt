package org.rsmod.plugins.api.cache.type.enums.literal

public object EnumTypeString : EnumTypeBaseString<String> {

    override fun decode(value: String): String {
        return value
    }
}
