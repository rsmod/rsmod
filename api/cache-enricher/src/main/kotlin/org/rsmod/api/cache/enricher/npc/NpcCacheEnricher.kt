package org.rsmod.api.cache.enricher.npc

import org.rsmod.api.cache.enricher.CacheEnricher
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.UnpackedNpcType

public interface NpcCacheEnricher : CacheEnricher<UnpackedNpcType> {
    override fun merge(edit: UnpackedNpcType, base: UnpackedNpcType): UnpackedNpcType {
        return NpcTypeBuilder.merge(edit, base)
    }

    override fun idOf(type: UnpackedNpcType): Int = type.id
}
