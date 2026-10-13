package org.runerealm.api.cache.enricher

import jakarta.inject.Inject
import org.openrs2.cache.Cache
import org.runerealm.api.cache.enricher.loc.LocCacheEnricher
import org.runerealm.api.cache.enricher.npc.NpcCacheEnricher
import org.runerealm.api.cache.enricher.obj.ObjCacheEnricher
import org.runerealm.api.cache.types.loc.LocTypeEncoder
import org.runerealm.api.cache.types.npc.NpcTypeEncoder
import org.runerealm.api.cache.types.obj.ObjTypeEncoder
import org.runerealm.api.cache.util.EncoderContext
import org.runerealm.game.type.CacheType
import org.runerealm.game.type.dbtable.DbTableTypeList
import org.runerealm.game.type.loc.LocTypeBuilder
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.npc.NpcTypeBuilder
import org.runerealm.game.type.npc.NpcTypeList
import org.runerealm.game.type.obj.ObjTypeBuilder
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.param.ParamTypeList
import org.runerealm.game.type.util.MergeableCacheBuilder
import org.runerealm.game.type.varp.VarpTypeList

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
    private val dbTableTypes: DbTableTypeList,
) {
    public fun encodeAll(dest: Cache) {
        val ctx = encoderContext()
        val locs = locEnrichments.collect(locTypes, LocTypeBuilder).asIterable()
        val npcs = npcEnrichments.collect(npcTypes, NpcTypeBuilder).asIterable()
        val objs = objEnrichments.collect(objTypes, ObjTypeBuilder).asIterable()
        LocTypeEncoder.encodeAll(dest, locs, ctx)
        NpcTypeEncoder.encodeAll(dest, npcs, ctx)
        ObjTypeEncoder.encodeAll(dest, objs, ctx)
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

    private fun encoderContext(): EncoderContext {
        return EncoderContext.server(
            params = paramTypes.filterTransmitKeys(),
            varps = varpTypes.filterTransmitKeys(),
            tables = dbTableTypes.filterTransmitKeys(),
        )
    }
}
