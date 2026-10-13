package org.runerealm.api.type.builders.map

import org.runerealm.api.type.builders.map.area.MapAreaBuilder
import org.runerealm.api.type.builders.map.loc.MapLocSpawnBuilder
import org.runerealm.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.runerealm.api.type.builders.map.obj.MapObjSpawnBuilder
import org.runerealm.api.type.builders.map.tile.MapTileBuilder

public data class MapBuilderList(
    val areas: Collection<MapAreaBuilder>,
    val locs: Collection<MapLocSpawnBuilder>,
    val tiles: Collection<MapTileBuilder>,
    val npcs: Collection<MapNpcSpawnBuilder>,
    val objs: Collection<MapObjSpawnBuilder>,
)
