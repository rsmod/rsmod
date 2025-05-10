package org.rsmod.server.shared.util

import org.rsmod.api.type.builders.map.MapBuilderList
import org.rsmod.api.type.builders.map.MapTypeBuilder
import org.rsmod.api.type.builders.map.area.MapAreaBuilder
import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder
import org.rsmod.server.shared.loader.MapTypeBuilderLoader

object MapBuilderListLoader {
    fun load(loader: MapTypeBuilderLoader): MapBuilderList {
        val builders = loader.load()
        return create(builders)
    }

    private fun create(builders: Collection<MapTypeBuilder>): MapBuilderList {
        val areas = builders.filterIsInstance<MapAreaBuilder>()
        val npcs = builders.filterIsInstance<MapNpcSpawnBuilder>()
        val objs = builders.filterIsInstance<MapObjSpawnBuilder>()
        return MapBuilderList(areas, npcs, objs)
    }
}
