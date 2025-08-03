package org.rsmod.server.shared.util

import org.rsmod.api.type.builders.clientscript.ClientScriptBuilder
import org.rsmod.api.type.builders.map.MapBuilderList
import org.rsmod.api.type.builders.map.area.MapAreaBuilder
import org.rsmod.api.type.builders.map.loc.MapLocSpawnBuilder
import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder
import org.rsmod.api.type.builders.map.tile.MapTileBuilder
import org.rsmod.api.type.builders.resource.ResourceTypeBuilder
import org.rsmod.api.type.builders.resource.TypeResourcePack
import org.rsmod.server.shared.loader.ResourceTypeBuilderLoader

object ResourceBuilderListLoader {
    fun load(loader: ResourceTypeBuilderLoader): TypeResourcePack {
        val builders = loader.load()
        return create(builders)
    }

    private fun create(builders: Collection<ResourceTypeBuilder>): TypeResourcePack {
        val areas = builders.filterIsInstance<MapAreaBuilder>()
        val locs = builders.filterIsInstance<MapLocSpawnBuilder>()
        val tiles = builders.filterIsInstance<MapTileBuilder>()
        val npcs = builders.filterIsInstance<MapNpcSpawnBuilder>()
        val objs = builders.filterIsInstance<MapObjSpawnBuilder>()
        val maps = MapBuilderList(areas, locs, tiles, npcs, objs)
        val clientscripts = builders.filterIsInstance<ClientScriptBuilder>()
        return TypeResourcePack(maps, clientscripts)
    }
}
