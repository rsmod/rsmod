package org.rsmod.api.type.updater

import jakarta.inject.Inject
import java.nio.file.Path
import org.openrs2.cache.Cache
import org.rsmod.annotations.EnrichedCache
import org.rsmod.annotations.GameCache
import org.rsmod.annotations.Js5Cache
import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.api.cache.map.area.MapAreaEncoder
import org.rsmod.api.cache.map.loc.MapLocListDefinition
import org.rsmod.api.cache.map.loc.MapLocListEncoder
import org.rsmod.api.cache.map.npc.MapNpcListDefinition
import org.rsmod.api.cache.map.npc.MapNpcListEncoder
import org.rsmod.api.cache.map.obj.MapObjListDefinition
import org.rsmod.api.cache.map.obj.MapObjListEncoder
import org.rsmod.api.cache.map.tile.MapTileByteDefinition
import org.rsmod.api.cache.map.tile.MapTileByteEncoder
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.type.builders.map.MapBuilderList
import org.rsmod.api.type.builders.map.MapTypeCollector
import org.rsmod.api.type.builders.map.area.MapAreaBuilder
import org.rsmod.api.type.builders.map.loc.MapLocSpawnBuilder
import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder
import org.rsmod.api.type.builders.map.tile.MapTileBuilder
import org.rsmod.game.map.xtea.XteaMap
import org.rsmod.map.square.MapSquareKey

public class TypeUpdaterMaps
@Inject
constructor(
    @Js5Cache private val js5CachePath: Path,
    @GameCache private val gameCachePath: Path,
    @EnrichedCache private val enrichedCache: Cache,
    private val collector: MapTypeCollector,
    private val xteaMap: XteaMap,
) {
    public fun updateAll(builders: MapBuilderList) {
        encodeAll(builders)
        cleanupAll(builders)
    }

    private fun encodeAll(builders: MapBuilderList) {
        val areas = collector.areas(builders.areas)
        val locs = collector.locs(builders.locs)
        val maps = collector.maps(builders.maps)
        val npcs = collector.npcs(builders.npcs)
        val objs = collector.objs(builders.objs)
        val updates = MapUpdates(areas, locs, maps, npcs, objs)

        val serverCtx = EncoderContext.server(emptySet(), emptySet(), emptySet())
        encodeCacheMaps(updates, gameCachePath, serverCtx)

        val clientCtx = EncoderContext.client(emptySet(), emptySet(), emptySet())
        encodeCacheMaps(updates, js5CachePath, clientCtx)
    }

    private fun cleanupAll(builders: MapBuilderList) {
        builders.areas.forEach(MapAreaBuilder::cleanup)
        builders.locs.forEach(MapLocSpawnBuilder::cleanup)
        builders.maps.forEach(MapTileBuilder::cleanup)
        builders.npcs.forEach(MapNpcSpawnBuilder::cleanup)
        builders.objs.forEach(MapObjSpawnBuilder::cleanup)
    }

    private data class MapUpdates(
        val areas: Map<MapSquareKey, MapAreaDefinition>,
        val locs: Map<MapSquareKey, MapLocListDefinition>,
        val maps: Map<MapSquareKey, MapTileByteDefinition>,
        val npcs: Map<MapSquareKey, MapNpcListDefinition>,
        val objs: Map<MapSquareKey, MapObjListDefinition>,
    )

    private fun encodeCacheMaps(updates: MapUpdates, cachePath: Path, ctx: EncoderContext) {
        Cache.open(cachePath).use { cache ->
            MapAreaEncoder.encodeAll(cache, updates.areas, ctx)
            MapLocListEncoder.encodeAll(cache, updates.locs, xteaMap)
            MapTileByteEncoder.encodeAll(cache, updates.maps)
            MapNpcListEncoder.encodeAll(cache, updates.npcs, ctx)
            MapObjListEncoder.encodeAll(cache, updates.objs, ctx)
        }
    }
}
