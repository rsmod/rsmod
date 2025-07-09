package org.rsmod.api.cache.map.area

import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortArraySet
import it.unimi.dsi.fastutil.shorts.ShortSet
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.util.LocalMapSquareZone

class MapAreaDefinitionLimitTest {
    // Mapsquare tests.

    @Test
    fun `validate mapsquare area limit`() {
        val areas = createMapAreas {
            mapSquare(area = TEST_AREA_1)
            mapSquare(area = TEST_AREA_2)
            mapSquare(area = TEST_AREA_3)
            mapSquare(area = TEST_AREA_4)
            mapSquare(area = TEST_AREA_5)
        }
        assertDoesNotThrow { MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas) }
    }

    @Test
    fun `throw on mapsquare area limit exceeded`() {
        val areas = createMapAreas {
            mapSquare(area = TEST_AREA_1)
            mapSquare(area = TEST_AREA_2)
            mapSquare(area = TEST_AREA_3)
            mapSquare(area = TEST_AREA_4)
            mapSquare(area = TEST_AREA_5)
            mapSquare(area = TEST_AREA_6)
        }
        assertThrows<IllegalStateException> {
            MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas)
        }
    }

    // Zone tests.

    @Test
    fun `validate zone area limit`() {
        val areas = createMapAreas {
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_1)
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_2)
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_3)
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_4)
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_5)
        }
        assertDoesNotThrow { MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas) }
    }

    @Test
    fun `validate different zones have separate area limits`() {
        val areas = createMapAreas {
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_1)
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_2)
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_3)
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_4)
            zone(zoneX = 0, zoneZ = 1, level = 0, area = TEST_AREA_5)
            zone(zoneX = 1, zoneZ = 2, level = 0, area = TEST_AREA_6)
        }
        assertDoesNotThrow { MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas) }
    }

    @Test
    fun `throw on zone area limit exceeded`() {
        val areas = createMapAreas {
            zone(zoneX = 2, zoneZ = 1, level = 1, area = TEST_AREA_1)
            zone(zoneX = 2, zoneZ = 1, level = 1, area = TEST_AREA_2)
            zone(zoneX = 2, zoneZ = 1, level = 1, area = TEST_AREA_3)
            zone(zoneX = 2, zoneZ = 1, level = 1, area = TEST_AREA_4)
            zone(zoneX = 2, zoneZ = 1, level = 1, area = TEST_AREA_5)
            zone(zoneX = 2, zoneZ = 1, level = 1, area = TEST_AREA_6)
        }
        assertThrows<IllegalStateException> {
            MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas)
        }
    }

    // CoordGrid tests.

    @Test
    fun `validate coord area limit`() {
        val areas = createMapAreas {
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_1)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_2)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_3)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_4)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_5)
        }
        assertDoesNotThrow { MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas) }
    }

    @Test
    fun `validate different coords have separate area limits`() {
        val areas = createMapAreas {
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_1)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_2)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_3)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_4)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_5)
            coord(mapSquareLocalX = 0, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_6)
        }
        assertDoesNotThrow { MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas) }
    }

    @Test
    fun `throw on coord area limit exceeded`() {
        val areas = createMapAreas {
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_1)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_2)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_3)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_4)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_5)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 10, level = 3, area = TEST_AREA_6)
        }
        assertThrows<IllegalStateException> {
            MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas)
        }
    }

    // Mapsquare + Zone tests.

    @Test
    fun `validate mapsquare plus zone area limit`() {
        val areas = createMapAreas {
            mapSquare(area = TEST_AREA_1)
            mapSquare(area = TEST_AREA_2)
            mapSquare(area = TEST_AREA_3)
            mapSquare(area = TEST_AREA_4)
            zone(zoneX = 5, zoneZ = 5, level = 1, area = TEST_AREA_5)
        }
        assertDoesNotThrow { MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas) }
    }

    @Test
    fun `throw on mapsquare plus zone area limit exceeded`() {
        val areas = createMapAreas {
            mapSquare(area = TEST_AREA_1)
            mapSquare(area = TEST_AREA_2)
            mapSquare(area = TEST_AREA_3)
            mapSquare(area = TEST_AREA_4)
            mapSquare(area = TEST_AREA_5)
            zone(zoneX = 5, zoneZ = 5, level = 1, area = TEST_AREA_6)
        }
        assertThrows<IllegalStateException> {
            MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas)
        }
    }

    @Test
    fun `throw on zone plus mapsquare area limit exceeded`() {
        val areas = createMapAreas {
            zone(zoneX = 6, zoneZ = 3, level = 0, area = TEST_AREA_1)
            zone(zoneX = 6, zoneZ = 3, level = 0, area = TEST_AREA_2)
            zone(zoneX = 6, zoneZ = 3, level = 0, area = TEST_AREA_3)
            zone(zoneX = 6, zoneZ = 3, level = 0, area = TEST_AREA_4)
            zone(zoneX = 6, zoneZ = 3, level = 0, area = TEST_AREA_5)
            mapSquare(area = TEST_AREA_6)
        }
        assertThrows<IllegalStateException> {
            MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas)
        }
    }

    // Mapsquare + CoordGrid tests.

    @Test
    fun `validate mapsquare plus coord area limit`() {
        val areas = createMapAreas {
            mapSquare(area = TEST_AREA_1)
            mapSquare(area = TEST_AREA_2)
            mapSquare(area = TEST_AREA_3)
            mapSquare(area = TEST_AREA_4)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 5, level = 1, area = TEST_AREA_5)
        }
        assertDoesNotThrow { MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas) }
    }

    @Test
    fun `throw on mapsquare plus coord area limit exceeded`() {
        val areas = createMapAreas {
            mapSquare(area = TEST_AREA_1)
            mapSquare(area = TEST_AREA_2)
            mapSquare(area = TEST_AREA_3)
            mapSquare(area = TEST_AREA_4)
            mapSquare(area = TEST_AREA_5)
            coord(mapSquareLocalX = 5, mapSquareLocalZ = 5, level = 1, area = TEST_AREA_6)
        }
        assertThrows<IllegalStateException> {
            MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas)
        }
    }

    @Test
    fun `throw on coord plus mapsquare area limit exceeded`() {
        val areas = createMapAreas {
            coord(mapSquareLocalX = 1, mapSquareLocalZ = 1, level = 1, area = TEST_AREA_1)
            coord(mapSquareLocalX = 1, mapSquareLocalZ = 1, level = 1, area = TEST_AREA_2)
            coord(mapSquareLocalX = 1, mapSquareLocalZ = 1, level = 1, area = TEST_AREA_3)
            coord(mapSquareLocalX = 1, mapSquareLocalZ = 1, level = 1, area = TEST_AREA_4)
            coord(mapSquareLocalX = 1, mapSquareLocalZ = 1, level = 1, area = TEST_AREA_5)
            mapSquare(area = TEST_AREA_6)
        }
        assertThrows<IllegalStateException> {
            MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas)
        }
    }

    // Zone + CoordGrid tests.

    @Test
    fun `validate zone plus coord area limit`() {
        val areas = createMapAreas {
            zone(zoneX = 1, zoneZ = 1, level = 0, area = TEST_AREA_1)
            zone(zoneX = 1, zoneZ = 1, level = 0, area = TEST_AREA_2)
            zone(zoneX = 1, zoneZ = 1, level = 0, area = TEST_AREA_3)
            zone(zoneX = 1, zoneZ = 1, level = 0, area = TEST_AREA_4)
            coord(mapSquareLocalX = 8, mapSquareLocalZ = 8, level = 0, area = TEST_AREA_5)
        }
        assertDoesNotThrow { MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas) }
    }

    @Test
    fun `throw on zone plus coord area limit exceeded`() {
        val areas = createMapAreas {
            zone(zoneX = 1, zoneZ = 1, level = 0, area = TEST_AREA_1)
            zone(zoneX = 1, zoneZ = 1, level = 0, area = TEST_AREA_2)
            zone(zoneX = 1, zoneZ = 1, level = 0, area = TEST_AREA_3)
            zone(zoneX = 1, zoneZ = 1, level = 0, area = TEST_AREA_4)
            zone(zoneX = 1, zoneZ = 1, level = 0, area = TEST_AREA_5)
            coord(mapSquareLocalX = 8, mapSquareLocalZ = 8, level = 0, area = TEST_AREA_6)
        }
        assertThrows<IllegalStateException> {
            MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas)
        }
    }

    // Mapsquare + Zone + CoordGrid tests.

    @Test
    fun `validate mapsquare plus zone and coord area limit`() {
        val areas = createMapAreas {
            mapSquare(area = TEST_AREA_1)
            mapSquare(area = TEST_AREA_2)
            mapSquare(area = TEST_AREA_3)
            zone(zoneX = 0, zoneZ = 0, level = 1, area = TEST_AREA_4)
            coord(mapSquareLocalX = 0, mapSquareLocalZ = 0, level = 0, area = TEST_AREA_5)
        }
        assertDoesNotThrow { MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas) }
    }

    @Test
    fun `throw on mapsquare plus zone and coord area limit exceeded`() {
        val areas = createMapAreas {
            mapSquare(area = TEST_AREA_1)
            mapSquare(area = TEST_AREA_2)
            mapSquare(area = TEST_AREA_3)
            mapSquare(area = TEST_AREA_4)
            zone(zoneX = 0, zoneZ = 0, level = 1, area = TEST_AREA_5)
            coord(mapSquareLocalX = 0, mapSquareLocalZ = 0, level = 1, area = TEST_AREA_6)
        }
        assertThrows<IllegalStateException> {
            MapAreaEncoder.validateAreaLimits(TEST_MAPSQUARE, areas)
        }
    }

    private fun createMapAreas(init: MapAreaBuilder.() -> Unit): MapAreaDefinition {
        return MapAreaBuilder().apply(init).build()
    }

    private class MapAreaBuilder {
        private val mapSquareAreas = ShortArraySet()
        private val zoneAreas = Byte2ObjectOpenHashMap<ShortSet>()
        private val coordAreas = Short2ObjectOpenHashMap<ShortSet>()

        fun mapSquare(area: Int) {
            mapSquareAreas.add(area.toShort())
        }

        fun zone(zoneX: Int, zoneZ: Int, level: Int, area: Int) {
            val zone = LocalMapSquareZone(zoneX, zoneZ, level)
            val areas = zoneAreas.computeIfAbsent(zone.packed.toByte(), ::ShortArraySet)
            areas.add(area.toShort())
        }

        fun coord(mapSquareLocalX: Int, mapSquareLocalZ: Int, level: Int, area: Int) {
            val grid = MapSquareGrid(mapSquareLocalX, mapSquareLocalZ, level)
            val areas = coordAreas.computeIfAbsent(grid.packed.toShort(), ::ShortArraySet)
            areas.add(area.toShort())
        }

        fun build(): MapAreaDefinition {
            return MapAreaDefinition(mapSquareAreas, zoneAreas, coordAreas)
        }
    }

    private companion object {
        const val TEST_AREA_1 = 1
        const val TEST_AREA_2 = 2
        const val TEST_AREA_3 = 3
        const val TEST_AREA_4 = 4
        const val TEST_AREA_5 = 5
        const val TEST_AREA_6 = 6

        val TEST_MAPSQUARE = MapSquareKey(10550)
    }
}
