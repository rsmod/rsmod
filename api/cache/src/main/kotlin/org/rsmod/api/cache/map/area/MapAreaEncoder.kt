package org.rsmod.api.cache.map.area

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.readOrNull
import org.rsmod.game.area.AreaIndex
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.util.LocalMapSquareZone
import org.rsmod.map.zone.ZoneKey

public object MapAreaEncoder {
    private const val MAX_AREAS_PER_COORD = AreaIndex.MAX_AREAS_PER_KEY

    public fun encodeAll(
        cache: Cache,
        areas: Map<MapSquareKey, MapAreaDefinition>,
        ctx: EncoderContext,
    ) {
        // Map areas are a server-only group.
        if (ctx.clientOnly) {
            return
        }
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.MAPS
        for ((key, area) in areas) {
            validateAreaLimits(key, area)

            val group = "a${key.x}_${key.z}"
            val oldBuf = cache.readOrNull(archive, group, file = 0)
            val newBuf = buffer.clear().apply { encode(area, this) }
            if (newBuf != oldBuf) {
                cache.write(archive, group, file = 0, newBuf)
            }
            oldBuf?.release()
        }
        buffer.release()
    }

    public fun encode(area: MapAreaDefinition, data: ByteBuf): Unit =
        with(area) {
            data.writeByte(mapSquareAreas.size)
            for (area in mapSquareAreas.iterator()) {
                data.writeShort(area.toInt())
            }

            data.writeByte(zoneAreas.size)
            for ((packed, areas) in zoneAreas) {
                check(areas.isNotEmpty()) {
                    val localZone = LocalMapSquareZone(packed.toInt())
                    "Area set for zone should not be empty: zone=$localZone, def=$area"
                }
                check(areas.size <= 255) {
                    val localZone = LocalMapSquareZone(packed.toInt())
                    "Area count for zone should not exceed 255: zone=$localZone, def=$area"
                }
                data.writeByte(packed.toInt())
                data.writeByte(areas.size)
                for (area in areas.iterator()) {
                    data.writeShort(area.toInt())
                }
            }

            data.writeShort(coordAreas.size)
            for ((packed, areas) in coordAreas) {
                check(areas.isNotEmpty()) {
                    val grid = MapSquareGrid(packed.toInt())
                    "Area set for grid should not be empty: grid=$grid, def=$area"
                }
                check(areas.size <= 255) {
                    val grid = MapSquareGrid(packed.toInt())
                    "Area count for grid should not exceed 255: grid=$grid, def=$area"
                }
                data.writeShort(packed.toInt())
                data.writeByte(areas.size)
                for (area in areas.iterator()) {
                    data.writeShort(area.toInt())
                }
            }
        }

    public fun validateAreaLimits(mapSquare: MapSquareKey, area: MapAreaDefinition) {
        val mapSquareCoord = mapSquare.toCoords(level = 0)
        val mapSquareAreaCount = area.mapSquareAreas.size
        if (mapSquareAreaCount > MAX_AREAS_PER_COORD) {
            val message =
                "MapSquare cannot be associated with more than $MAX_AREAS_PER_COORD areas: " +
                    "mapSquare=$mapSquare, areaCount=$mapSquareAreaCount"
            throw IllegalStateException(message)
        }

        val cachedZoneAreaCounts =
            ByteArray(LocalMapSquareZone.LENGTH * LocalMapSquareZone.LENGTH * CoordGrid.LEVEL_COUNT)
        for ((packed, areas) in area.zoneAreas) {
            val localZone = LocalMapSquareZone(packed.toInt())
            cachedZoneAreaCounts[localZone.packed and 0xFF] = areas.size.toByte()

            val totalAreas = mapSquareAreaCount + areas.size
            if (totalAreas > MAX_AREAS_PER_COORD) {
                val zoneCoord = mapSquareCoord.translate(localZone.x, localZone.z, localZone.level)
                val zoneKey = ZoneKey.from(zoneCoord)
                val message =
                    "Zone cannot be associated with more than $MAX_AREAS_PER_COORD areas: " +
                        "zone=$zoneKey, areaCount=$totalAreas, " +
                        "(mapSquareAreas=$mapSquareAreaCount, zoneAreas=${areas.size})"
                throw IllegalStateException(message)
            }
        }

        for ((packed, areas) in area.coordAreas) {
            val grid = MapSquareGrid(packed.toInt())
            val coord = mapSquareCoord.translate(grid.x, grid.z, grid.level)
            val coordZoneKey = ZoneKey.from(coord)
            val localZone = LocalMapSquareZone.from(coordZoneKey)
            val zoneAreaCount = cachedZoneAreaCounts[localZone.packed and 0xFF]

            val totalAreas = mapSquareAreaCount + zoneAreaCount + areas.size
            if (totalAreas > MAX_AREAS_PER_COORD) {
                val message =
                    "Coord cannot be associated with more than $MAX_AREAS_PER_COORD areas: " +
                        "coord=$coord, areaCount=$totalAreas, " +
                        "(mapSquareAreas=$mapSquareAreaCount, " +
                        "zoneAreas=$zoneAreaCount, " +
                        "coordAreas=${areas.size})"
                throw IllegalStateException(message)
            }
        }
    }
}
