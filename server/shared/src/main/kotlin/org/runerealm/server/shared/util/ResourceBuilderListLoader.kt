package org.runerealm.server.shared.util

import org.runerealm.api.type.builders.clientscript.ClientScriptBuilder
import org.runerealm.api.type.builders.map.MapBuilderList
import org.runerealm.api.type.builders.map.area.MapAreaBuilder
import org.runerealm.api.type.builders.map.loc.MapLocSpawnBuilder
import org.runerealm.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.runerealm.api.type.builders.map.obj.MapObjSpawnBuilder
import org.runerealm.api.type.builders.map.tile.MapTileBuilder
import org.runerealm.api.type.builders.model.ModelBuilder
import org.runerealm.api.type.builders.resource.ResourceTypeBuilder
import org.runerealm.api.type.builders.resource.TypeResourcePack
import org.runerealm.server.shared.loader.ResourceTypeBuilderLoader

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
        val models = builders.filterIsInstance<ModelBuilder>()
        return TypeResourcePack(maps, clientscripts, models)
    }
}
