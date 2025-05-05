package org.rsmod.game.area.polygon

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.util.LocalMapSquareZone
import org.rsmod.map.zone.ZoneGrid

class PolygonMapSquareBuilderTest {
    @Test
    fun `single tile polygon is registered as coord area`() {
        val area: Short = 2
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(1)) {
            vertex(10, 10)
            vertex(11, 10)
            vertex(11, 11)
            vertex(10, 11)
            vertex(10, 10)
        }

        val result = builder.build()

        assertTrue(result.containsArea(area, MapSquareGrid(10, 10, 1)))
        assertTrue(result.containsArea(area, MapSquareGrid(10, 11, 1)))
        assertTrue(result.containsArea(area, MapSquareGrid(11, 10, 1)))
        assertTrue(result.containsArea(area, MapSquareGrid(11, 11, 1)))

        assertFalse(result.hasMapSquareArea(area))
        assertFalse(result.hasZoneArea(area))
    }

    @Test
    fun `full zone polygon is registered as zone area`() {
        val area: Short = 1
        val zoneX = 1
        val zoneZ = 2
        val level = 1

        val zoneLength = ZoneGrid.LENGTH
        val startX = zoneX * zoneLength
        val startZ = zoneZ * zoneLength

        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(level)) {
            vertex(startX, startZ)
            vertex(startX + zoneLength - 1, startZ)
            vertex(startX + zoneLength - 1, startZ + zoneLength - 1)
            vertex(startX, startZ + zoneLength - 1)
            vertex(startX, startZ)
        }

        val result = builder.build()
        val localZone = LocalMapSquareZone(zoneX, zoneZ, level)

        assertFalse(result.hasMapSquareArea(area))
        assertTrue(result.containsArea(area, localZone))
        assertFalse(result.hasCoordArea(area))
    }

    @Test
    fun `full map square polygon is registered as map square area`() {
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area = 1, levels = setOf(0, 1, 2, 3)) {
            vertex(0, 0)
            vertex(63, 0)
            vertex(63, 63)
            vertex(0, 63)
            vertex(0, 0)
        }
        val result = builder.build()
        assertTrue(result.hasMapSquareArea(1))
        assertTrue(result.coordAreas.isEmpty())
        assertTrue(result.zoneAreas.isEmpty())
    }

    @Test
    fun `partial zone polygon is not promoted to zone area`() {
        val area: Short = 2
        val level = 3
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(level)) {
            vertex(1, 1)
            vertex(5, 1)
            vertex(5, 5)
            vertex(1, 5)
            vertex(1, 1)
        }

        val result = builder.build()
        val coord = MapSquareGrid(3, 3, level)

        assertFalse(result.hasMapSquareArea(area))
        assertFalse(result.hasZoneArea(area))
        assertTrue(result.containsArea(area, coord))
    }

    @Test
    fun `partial map square polygon is not promoted to map square area`() {
        val area: Short = 15
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0, 1, 2, 3)) {
            vertex(0, 0)
            vertex(63, 0)
            vertex(63, 62)
            vertex(0, 62)
            vertex(0, 0)
        }
        val result = builder.build()
        assertFalse(result.hasMapSquareArea(area))
        assertTrue(result.hasCoordArea(area))
        assertTrue(result.hasZoneArea(area))
    }

    @Test
    fun `polygon spanning multiple levels is registered on each level`() {
        val area: Short = 4
        val levels = setOf(0, 1, 2)
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels) {
            vertex(10, 10)
            vertex(12, 10)
            vertex(12, 12)
            vertex(10, 12)
            vertex(10, 10)
        }

        val result = builder.build()

        for (level in levels) {
            val coord = MapSquareGrid(11, 11, level)
            assertTrue(result.containsArea(area, coord)) {
                "Expected tile at ($coord) to be part of area: $area"
            }
        }

        assertFalse(result.hasMapSquareArea(area))
        assertFalse(result.hasZoneArea(area))
    }

    @Test
    fun `edge-only polygon fills internal tiles`() {
        val area: Short = 5
        val level = 0
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(level)) {
            vertex(1, 1)
            vertex(6, 1)
            vertex(6, 6)
            vertex(1, 6)
        }

        val result = builder.build()

        for (x in 1..6) {
            for (z in 1..6) {
                val coord = MapSquareGrid(x, z, level)
                assertTrue(result.containsArea(area, coord)) {
                    "Expected tile at ($coord) to be part of filled polygon for area: $area"
                }
            }
        }

        assertFalse(result.hasZoneArea(area))
        assertFalse(result.hasMapSquareArea(area))
    }

    @Test
    fun `triangle polygon auto-closes when not explicitly closed`() {
        val area: Short = 77
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(0, 0)
            vertex(4, 0)
            vertex(0, 4)
        }

        val result = builder.build()

        assertTrue(result.containsArea(area, MapSquareGrid(0, 0)))
        assertTrue(result.containsArea(area, MapSquareGrid(4, 0)))
        assertTrue(result.containsArea(area, MapSquareGrid(0, 4)))

        assertTrue(result.containsArea(area, MapSquareGrid(1, 1))) {
            "Expected tile at MapSquareGrid(1, 1) to be filled"
        }

        assertFalse(result.containsArea(area, MapSquareGrid(3, 3))) {
            "Expected tile at MapSquareGrid(3, 3) to not be filled"
        }
    }

    @Test
    fun `polygon is implicitly closed on build`() {
        val area: Short = 3
        val level = 3
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(level)) {
            vertex(1, 1)
            vertex(5, 1)
            vertex(5, 5)
            vertex(1, 5)
        }

        val result = builder.build()
        val coord = MapSquareGrid(3, 3, level)

        assertTrue(result.containsArea(area, coord))
    }

    @Test
    fun `polygon is implicitly closed on build with multiple levels`() {
        val area: Short = 3
        val levels = setOf(1, 2)
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels) {
            vertex(1, 1)
            vertex(5, 1)
            vertex(5, 5)
            vertex(1, 5)
        }

        val result = builder.build()

        for (level in levels) {
            val coord = MapSquareGrid(3, 3, level)
            assertTrue(result.containsArea(area, coord)) {
                "Expected tile at ($coord) to be filled for level: $level"
            }
        }
    }

    @Test
    fun `multiple polygons are aggregated independently`() {
        val builder = PolygonMapSquareBuilder()

        builder.polygon(1, levels = setOf(0)) {
            vertex(1, 1)
            vertex(3, 1)
            vertex(3, 3)
            vertex(1, 3)
        }

        builder.polygon(2, levels = setOf(1)) {
            vertex(10, 10)
            vertex(12, 10)
            vertex(12, 12)
            vertex(10, 12)
        }

        val result = builder.build()

        assertTrue(result.containsArea(1, MapSquareGrid(2, 2, 0)))
        assertFalse(result.containsArea(2, MapSquareGrid(2, 2, 0)))

        assertTrue(result.containsArea(2, MapSquareGrid(11, 11, 1)))
        assertFalse(result.containsArea(1, MapSquareGrid(11, 11, 1)))
    }

    @Test
    fun `overlapping polygons with shared tiles are tracked per area`() {
        val sharedX = 10
        val sharedZ = 10
        val sharedLevel = 0

        val builder = PolygonMapSquareBuilder()

        builder.polygon(1, levels = setOf(sharedLevel)) {
            vertex(sharedX, sharedZ)
            vertex(sharedX + 1, sharedZ)
            vertex(sharedX + 1, sharedZ + 1)
            vertex(sharedX, sharedZ + 1)
        }

        builder.polygon(2, levels = setOf(sharedLevel)) {
            vertex(sharedX, sharedZ)
            vertex(sharedX + 2, sharedZ)
            vertex(sharedX + 2, sharedZ + 2)
            vertex(sharedX, sharedZ + 2)
        }

        val result = builder.build()
        val sharedCoord = MapSquareGrid(sharedX, sharedZ, sharedLevel)

        assertTrue(result.containsArea(1, sharedCoord))
        assertTrue(result.containsArea(2, sharedCoord))
    }

    @Test
    fun `partially overlapping polygons track intersecting and distinct tiles per area`() {
        val x1 = 10
        val z1 = 10
        val x2 = 11
        val z2 = 11
        val level = 2

        val builder = PolygonMapSquareBuilder()

        builder.polygon(1, levels = setOf(level)) {
            vertex(x1, z1)
            vertex(x1 + 2, z1)
            vertex(x1 + 2, z1 + 2)
            vertex(x1, z1 + 2)
        }

        builder.polygon(2, levels = setOf(level)) {
            vertex(x2, z2)
            vertex(x2 + 2, z2)
            vertex(x2 + 2, z2 + 2)
            vertex(x2, z2 + 2)
        }

        val result = builder.build()
        val sharedTile = MapSquareGrid(x2, z2, level)
        val uniqueTo1 = MapSquareGrid(x1, z1, level)
        val uniqueTo2 = MapSquareGrid(x2 + 2, z2 + 2, level)

        assertTrue(result.containsArea(1, sharedTile))
        assertTrue(result.containsArea(2, sharedTile))

        assertTrue(result.containsArea(1, uniqueTo1))
        assertTrue(result.containsArea(2, uniqueTo2))

        assertFalse(result.containsArea(1, uniqueTo2))
        assertFalse(result.containsArea(2, uniqueTo1))
    }

    @Test
    fun `polygons on different levels with same tile coordinates are tracked independently`() {
        val x = 10
        val z = 10
        val level1 = 1
        val level2 = 2

        val builder = PolygonMapSquareBuilder()

        builder.polygon(1, levels = setOf(level1)) {
            vertex(x, z)
            vertex(x + 1, z)
            vertex(x + 1, z + 1)
            vertex(x, z + 1)
        }

        builder.polygon(2, levels = setOf(level2)) {
            vertex(x, z)
            vertex(x + 1, z)
            vertex(x + 1, z + 1)
            vertex(x, z + 1)
        }

        val result = builder.build()
        val level1Coord = MapSquareGrid(x, z, level1)
        val level2Coord = MapSquareGrid(x, z, level2)

        assertTrue(result.containsArea(1, level1Coord))
        assertFalse(result.containsArea(1, level2Coord))
        assertTrue(result.containsArea(2, level2Coord))
        assertFalse(result.containsArea(2, level1Coord))
    }

    @Test
    fun `horizontal line polygon fills expected tiles`() {
        val area: Short = 12
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(10, 10)
            vertex(15, 10)
        }
        val result = builder.build()
        for (x in 10..15) {
            val tile = MapSquareGrid(x, 10)
            assertTrue(result.containsArea(area, tile)) {
                "Expected tile at ($tile) to be part of filled area"
            }
        }
    }

    @Test
    fun `vertical line polygon fills expected tiles`() {
        val area: Short = 12
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(10, 10)
            vertex(10, 16)
        }
        val result = builder.build()
        for (z in 10..16) {
            val tile = MapSquareGrid(10, z)
            assertTrue(result.containsArea(area, tile)) {
                "Expected tile at ($tile) to be part of filled area"
            }
        }
    }

    @Test
    fun `diagonal line polygon fills expected tiles`() {
        val area: Short = 12
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(10, 10)
            vertex(15, 15)
        }
        val result = builder.build()
        for (off in 10..15) {
            val tile = MapSquareGrid(off, off)
            assertTrue(result.containsArea(area, tile)) {
                "Expected tile at ($tile) to be part of filled area"
            }
        }
    }

    @Test
    fun `negative slope diagonal fills expected tiles`() {
        val area: Short = 15
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(10, 20)
            vertex(15, 15)
        }
        val result = builder.build()
        for (off in 0..5) {
            val tile = MapSquareGrid(10 + off, 20 - off)
            assertTrue(result.containsArea(area, tile)) {
                "Expected tile at ($tile) to be part of filled area"
            }
        }
    }

    @Test
    fun `horizontal line with multiple vertices fills expected tiles`() {
        val area: Short = 12
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(10, 10)
            vertex(12, 10)
            vertex(14, 10)
            vertex(15, 10)
        }
        val result = builder.build()
        for (x in 10..15) {
            val tile = MapSquareGrid(x, 10)
            assertTrue(result.containsArea(area, tile)) {
                "Expected tile at ($tile) to be part of filled area"
            }
        }
    }

    @Test
    fun `vertical line with multiple vertices fills expected tiles`() {
        val area: Short = 13
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(20, 10)
            vertex(20, 12)
            vertex(20, 14)
            vertex(20, 16)
        }
        val result = builder.build()
        for (z in 10..16) {
            val tile = MapSquareGrid(20, z)
            assertTrue(result.containsArea(area, tile)) {
                "Expected tile at ($tile) to be part of filled area"
            }
        }
    }

    @Test
    fun `diagonal line with multiple vertices fills expected tiles`() {
        val area: Short = 14
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(30, 30)
            vertex(32, 32)
            vertex(34, 34)
            vertex(36, 36)
        }
        val result = builder.build()
        for (off in 30..36) {
            val tile = MapSquareGrid(off, off)
            assertTrue(result.containsArea(area, tile)) {
                "Expected tile at ($tile) to be part of filled area"
            }
        }
    }

    @Test
    fun `small polygon overlapping multiple zones is not promoted to zone areas`() {
        val area: Short = 14
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(7, 7)
            vertex(10, 7)
            vertex(10, 10)
            vertex(7, 10)
            vertex(7, 7)
        }

        val result = builder.build()

        assertFalse(result.hasZoneArea(area))
        assertFalse(result.hasMapSquareArea(area))

        // Area should have still been set for every tile.
        for (x in 7..10) {
            for (z in 7..10) {
                assertTrue(result.containsArea(area, MapSquareGrid(x, z, 0))) {
                    "Expected tile at ($x, $z) to be set for area: $area"
                }
            }
        }
    }

    @Test
    fun `fill single-tile scanline correctly without spilling`() {
        val area: Short = 14
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(56, 56)
            vertex(48, 50)
            vertex(55, 43)
            vertex(62, 49)
            vertex(56, 56)
        }

        val result = builder.build()

        val south = MapSquareGrid(55, 43)
        assertTrue(result.containsArea(area, south))
        for (x in 0 until MapSquareGrid.LENGTH) {
            if (x == south.x) {
                continue
            }
            val tile = MapSquareGrid(x, south.z)
            assertFalse(result.containsArea(area, tile)) { "Unexpected filled tile: $tile" }
        }

        val north = MapSquareGrid(56, 56)
        assertTrue(result.containsArea(area, north))
        for (x in 0 until MapSquareGrid.LENGTH) {
            if (x == north.x) {
                continue
            }
            val tile = MapSquareGrid(x, north.z)
            assertFalse(result.containsArea(area, tile)) { "Unexpected filled tile: $tile" }
        }
    }

    @Test
    fun `multiple horizontal edges on same z-axis replaces previous and fills correctly`() {
        val area: Short = 1
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area = area, levels = setOf(0)) {
            vertex(5, 5)
            vertex(10, 5)
            vertex(15, 5)
            vertex(15, 10)
            vertex(5, 10)
            vertex(5, 5)
        }
        val result = builder.build()
        for (x in 5..15) {
            for (z in 5..10) {
                assertTrue(result.containsArea(area, MapSquareGrid(x, z))) {
                    "Expected tile at ($x, $z) to be part of filled area"
                }
            }
        }
    }

    @Test
    fun `northward edge does not include extra top row tile`() {
        val area: Short = 99
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(0, 63)
            vertex(0, 0)
            vertex(15, 0)
            vertex(15, 2)
            vertex(28, 2)
            vertex(28, 0)
            vertex(43, 0)
            vertex(43, 63)
            vertex(43, 0)
        }

        val result = builder.build()

        // Check row "2" (third row from bottom) as it should be fully filled in.
        for (x in 0 until 43) {
            val tile = MapSquareGrid(x, z = 2)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        // Check row "0" (first row from bottom) as it should be partially filled in.
        for (x in 0..15) {
            val tile = MapSquareGrid(x, z = 0)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        // Check row "0" is not filled in the partial area.
        for (x in 16..27) {
            val tile = MapSquareGrid(x, z = 0)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertFalse(contains) { "Expected tile at ($tile) to not be part of filled area" }
        }

        // Check row "0" has the rest of the row filled in.
        for (x in 28..43) {
            val tile = MapSquareGrid(x, z = 0)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }
    }

    @Test
    fun `north-east diagonal edge does not include extra top row tile`() {
        val area: Short = 99
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(0, 63)
            vertex(0, 0)
            vertex(14, 0)
            vertex(16, 2)
            vertex(26, 2)
            vertex(28, 0)
            vertex(43, 0)
            vertex(43, 63)
            vertex(43, 0)
        }

        val result = builder.build()

        // Row 2 should be fully filled.
        for (x in 0 until 43) {
            val tile = MapSquareGrid(x, z = 2)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        // Row 1: filled from 0 to 15.
        for (x in 0..15) {
            val tile = MapSquareGrid(x, z = 1)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        // Row 1: empty from 16 to 26.
        for (x in 16..26) {
            val tile = MapSquareGrid(x, z = 1)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertFalse(contains) { "Expected tile at ($tile) to not be part of filled area" }
        }

        // Row 1: filled from 27 to 43.
        for (x in 27..43) {
            val tile = MapSquareGrid(x, z = 1)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }
    }

    @Test
    fun `north-west diagonal edge does not include extra top row tile`() {
        val area: Short = 98
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(43, 63)
            vertex(43, 0)
            vertex(29, 0)
            vertex(27, 2)
            vertex(17, 2)
            vertex(15, 0)
            vertex(0, 0)
            vertex(0, 63)
        }

        val result = builder.build()

        // Row 2 should be fully filled.
        for (x in 0..43) {
            val tile = MapSquareGrid(x, z = 2)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        // Row 1: filled from 0 to 14.
        for (x in 0..14) {
            val tile = MapSquareGrid(x, z = 1)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        // Row 1: empty from 17 to 27.
        for (x in 17..27) {
            val tile = MapSquareGrid(x, z = 1)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertFalse(contains) { "Expected tile at ($tile) to not be part of filled area" }
        }

        // Row 1: filled from 28 to 43.
        for (x in 28..43) {
            val tile = MapSquareGrid(x, z = 1)
            val zone = LocalMapSquareZone(x / LocalMapSquareZone.LENGTH, z = 0, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }
    }

    @Test
    fun `concave polygon with multiple shared vertices fills correctly`() {
        val area: Short = 123
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(5, 5)
            vertex(15, 5)
            vertex(15, 15)
            vertex(10, 15)
            vertex(10, 10)
            vertex(7, 10)
            vertex(7, 15)
            vertex(5, 15)
            vertex(5, 5)
        }
        val result = builder.build()

        val filledFirstRow = (5..15).map { MapSquareGrid(it, 5) }
        val filledSecondRow = (5..15).map { MapSquareGrid(it, 6) }
        val filledThirdRow = (5..15).map { MapSquareGrid(it, 7) }
        val filledSixthRow = (5..15).map { MapSquareGrid(it, 10) }
        val expectedFill = filledFirstRow + filledSecondRow + filledThirdRow + filledSixthRow

        // Test the first few rows are fully filled in, including the row with shared vertices.
        for (tile in expectedFill) {
            val zoneX = tile.x / LocalMapSquareZone.LENGTH
            val zoneZ = tile.z / LocalMapSquareZone.LENGTH
            val zone = LocalMapSquareZone(zoneX, zoneZ, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        // Test the first concave row being partially filled.
        for (x in 5..7) {
            val tile = MapSquareGrid(x, z = 11)
            val zoneX = tile.x / LocalMapSquareZone.LENGTH
            val zoneZ = tile.z / LocalMapSquareZone.LENGTH
            val zone = LocalMapSquareZone(zoneX, zoneZ, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        for (x in 8..9) {
            val tile = MapSquareGrid(x, z = 11)
            val zoneX = tile.x / LocalMapSquareZone.LENGTH
            val zoneZ = tile.z / LocalMapSquareZone.LENGTH
            val zone = LocalMapSquareZone(zoneX, zoneZ, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertFalse(contains) { "Expected tile at ($tile) to not be part of filled area" }
        }

        for (x in 10..15) {
            val tile = MapSquareGrid(x, z = 11)
            val zoneX = tile.x / LocalMapSquareZone.LENGTH
            val zoneZ = tile.z / LocalMapSquareZone.LENGTH
            val zone = LocalMapSquareZone(zoneX, zoneZ, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        // Test that the final row is also (partially) filled in.
        for (x in 5..7) {
            val tile = MapSquareGrid(x, z = 15)
            val zoneX = tile.x / LocalMapSquareZone.LENGTH
            val zoneZ = tile.z / LocalMapSquareZone.LENGTH
            val zone = LocalMapSquareZone(zoneX, zoneZ, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }

        for (x in 8..9) {
            val tile = MapSquareGrid(x, z = 15)
            val zoneX = tile.x / LocalMapSquareZone.LENGTH
            val zoneZ = tile.z / LocalMapSquareZone.LENGTH
            val zone = LocalMapSquareZone(zoneX, zoneZ, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertFalse(contains) { "Expected tile at ($tile) to not be part of filled area" }
        }

        for (x in 10..15) {
            val tile = MapSquareGrid(x, z = 15)
            val zoneX = tile.x / LocalMapSquareZone.LENGTH
            val zoneZ = tile.z / LocalMapSquareZone.LENGTH
            val zone = LocalMapSquareZone(zoneX, zoneZ, level = 0)
            val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
            assertTrue(contains) { "Expected tile at ($tile) to be part of filled area" }
        }
    }

    /*
     * Tests a "bowtie" polygon to ensure the center "meet-point" is filled in.
     *
     * ```
     *   (5,15)┌───────────────┐ (15,15)
     *          \             /
     *           \           /
     *            \         /
     *             \(10,10)/
     *            /         \
     *           /           \
     *          /             \
     *   (5,5) └───────────────┘ (15,5)
     * ```
     */
    @Test
    fun `self intersecting bowtie polygon uses non-zero fill`() {
        val area: Short = 44
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(5, 5)
            vertex(15, 15)
            vertex(5, 15)
            vertex(15, 5)
            vertex(5, 5)
        }
        val result = builder.build()
        val tile = MapSquareGrid(10, 10)
        assertTrue(result.containsArea(area, tile)) {
            "Expected tile at ($tile) to not be part of filled area"
        }
    }

    @Test
    fun `multiple sub-polygons union to full zone area`() {
        val area: Short = 99
        val level = 0
        val builder = PolygonMapSquareBuilder()

        builder.polygon(area, levels = setOf(level)) {
            vertex(0, 0)
            vertex(3, 0)
            vertex(3, ZoneGrid.LENGTH - 1)
            vertex(0, ZoneGrid.LENGTH - 1)
        }

        builder.polygon(area, levels = setOf(level)) {
            vertex(4, 0)
            vertex(ZoneGrid.LENGTH - 1, 0)
            vertex(ZoneGrid.LENGTH - 1, ZoneGrid.LENGTH - 1)
            vertex(4, ZoneGrid.LENGTH - 1)
        }

        val result = builder.build()
        assertFalse(result.hasCoordArea(area))
        assertFalse(result.hasMapSquareArea(area))

        val fullZone = LocalMapSquareZone(0, 0, level)
        assertTrue(result.containsArea(area, fullZone))
    }

    @Test
    fun `complex polygon respects exclusive north east boundary fill rules`() {
        val area: Short = 1234
        val builder = PolygonMapSquareBuilder()
        val boundary =
            listOf(
                MapSquareGrid(x = 55, z = 11),
                MapSquareGrid(x = 45, z = 1),
                MapSquareGrid(x = 39, z = 5),
                MapSquareGrid(x = 37, z = 3),
                MapSquareGrid(x = 28, z = 2),
                MapSquareGrid(x = 27, z = 3),
                MapSquareGrid(x = 27, z = 6),
                MapSquareGrid(x = 17, z = 6),
                MapSquareGrid(x = 17, z = 7),
                MapSquareGrid(x = 16, z = 7),
                MapSquareGrid(x = 5, z = 6),
                MapSquareGrid(x = 5, z = 7),
                MapSquareGrid(x = 0, z = 7),
                MapSquareGrid(x = 0, z = 0),
                MapSquareGrid(x = 63, z = 0),
                MapSquareGrid(x = 63, z = 9),
                MapSquareGrid(x = 59, z = 8),
            )

        builder.polygon(area, levels = setOf(0)) {
            for (tile in boundary) {
                vertex(tile.x, tile.z)
            }
        }

        val result = builder.build()

        val tile = MapSquareGrid(58, 6)
        val zoneX = tile.x / LocalMapSquareZone.LENGTH
        val zoneZ = tile.z / LocalMapSquareZone.LENGTH
        val zone = LocalMapSquareZone(zoneX, zoneZ, level = 0)
        val contains = result.containsArea(area, zone) || result.containsArea(area, tile)
        assertTrue(contains)
    }

    @Test
    fun `horizontal polygon with two adjacent vertices fills exactly the two tiles`() {
        val area: Short = 50
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels = setOf(0)) {
            vertex(10, 5)
            vertex(11, 5)
        }

        val result = builder.build()

        assertTrue(result.containsArea(area, MapSquareGrid(10, 5)))
        assertTrue(result.containsArea(area, MapSquareGrid(11, 5)))

        assertFalse(result.containsArea(area, MapSquareGrid(10, 4)))
        assertFalse(result.containsArea(area, MapSquareGrid(11, 4)))
        assertFalse(result.containsArea(area, MapSquareGrid(10, 6)))
        assertFalse(result.containsArea(area, MapSquareGrid(11, 6)))
    }
}
