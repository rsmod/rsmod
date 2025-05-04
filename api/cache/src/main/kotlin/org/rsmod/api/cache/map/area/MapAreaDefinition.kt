package org.rsmod.api.cache.map.area

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortArraySet
import it.unimi.dsi.fastutil.shorts.ShortSet
import org.rsmod.game.area.polygon.PolygonMapSquare

public data class MapAreaDefinition(
    val mapSquareAreas: ShortSet,
    val zoneAreas: Byte2ObjectMap<ShortSet>,
    val coordAreas: Short2ObjectMap<ShortSet>,
) {
    public companion object {
        public fun from(polygon: PolygonMapSquare): MapAreaDefinition {
            return polygon.toMapAreaDefinition()
        }

        private fun PolygonMapSquare.toMapAreaDefinition(): MapAreaDefinition {
            val flippedZoneAreas = Byte2ObjectOpenHashMap<ShortSet>()
            for ((area, bitset) in zoneAreas) {
                var index = bitset.nextSetBit(0)
                while (index != -1) {
                    val zoneKey = index.toByte()
                    val areaSet = flippedZoneAreas.getOrPut(zoneKey) { ShortArraySet() }
                    areaSet.add(area)
                    index = bitset.nextSetBit(index + 1)
                }
            }

            val flippedCoordAreas = Short2ObjectOpenHashMap<ShortSet>()
            for ((area, bitset) in coordAreas) {
                var index = bitset.nextSetBit(0)
                while (index != -1) {
                    val areaSet = flippedCoordAreas.getOrPut(index.toShort()) { ShortArraySet() }
                    areaSet.add(area)
                    index = bitset.nextSetBit(index + 1)
                }
            }

            return MapAreaDefinition(
                mapSquareAreas = mapSquareAreas,
                zoneAreas = flippedZoneAreas,
                coordAreas = flippedCoordAreas,
            )
        }

        public fun merge(edit: MapAreaDefinition, base: MapAreaDefinition): MapAreaDefinition {
            // TODO: Ensure area count doesn't go over 5 for coords (including zones+entire square).
            val mergedMapSquares =
                ShortArraySet(base.mapSquareAreas.size + edit.mapSquareAreas.size)
            mergedMapSquares.addAll(base.mapSquareAreas)
            mergedMapSquares.addAll(edit.mapSquareAreas)

            val mergedZoneAreas =
                Byte2ObjectOpenHashMap<ShortSet>(base.zoneAreas.size + edit.zoneAreas.size)
            for ((zone, areas) in base.zoneAreas) {
                mergedZoneAreas[zone] = ShortArraySet(areas)
            }
            for ((zone, areas) in edit.zoneAreas) {
                val mergedSet = mergedZoneAreas.getOrPut(zone) { ShortArraySet() }
                mergedSet.addAll(areas)
            }

            val mergedCoordAreas =
                Short2ObjectOpenHashMap<ShortSet>(base.coordAreas.size + edit.coordAreas.size)
            for ((coord, areas) in base.coordAreas) {
                mergedCoordAreas[coord] = ShortArraySet(areas)
            }
            for ((coord, areas) in edit.coordAreas) {
                val mergedSet = mergedCoordAreas.getOrPut(coord) { ShortArraySet() }
                mergedSet.addAll(areas)
            }

            return MapAreaDefinition(
                mapSquareAreas = mergedMapSquares,
                zoneAreas = mergedZoneAreas,
                coordAreas = mergedCoordAreas,
            )
        }
    }
}
