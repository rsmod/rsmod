package org.rsmod.plugins.cache.literal.codec

public object CacheTypeBoolean : CacheTypeBaseInt<Boolean>(Boolean::class.java) {

    override fun decode(value: Int): Boolean {
        return value == 1
    }

    override fun encode(value: Boolean): Int {
        return if (value) 1 else 0
    }
}
