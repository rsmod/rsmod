package org.rsmod.plugins.api.cache.type.literal.codec

public object CacheTypeString : CacheTypeBaseString<String> {

    override fun decode(value: String): String {
        return value
    }

    override fun encode(value: String): String {
        return value
    }
}