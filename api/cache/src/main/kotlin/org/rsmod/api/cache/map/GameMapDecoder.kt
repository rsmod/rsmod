package org.rsmod.api.cache.map

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.openrs2.crypto.SymmetricKey
import org.rsmod.annotations.GameCache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.map.MapDefinition.Companion.LINK_BELOW
import org.rsmod.api.cache.map.loc.MapLoc
import org.rsmod.api.cache.map.loc.MapLocDefinition
import org.rsmod.api.cache.util.InlineByteBuf
import org.rsmod.api.cache.util.toInlineBuf
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.game.map.collision.toggleLoc
import org.rsmod.game.map.xtea.XteaMap
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag
import org.rsmod.routefinder.loc.LocLayerConstants

public class GameMapDecoder
@Inject
constructor(
    @GameCache private val gameCache: Cache,
    private val collision: CollisionFlagMap,
    private val locZones: LocZoneStorage,
    private val locTypes: LocTypeList,
    private val xteaMap: XteaMap,
) {
    public fun decodeAll(): Unit = runBlocking { launchDecodeAll() }

    private fun CoroutineScope.launchDecodeAll() =
        launch(Dispatchers.IO) {
            val mapBuffers = gameCache.readMapBuffers(xteaMap)
            val decodedMaps = mapBuffers.decodeAll()
            decodedMaps.putMapCollision()
            val locBuilders = decodedMaps.computeLocBuilders()
            for (builder in locBuilders) {
                locZones.putAll(builder)
            }
        }

    private fun Cache.readMapBuffers(xteaMap: XteaMap): List<MapBuffer> =
        xteaMap.map { (mapSquareKey, keyArray) ->
            val name = "${mapSquareKey.x}_${mapSquareKey.z}"
            val key = SymmetricKey.fromIntArray(keyArray)
            val map = read(Js5Archives.MAPS, "m$name", file = 0).use { it.toInlineBuf() }
            val loc = read(Js5Archives.MAPS, "l$name", file = 0, key).use { it.toInlineBuf() }
            MapBuffer(mapSquareKey, map, loc)
        }

    private suspend fun List<MapBuffer>.decodeAll() = supervisorScope {
        val decoded = ArrayList<Deferred<DecodedMap>>(size)
        for (buffer in this@decodeAll) {
            decoded += async { buffer.decode() }
        }
        decoded.awaitAll()
    }

    private suspend fun List<DecodedMap>.putMapCollision() = supervisorScope {
        for (decoded in this@putMapCollision) {
            async { putMaps(collision, decoded.key, decoded.map) }
        }
    }

    private fun List<DecodedMap>.computeLocBuilders(): Iterable<GameMapBuilder> {
        val builder = GameMapBuilder().apply { putLocs(this@computeLocBuilders) }
        return listOf(builder)
    }

    private fun GameMapBuilder.putLocs(decodedMaps: List<DecodedMap>) {
        for (decoded in decodedMaps) {
            putLocs(this, collision, locTypes, decoded.key, decoded.map, decoded.locs)
        }
    }

    private fun LocZoneStorage.putAll(builder: GameMapBuilder) {
        for ((zoneKey, zoneBuilder) in builder.zoneBuilders) {
            mapLocs[zoneKey] = zoneBuilder.build()
        }
    }

    public companion object {
        public fun putMaps(
            collision: CollisionFlagMap,
            square: MapSquareKey,
            mapDef: SimpleMapDefinition,
        ) {
            val baseX = square.x * MapSquareGrid.LENGTH
            val baseZ = square.z * MapSquareGrid.LENGTH
            for (level in 0 until CoordGrid.LEVEL_COUNT) {
                for (x in 0 until MapSquareGrid.LENGTH) {
                    for (z in 0 until MapSquareGrid.LENGTH) {
                        // As an optimization, we use the `SimpleMapDefinition.BRIDGE` flag to
                        // make sure we don't add collision for tiles such as, and primarily,
                        // bridges. If for some reason we'd like to remove the custom `BRIDGE`
                        // flag, we have to use the `rule` function for `flags` here, so that
                        // the above tile flags are taken into account when applicable.
                        val flags = mapDef[x, z, level].toInt()
                        if (flags == 0) {
                            continue
                        }
                        val absX = baseX + x
                        val absZ = baseZ + z
                        if (level == 0 && (flags and SimpleMapDefinition.BRIDGE) != 0) {
                            continue
                        }
                        if ((flags and SimpleMapDefinition.BLOCK_MAP_SQUARE) != 0) {
                            collision.add(absX, absZ, level, CollisionFlag.BLOCK_WALK)
                        }
                        if ((flags and SimpleMapDefinition.REMOVE_ROOFS) != 0) {
                            collision.add(absX, absZ, level, CollisionFlag.ROOF)
                        }
                        if ((flags and SimpleMapDefinition.COLOURED) != 0) {
                            collision.allocateIfAbsent(absX, absZ, level)
                        }
                    }
                }
            }
        }

        public fun putLocs(
            mapBuilder: GameMapBuilder,
            collision: CollisionFlagMap,
            locTypes: LocTypeList,
            square: MapSquareKey,
            mapDef: SimpleMapDefinition,
            locDef: MapLocDefinition,
        ): Unit =
            with(mapBuilder) {
                for (packedLoc in locDef) {
                    val loc = MapLoc(packedLoc)
                    val local = MapSquareGrid(loc.localX, loc.localZ, loc.level)
                    val tileFlags = mapDef[local.x, local.z, local.level].toInt()
                    val tileAboveFlags =
                        if (local.level >= CoordGrid.LEVEL_COUNT - 1) {
                            tileFlags
                        } else {
                            mapDef[local.x, local.z, local.level + 1].toInt()
                        }
                    val resolvedTileFlags =
                        if ((tileAboveFlags and LINK_BELOW) != 0) {
                            tileAboveFlags
                        } else {
                            tileFlags
                        }
                    // Take into account that any tile that has this bit flag will cause locs below
                    // it to "visually" go one level down.
                    val visualLevel =
                        if ((resolvedTileFlags and SimpleMapDefinition.LINK_BELOW) != 0) {
                            loc.level - 1
                        } else {
                            loc.level
                        }
                    if (visualLevel < 0) {
                        continue
                    }
                    val coords =
                        square
                            .toCoords(0.coerceAtLeast(visualLevel))
                            .translate(loc.localX, loc.localZ)
                    val zoneGridX = coords.x and ZoneGrid.X_BIT_MASK
                    val zoneGridZ = coords.z and ZoneGrid.Z_BIT_MASK
                    val zone = computeIfAbsent(ZoneKey.from(coords)) { ZoneBuilder() }
                    val layer = LocLayerConstants.of(loc.shape)
                    val entity = LocEntity(loc.id, loc.shape, loc.angle)
                    val type = locTypes.getValue(loc.id)
                    zone.add(zoneGridX, zoneGridZ, layer, entity)
                    collision.toggleLoc(
                        coords = coords,
                        width = type.width,
                        length = type.length,
                        shape = loc.shape,
                        angle = loc.angle,
                        blockWalk = type.blockWalk,
                        blockRange = type.blockRange,
                        breakRouteFinding = type.breakRouteFinding,
                        add = true,
                    )
                }
            }
    }
}

private class MapBuffer(val key: MapSquareKey, val map: InlineByteBuf, val locs: InlineByteBuf) {
    fun decode(): DecodedMap {
        val mapDef = MapDefinitionDecoder.decodeMap(map)
        val locDef = MapDefinitionDecoder.decodeLocs(locs)
        return DecodedMap(key, mapDef, locDef)
    }
}

private data class DecodedMap(
    val key: MapSquareKey,
    val map: SimpleMapDefinition,
    val locs: MapLocDefinition,
)
