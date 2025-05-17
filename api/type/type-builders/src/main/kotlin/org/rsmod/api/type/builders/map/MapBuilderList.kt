package org.rsmod.api.type.builders.map

import org.rsmod.api.type.builders.map.area.MapAreaBuilder
import org.rsmod.api.type.builders.map.loc.MapLocSpawnBuilder
import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder

public data class MapBuilderList(
    val areas: Collection<MapAreaBuilder>,
    val locs: Collection<MapLocSpawnBuilder>,
    val npcs: Collection<MapNpcSpawnBuilder>,
    val objs: Collection<MapObjSpawnBuilder>,
)
