package org.rsmod.plugins.api.cache.type.literal

public interface CacheTypeLiteral<K, V> {

    public fun decode(value: K): V?

    public fun encode(value: V): K
}
