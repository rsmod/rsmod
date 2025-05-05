package org.rsmod.api.cache.enricher

import jakarta.inject.Inject
import org.openrs2.cache.Cache
import org.rsmod.api.cache.enricher.loc.LocCacheEnricher
import org.rsmod.api.cache.enricher.map.area.AreaCacheEnricher
import org.rsmod.api.cache.enricher.map.area.EnrichedAreaConfig
import org.rsmod.api.cache.enricher.npc.NpcCacheEnricher
import org.rsmod.api.cache.enricher.obj.ObjCacheEnricher
import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.api.cache.map.area.MapAreaEncoder
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
import org.rsmod.map.square.MapSquareKey

public class CacheEnrichment
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
    private val areaEnrichments: Set<AreaCacheEnricher>,
) {
    public fun encodeAll(dest: Cache) {
        val encoderContext =
            EncoderContext.server(paramTypes.filterTransmitKeys(), varpTypes.filterTransmitKeys())
        dest.use { cache ->
            encodeConfigs(cache, encoderContext)
            encodeMaps(cache, encoderContext)
        }
    }

    private fun encodeConfigs(cache: Cache, ctx: EncoderContext) {
        val locs = locEnrichments.collect(locTypes, LocTypeBuilder).asIterable()
        val npcs = npcEnrichments.collect(npcTypes, NpcTypeBuilder).asIterable()
        val objs = objEnrichments.collect(objTypes, ObjTypeBuilder).asIterable()
        LocTypeEncoder.encodeAll(cache, locs, ctx)
        NpcTypeEncoder.encodeAll(cache, npcs, ctx)
        ObjTypeEncoder.encodeAll(cache, objs, ctx)
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

    private fun encodeMaps(cache: Cache, ctx: EncoderContext) {
        val areas = areaEnrichments.flatMap(AreaCacheEnricher::generate).collect()
        MapAreaEncoder.encodeAll(cache, areas, ctx)
    }

    private fun Iterable<EnrichedAreaConfig>.collect(): Map<MapSquareKey, MapAreaDefinition> {
        val merged = mutableMapOf<MapSquareKey, MapAreaDefinition>()
        for (config in this) {
            val key = config.square
            val existing = merged[key]
            if (existing == null) {
                merged[key] = config.areas
                continue
            }
            val combined = MapAreaDefinition.merge(config.areas, existing)
            merged[key] = combined
        }
        return merged
    }
}
