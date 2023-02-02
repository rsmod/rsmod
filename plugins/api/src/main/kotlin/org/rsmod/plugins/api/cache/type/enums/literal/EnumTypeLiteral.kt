package org.rsmod.plugins.api.cache.type.enums.literal

public interface EnumTypeLiteral<K, V> {

    public fun decode(value: K): V?
}
