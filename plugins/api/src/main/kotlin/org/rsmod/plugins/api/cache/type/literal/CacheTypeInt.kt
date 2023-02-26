package org.rsmod.plugins.api.cache.type.literal

public object CacheTypeInt : CacheTypeBaseInt<Int> {

    override fun decode(value: Int): Int {
        return value
    }

    override fun encode(value: Int): Int {
        return value
    }
}
