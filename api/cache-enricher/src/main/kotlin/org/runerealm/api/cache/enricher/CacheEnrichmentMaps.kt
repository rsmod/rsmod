package org.runerealm.api.cache.enricher

import jakarta.inject.Inject
import org.openrs2.cache.Cache
import org.runerealm.api.cache.Js5Archives
import org.runerealm.api.cache.enricher.map.area.AreaCacheEnricher
import org.runerealm.api.cache.enricher.map.area.EnrichedAreaConfig
import org.runerealm.api.cache.map.area.MapAreaDecoder
import org.runerealm.api.cache.map.area.MapAreaDefinition
import org.runerealm.api.cache.map.area.MapAreaEncoder
import org.runerealm.api.cache.util.EncoderContext
import org.runerealm.api.cache.util.readOrNull
import org.runerealm.api.cache.util.toInlineBuf
import org.runerealm.game.type.dbtable.DbTableTypeList
import org.runerealm.game.type.param.ParamTypeList
import org.runerealm.game.type.varp.VarpTypeList
import org.runerealm.map.square.MapSquareKey

public class CacheEnrichmentMaps
@Inject
constructor(
    private val paramTypes: ParamTypeList,
    private val varpTypes: VarpTypeList,
    private val dbTableTypes: DbTableTypeList,
    private val areaEnrichments: Set<AreaCacheEnricher>,
) {
    public fun encodeAll(dest: Cache) {
        val ctx = encoderContext()
        val areas = areaEnrichments.flatMap(AreaCacheEnricher::generate).collect(dest)
        MapAreaEncoder.encodeAll(dest, areas, ctx)
    }

    private fun Iterable<EnrichedAreaConfig>.collect(
        cache: Cache
    ): Map<MapSquareKey, MapAreaDefinition> {
        val merged = loadCacheAreas(cache)
        for (config in this) {
            merged.merge(config.square, config.areas, MapAreaDefinition::merge)
        }
        return merged
    }

    private fun Iterable<EnrichedAreaConfig>.loadCacheAreas(
        cache: Cache
    ): MutableMap<MapSquareKey, MapAreaDefinition> {
        val areas = mutableMapOf<MapSquareKey, MapAreaDefinition>()
        for (config in this) {
            val (mx, mz) = config.square
            val buffer = cache.readOrNull(Js5Archives.MAPS, "a${mx}_$mz") ?: continue
            val area = MapAreaDecoder.decode(buffer.toInlineBuf())
            areas[config.square] = area
        }
        return areas
    }

    private fun encoderContext(): EncoderContext {
        return EncoderContext.server(
            params = paramTypes.filterTransmitKeys(),
            varps = varpTypes.filterTransmitKeys(),
            tables = dbTableTypes.filterTransmitKeys(),
        )
    }
}
