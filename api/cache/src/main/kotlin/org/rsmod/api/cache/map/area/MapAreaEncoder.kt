package org.rsmod.api.cache.map.area

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.readOrNull
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.util.LocalMapSquareZone

public object MapAreaEncoder {
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
}
