package org.rsmod.api.cache.enricher

import org.rsmod.game.type.CacheType

public interface CacheEnricher<T : CacheType> {
    public fun generate(): List<T>
}
