package org.rsmod.api.cache.enricher

public interface CacheEnricher<T> {
    public fun generate(): List<T>

    public fun merge(edit: T, base: T): T

    public fun idOf(type: T): Int
}
