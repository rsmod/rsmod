package org.rsmod.api.cache.map.area

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.area.polygon.PolygonMapSquareBuilder
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.util.LocalMapSquareZone

class MapAreaDefinitionPolygonTest {
    @Test
    fun `small polygon converts to coord area`() {
        val area: Short = 5
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(10, 10)
            vertex(11, 10)
            vertex(11, 11)
            vertex(10, 11)
            vertex(10, 10)
        }
        val polygon = builder.build()
        val areaDef = MapAreaDefinition.from(polygon)

        assertTrue(areaDef.zoneAreas.isEmpty())
        assertTrue(areaDef.mapSquareAreas.isEmpty())

        val expectedGrid = MapSquareGrid(10, 10, 0).packed.toShort()
        val areaSet = areaDef.coordAreas[expectedGrid]
        assertEquals(setOf(area), areaSet.toSet())
    }

    @Test
    fun `full zone polygon converts to zone area`() {
        val area: Short = 3
        val zoneX = 2
        val zoneZ = 3
        val baseX = zoneX * 8
        val baseZ = zoneZ * 8
        val level = 1

        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(level)) {
            vertex(baseX, baseZ)
            vertex(baseX + 7, baseZ)
            vertex(baseX + 7, baseZ + 7)
            vertex(baseX, baseZ + 7)
            vertex(baseX, baseZ)
        }
        val polygon = builder.build()
        val areaDef = MapAreaDefinition.from(polygon)

        assertTrue(areaDef.coordAreas.isEmpty())
        assertTrue(areaDef.mapSquareAreas.isEmpty())

        val localZone = LocalMapSquareZone(zoneX, zoneZ, level).packed.toByte()
        val areaSet = areaDef.zoneAreas[localZone]
        assertEquals(setOf(area), areaSet.toSet())
    }

    @Test
    fun `full map square polygon converts to map square area`() {
        val area: Short = 4

        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0, 1, 2, 3)) {
            vertex(0, 0)
            vertex(63, 0)
            vertex(63, 63)
            vertex(0, 63)
            vertex(0, 0)
        }

        val polygon = builder.build()
        val areaDef = MapAreaDefinition.from(polygon)

        assertTrue(areaDef.coordAreas.isEmpty())
        assertTrue(areaDef.zoneAreas.isEmpty())
        assertEquals(setOf(area), areaDef.mapSquareAreas.toSet())
    }

    @Test
    fun `multiple overlapping areas at the same coord are grouped correctly`() {
        val area1: Short = 10
        val area2: Short = 11
        val tileX = 20
        val tileZ = 20
        val level = 2

        val builder = PolygonMapSquareBuilder()
        builder.polygon(area1, levels = setOf(level)) {
            vertex(tileX, tileZ)
            vertex(tileX + 1, tileZ)
            vertex(tileX + 1, tileZ + 1)
            vertex(tileX, tileZ + 1)
            vertex(tileX, tileZ)
        }
        builder.polygon(area2, levels = setOf(level)) {
            vertex(tileX, tileZ)
            vertex(tileX + 2, tileZ)
            vertex(tileX + 2, tileZ + 2)
            vertex(tileX, tileZ + 2)
            vertex(tileX, tileZ)
        }

        val polygon = builder.build()
        val areaDef = MapAreaDefinition.from(polygon)

        val packed = MapSquareGrid(tileX, tileZ, level).packed.toShort()
        val areaSet = areaDef.coordAreas[packed]
        assertEquals(setOf(area1, area2), areaSet.toSet())
    }

    @Test
    fun `multiple polygons across all scopes convert correctly`() {
        val coordArea: Short = 4
        val zoneArea: Short = 8
        val mapSquareArea: Short = 10

        val builder = PolygonMapSquareBuilder()

        builder.polygon(coordArea, levels = setOf(0)) {
            vertex(10, 10)
            vertex(11, 10)
            vertex(11, 11)
            vertex(10, 11)
            vertex(10, 10)
        }

        val zoneX = 2
        val zoneZ = 2
        val baseX = zoneX * 8
        val baseZ = zoneZ * 8
        builder.polygon(zoneArea, levels = setOf(0)) {
            vertex(baseX, baseZ)
            vertex(baseX + 7, baseZ)
            vertex(baseX + 7, baseZ + 7)
            vertex(baseX, baseZ + 7)
            vertex(baseX, baseZ)
        }

        builder.polygon(mapSquareArea, levels = setOf(0, 1, 2, 3)) {
            vertex(0, 0)
            vertex(63, 0)
            vertex(63, 63)
            vertex(0, 63)
            vertex(0, 0)
        }

        val polygon = builder.build()
        val areaDef = MapAreaDefinition.from(polygon)

        assertTrue(areaDef.mapSquareAreas.contains(mapSquareArea))
        assertFalse(areaDef.mapSquareAreas.contains(coordArea))
        assertFalse(areaDef.mapSquareAreas.contains(zoneArea))

        val localZone = LocalMapSquareZone(zoneX, zoneZ, 0).packed.toByte()
        assertEquals(setOf(zoneArea), areaDef.zoneAreas[localZone].toSet())

        val packedCoord = MapSquareGrid(10, 10, 0).packed.toShort()
        assertEquals(setOf(coordArea), areaDef.coordAreas[packedCoord].toSet())
    }

    @Test
    fun `polygon with no areas converts to empty definition`() {
        val builder = PolygonMapSquareBuilder()
        val polygon = builder.build()
        val areaDef = MapAreaDefinition.from(polygon)
        assertTrue(areaDef.coordAreas.isEmpty())
        assertTrue(areaDef.zoneAreas.isEmpty())
        assertTrue(areaDef.mapSquareAreas.isEmpty())
    }
}
