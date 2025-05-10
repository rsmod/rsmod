package org.rsmod.api.cache.enricher

import jakarta.inject.Inject
import org.openrs2.cache.Cache
import org.rsmod.api.cache.enricher.map.area.AreaCacheEnricher
import org.rsmod.api.cache.enricher.map.area.EnrichedAreaConfig
import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.api.cache.map.area.MapAreaEncoder
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.game.type.param.ParamTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.map.square.MapSquareKey

public class CacheEnrichmentMaps
@Inject
constructor(
    private val paramTypes: ParamTypeList,
    private val varpTypes: VarpTypeList,
    private val areaEnrichments: Set<AreaCacheEnricher>,
) {
    public fun encodeAll(dest: Cache) {
        val encoderContext =
            EncoderContext.server(paramTypes.filterTransmitKeys(), varpTypes.filterTransmitKeys())
        val areas = areaEnrichments.flatMap(AreaCacheEnricher::generate).collect()
        MapAreaEncoder.encodeAll(dest, areas, encoderContext)
    }

    // TODO: Load necessary map area from "a{x}_{z}" in cache so they can be merged here, otherwise
    //  they will be overwritten.
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
