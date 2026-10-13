package org.runerealm.api.type.updater

import jakarta.inject.Inject
import java.nio.file.Path
import org.openrs2.cache.Cache
import org.runerealm.annotations.EnrichedCache
import org.runerealm.annotations.GameCache
import org.runerealm.annotations.Js5Cache
import org.runerealm.api.cache.map.area.MapAreaDefinition
import org.runerealm.api.cache.map.area.MapAreaEncoder
import org.runerealm.api.cache.map.loc.MapLocListDefinition
import org.runerealm.api.cache.map.loc.MapLocListEncoder
import org.runerealm.api.cache.map.npc.MapNpcListDefinition
import org.runerealm.api.cache.map.npc.MapNpcListEncoder
import org.runerealm.api.cache.map.obj.MapObjListDefinition
import org.runerealm.api.cache.map.obj.MapObjListEncoder
import org.runerealm.api.cache.map.tile.MapTileByteDefinition
import org.runerealm.api.cache.map.tile.MapTileByteEncoder
import org.runerealm.api.cache.types.clientscript.ClientScriptByteDefinition
import org.runerealm.api.cache.types.clientscript.ClientScriptByteEncoder
import org.runerealm.api.cache.types.model.ModelByteDefinition
import org.runerealm.api.cache.types.model.ModelByteEncoder
import org.runerealm.api.cache.util.EncoderContext
import org.runerealm.api.type.builders.clientscript.ClientScriptBuilder
import org.runerealm.api.type.builders.clientscript.ClientScriptCollector
import org.runerealm.api.type.builders.map.MapBuilderList
import org.runerealm.api.type.builders.map.MapTypeCollector
import org.runerealm.api.type.builders.map.area.MapAreaBuilder
import org.runerealm.api.type.builders.map.loc.MapLocSpawnBuilder
import org.runerealm.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.runerealm.api.type.builders.map.obj.MapObjSpawnBuilder
import org.runerealm.api.type.builders.map.tile.MapTileBuilder
import org.runerealm.api.type.builders.model.ModelBuilder
import org.runerealm.api.type.builders.model.ModelCollector
import org.runerealm.api.type.builders.resource.TypeResourcePack
import org.runerealm.game.map.xtea.XteaMap
import org.runerealm.map.square.MapSquareKey

public class TypeUpdaterResources
@Inject
constructor(
    @Js5Cache private val js5CachePath: Path,
    @GameCache private val gameCachePath: Path,
    @EnrichedCache private val enrichedCache: Cache,
    private val xteaMap: XteaMap,
    private val map: MapTypeCollector,
    private val clientscripts: ClientScriptCollector,
    private val models: ModelCollector,
) {
    public fun updateAll(resources: TypeResourcePack) {
        val mapUpdates = toMapUpdates(resources.maps)
        val clientscriptUpdates = toClientScriptUpdate(resources.clientscripts)
        val modelUpdates = toModelUpdate(resources.models)

        val serverCtx = EncoderContext.server(emptySet(), emptySet(), emptySet())
        Cache.open(gameCachePath).use { cache ->
            encodeCacheMaps(mapUpdates, cache, serverCtx)
            encodeCacheClientScripts(clientscriptUpdates, cache)
            encodeCacheModels(modelUpdates, cache)
        }

        val clientCtx = EncoderContext.client(emptySet(), emptySet(), emptySet())
        Cache.open(js5CachePath).use { cache ->
            encodeCacheMaps(mapUpdates, cache, clientCtx)
            encodeCacheClientScripts(clientscriptUpdates, cache)
            encodeCacheModels(modelUpdates, cache)
        }

        cleanupMaps(resources.maps)
        cleanupClientScripts(resources.clientscripts)
        cleanupModels(resources.models)
    }

    private fun toMapUpdates(builders: MapBuilderList): MapUpdates {
        val areas = map.areas(builders.areas)
        val locs = map.locs(builders.locs)
        val npcs = map.npcs(builders.npcs)
        val objs = map.objs(builders.objs)
        val tiles = map.tiles(builders.tiles)
        return MapUpdates(areas, locs, tiles, npcs, objs)
    }

    private fun cleanupMaps(builders: MapBuilderList) {
        builders.areas.forEach(MapAreaBuilder::cleanup)
        builders.locs.forEach(MapLocSpawnBuilder::cleanup)
        builders.tiles.forEach(MapTileBuilder::cleanup)
        builders.npcs.forEach(MapNpcSpawnBuilder::cleanup)
        builders.objs.forEach(MapObjSpawnBuilder::cleanup)
    }

    private data class MapUpdates(
        val areas: Map<MapSquareKey, MapAreaDefinition>,
        val locs: Map<MapSquareKey, MapLocListDefinition>,
        val tiles: Map<MapSquareKey, MapTileByteDefinition>,
        val npcs: Map<MapSquareKey, MapNpcListDefinition>,
        val objs: Map<MapSquareKey, MapObjListDefinition>,
    )

    private fun encodeCacheMaps(updates: MapUpdates, cache: Cache, ctx: EncoderContext) {
        MapAreaEncoder.encodeAll(cache, updates.areas, ctx)
        MapLocListEncoder.encodeAll(cache, updates.locs, xteaMap)
        MapTileByteEncoder.encodeAll(cache, updates.tiles)
        MapNpcListEncoder.encodeAll(cache, updates.npcs, ctx)
        MapObjListEncoder.encodeAll(cache, updates.objs, ctx)
    }

    private fun toClientScriptUpdate(
        builders: Collection<ClientScriptBuilder>
    ): ClientScriptUpdate {
        val definitions = clientscripts.loadAndCollect(builders)
        return ClientScriptUpdate(definitions)
    }

    private fun cleanupClientScripts(builders: Collection<ClientScriptBuilder>) {
        builders.forEach(ClientScriptBuilder::cleanup)
    }

    private data class ClientScriptUpdate(val definitions: List<ClientScriptByteDefinition>)

    private fun encodeCacheClientScripts(update: ClientScriptUpdate, cache: Cache) {
        ClientScriptByteEncoder.encodeAll(cache, update.definitions)
    }

    private fun toModelUpdate(builders: Collection<ModelBuilder>): ModelUpdate {
        val definitions = models.loadAndCollect(builders)
        return ModelUpdate(definitions)
    }

    private fun cleanupModels(builders: Collection<ModelBuilder>) {
        builders.forEach(ModelBuilder::cleanup)
    }

    private data class ModelUpdate(val definitions: List<ModelByteDefinition>)

    private fun encodeCacheModels(update: ModelUpdate, cache: Cache) {
        ModelByteEncoder.encodeAll(cache, update.definitions)
    }
}
