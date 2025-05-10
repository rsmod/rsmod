package org.rsmod.api.cache.enricher

import jakarta.inject.Inject
import org.openrs2.cache.Cache
import org.rsmod.api.cache.enricher.loc.LocCacheEnricher
import org.rsmod.api.cache.enricher.npc.NpcCacheEnricher
import org.rsmod.api.cache.enricher.obj.ObjCacheEnricher
import org.rsmod.api.cache.types.loc.LocTypeEncoder
import org.rsmod.api.cache.types.npc.NpcTypeEncoder
import org.rsmod.api.cache.types.obj.ObjTypeEncoder
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.game.type.CacheType
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.obj.ObjTypeBuilder
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.param.ParamTypeList
import org.rsmod.game.type.util.MergeableCacheBuilder
import org.rsmod.game.type.varp.VarpTypeList

public class CacheEnrichmentConfigs
@Inject
constructor(
    private val locTypes: LocTypeList,
    private val locEnrichments: Set<LocCacheEnricher>,
    private val npcTypes: NpcTypeList,
    private val npcEnrichments: Set<NpcCacheEnricher>,
    private val objTypes: ObjTypeList,
    private val objEnrichments: Set<ObjCacheEnricher>,
    private val paramTypes: ParamTypeList,
    private val varpTypes: VarpTypeList,
) {
    public fun encodeAll(dest: Cache) {
        val encoderContext =
            EncoderContext.server(paramTypes.filterTransmitKeys(), varpTypes.filterTransmitKeys())
        val locs = locEnrichments.collect(locTypes, LocTypeBuilder).asIterable()
        val npcs = npcEnrichments.collect(npcTypes, NpcTypeBuilder).asIterable()
        val objs = objEnrichments.collect(objTypes, ObjTypeBuilder).asIterable()
        LocTypeEncoder.encodeAll(dest, locs, encoderContext)
        NpcTypeEncoder.encodeAll(dest, npcs, encoderContext)
        ObjTypeEncoder.encodeAll(dest, objs, encoderContext)
    }

    private fun <T : CacheType, E : CacheEnricher<T>> Set<E>.collect(
        cacheTypes: Map<Int, T>,
        merger: MergeableCacheBuilder<T>,
    ): Map<Int, T> {
        val list = map(CacheEnricher<T>::generate).flatten()
        val grouped = list.groupBy(CacheType::id)
        val merged = mutableMapOf<Int, T>()
        for ((id, types) in grouped) {
            check(types.isNotEmpty()) { "Grouped types for enricher must not be empty." }
            val cacheType = cacheTypes[id] ?: continue // Skip types not in the cache.
            if (types.size == 1) {
                merged[id] = merger.merge(cacheType, types[0])
                continue
            }
            val folded = types.fold(types[0]) { curr, next -> merger.merge(next, curr) }
            merged[id] = merger.merge(cacheType, folded)
        }
        return merged
    }

    private fun <T> Map<Int, T>.asIterable(): Iterable<T> = values
}
