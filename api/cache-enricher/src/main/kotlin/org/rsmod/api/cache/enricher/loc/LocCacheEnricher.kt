package org.rsmod.api.cache.enricher.loc

import org.rsmod.api.cache.enricher.CacheEnricher
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.UnpackedLocType

public interface LocCacheEnricher : CacheEnricher<UnpackedLocType> {
    override fun merge(edit: UnpackedLocType, base: UnpackedLocType): UnpackedLocType {
        return LocTypeBuilder.merge(edit, base)
    }

    override fun idOf(type: UnpackedLocType): Int = type.id
}
