package org.rsmod.api.cache.map.area

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMaps
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectMaps
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortArraySet
import it.unimi.dsi.fastutil.shorts.ShortSet
import it.unimi.dsi.fastutil.shorts.ShortSets
import org.rsmod.api.cache.util.InlineByteBuf

public object MapAreaDecoder {
    public fun decode(buf: InlineByteBuf): MapAreaDefinition {
        var cursor = buf.newCursor()

        // Decode areas associated with the entire map square block (all 64x64x4 tiles).
        cursor = buf.readUnsignedByte(cursor)
        val fullMapSqAreaCount = cursor.value
        val fullMapSqAreas: ShortSet =
            if (fullMapSqAreaCount == 0) {
                ShortSets.emptySet()
            } else {
                val areas = ShortArraySet()
                repeat(fullMapSqAreaCount) {
                    cursor = buf.readShort(cursor)
                    val area = cursor.value.toShort()
                    areas.add(area)
                }
                areas
            }

        // Decode areas associated with entire zone blocks (8x8 tiles).
        cursor = buf.readUnsignedByte(cursor)
        val zoneCount = cursor.value
        val zoneAreas: Byte2ObjectMap<ShortSet> =
            if (zoneCount == 0) {
                Byte2ObjectMaps.emptyMap()
            } else {
                val areas = Byte2ObjectOpenHashMap<ShortSet>()
                repeat(zoneCount) {
                    cursor = buf.readByte(cursor)
                    val localZone = cursor.value.toByte()

                    cursor = buf.readUnsignedByte(cursor)
                    val areaCount = cursor.value

                    val areaSet = ShortArraySet(areaCount)
                    repeat(areaCount) {
                        cursor = buf.readShort(cursor)
                        val area = cursor.value.toShort()
                        areaSet.add(area)
                    }
                    areas[localZone] = areaSet
                }
                areas
            }

        // Decode individual tile areas.
        cursor = buf.readShort(cursor)
        val coordCount = cursor.value
        val coordAreas: Short2ObjectMap<ShortSet> =
            if (coordCount == 0) {
                Short2ObjectMaps.emptyMap()
            } else {
                val areas = Short2ObjectOpenHashMap<ShortSet>()
                repeat(coordCount) {
                    cursor = buf.readShort(cursor)
                    val grid = cursor.value.toShort()

                    cursor = buf.readUnsignedByte(cursor)
                    val areaCount = cursor.value

                    val areaSet = ShortArraySet(areaCount)
                    repeat(areaCount) {
                        cursor = buf.readShort(cursor)
                        val area = cursor.value.toShort()
                        areaSet.add(area)
                    }
                    areas[grid] = areaSet
                }
                areas
            }

        return MapAreaDefinition(
            mapSquareAreas = fullMapSqAreas,
            zoneAreas = zoneAreas,
            coordAreas = coordAreas,
        )
    }
}
