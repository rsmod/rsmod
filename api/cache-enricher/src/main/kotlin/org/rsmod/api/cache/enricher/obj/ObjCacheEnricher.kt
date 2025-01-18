package org.rsmod.api.cache.enricher.obj

import org.rsmod.api.cache.enricher.CacheEnricher
import org.rsmod.game.type.obj.ObjTypeBuilder
import org.rsmod.game.type.obj.UnpackedObjType

public interface ObjCacheEnricher : CacheEnricher<UnpackedObjType> {
    override fun merge(edit: UnpackedObjType, base: UnpackedObjType): UnpackedObjType {
        return ObjTypeBuilder.merge(edit, base)
    }

    override fun idOf(type: UnpackedObjType): Int = type.id
}
