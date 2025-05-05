package org.rsmod.api.cache.enricher

public interface CacheEnricher<T> {
    public fun generate(): List<T>
}
