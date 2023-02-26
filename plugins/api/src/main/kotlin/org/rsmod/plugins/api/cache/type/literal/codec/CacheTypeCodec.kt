package org.rsmod.plugins.api.cache.type.literal.codec

public interface CacheTypeCodec<K, V> {

    public fun decode(value: K): V?

    public fun encode(value: V): K
}
