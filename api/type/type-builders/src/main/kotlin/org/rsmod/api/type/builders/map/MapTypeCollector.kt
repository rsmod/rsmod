package org.rsmod.api.type.builders.map

import jakarta.inject.Inject
import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.api.cache.map.loc.MapLocListDefinition
import org.rsmod.api.cache.map.npc.MapNpcListDefinition
import org.rsmod.api.cache.map.obj.MapObjListDefinition
import org.rsmod.api.cache.map.tile.MapTileByteDefinition
import org.rsmod.api.type.builders.map.area.MapAreaBuilder
import org.rsmod.api.type.builders.map.area.MapAreaCollector
import org.rsmod.api.type.builders.map.loc.MapLocSpawnBuilder
import org.rsmod.api.type.builders.map.loc.MapLocSpawnCollector
import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.api.type.builders.map.npc.MapNpcSpawnCollector
import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder
import org.rsmod.api.type.builders.map.obj.MapObjSpawnCollector
import org.rsmod.api.type.builders.map.tile.MapTileBuilder
import org.rsmod.api.type.builders.map.tile.MapTileCollector
import org.rsmod.map.square.MapSquareKey

public class MapTypeCollector
@Inject
constructor(
    private val areas: MapAreaCollector,
    private val locs: MapLocSpawnCollector,
    private val tiles: MapTileCollector,
    private val npcs: MapNpcSpawnCollector,
    private val objs: MapObjSpawnCollector,
) {
    public fun areas(builders: Iterable<MapAreaBuilder>): Map<MapSquareKey, MapAreaDefinition> {
        return areas.loadAndCollect(builders)
    }

    public fun locs(
        builders: Iterable<MapLocSpawnBuilder>
    ): Map<MapSquareKey, MapLocListDefinition> {
        return locs.loadAndCollect(builders)
    }

    public fun tiles(builders: Iterable<MapTileBuilder>): Map<MapSquareKey, MapTileByteDefinition> {
        return tiles.loadAndCollect(builders)
    }

    public fun npcs(
        builders: Iterable<MapNpcSpawnBuilder>
    ): Map<MapSquareKey, MapNpcListDefinition> {
        return npcs.loadAndCollect(builders)
    }

    public fun objs(
        builders: Iterable<MapObjSpawnBuilder>
    ): Map<MapSquareKey, MapObjListDefinition> {
        return objs.loadAndCollect(builders)
    }
}
