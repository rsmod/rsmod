package org.rsmod.api.type.updater

import jakarta.inject.Inject
import java.nio.file.Path
import org.openrs2.cache.Cache
import org.rsmod.annotations.EnrichedCache
import org.rsmod.annotations.GameCache
import org.rsmod.annotations.Js5Cache
import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.api.cache.map.area.MapAreaEncoder
import org.rsmod.api.cache.map.npc.MapNpcListDefinition
import org.rsmod.api.cache.map.npc.MapNpcListEncoder
import org.rsmod.api.cache.map.obj.MapObjListDefinition
import org.rsmod.api.cache.map.obj.MapObjListEncoder
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.type.builders.map.MapBuilderList
import org.rsmod.api.type.builders.map.MapTypeCollector
import org.rsmod.map.square.MapSquareKey

public class TypeUpdaterMaps
@Inject
constructor(
    @Js5Cache private val js5CachePath: Path,
    @GameCache private val gameCachePath: Path,
    @EnrichedCache private val enrichedCache: Cache,
    private val collector: MapTypeCollector,
) {
    public fun updateAll(builders: MapBuilderList) {
        encodeAll(builders)
    }

    private fun encodeAll(builders: MapBuilderList) {
        val areas = collector.areas(builders.areas)
        val npcs = collector.npcs(builders.npcs)
        val objs = collector.objs(builders.objs)
        val updates = MapUpdates(areas, npcs, objs)
        encodeCacheMaps(updates, gameCachePath, EncoderContext.server(emptySet(), emptySet()))
        encodeCacheMaps(updates, js5CachePath, EncoderContext.client(emptySet(), emptySet()))
    }

    private data class MapUpdates(
        val areas: Map<MapSquareKey, MapAreaDefinition>,
        val npcs: Map<MapSquareKey, MapNpcListDefinition>,
        val objs: Map<MapSquareKey, MapObjListDefinition>,
    )

    private fun encodeCacheMaps(updates: MapUpdates, cachePath: Path, ctx: EncoderContext) {
        Cache.open(cachePath).use { cache ->
            MapAreaEncoder.encodeAll(cache, updates.areas, ctx)
            MapNpcListEncoder.encodeAll(cache, updates.npcs, ctx)
            MapObjListEncoder.encodeAll(cache, updates.objs, ctx)
        }
    }
}
