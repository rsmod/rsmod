package org.rsmod.game.area.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey

class PolygonMapSquareClipperTest {
    @Test
    fun `clip returns 4 points when horizontal line crosses map square boundary`() {
        val input = listOf(CoordGrid(60, 10), CoordGrid(68, 10))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 = setOf(CoordGrid(60, 10), CoordGrid(63, 10))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 = setOf(CoordGrid(64, 10), CoordGrid(68, 10))
        assertEquals(expected2, result[MapSquareKey(1, 0)]?.toSet())
    }

    @Test
    fun `clip returns 4 points when vertical line crosses map square boundary`() {
        val input = listOf(CoordGrid(10, 60), CoordGrid(10, 68))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 = setOf(CoordGrid(10, 60), CoordGrid(10, 63))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 = setOf(CoordGrid(10, 64), CoordGrid(10, 68))
        assertEquals(expected2, result[MapSquareKey(0, 1)]?.toSet())
    }

    @Test
    fun `clip returns 4 points when diagonal line crosses into corner map square`() {
        val input = listOf(CoordGrid(60, 60), CoordGrid(68, 68))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 = setOf(CoordGrid(60, 60), CoordGrid(63, 63))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 = setOf(CoordGrid(64, 64), CoordGrid(68, 68))
        assertEquals(expected2, result[MapSquareKey(1, 1)]?.toSet())
    }

    @Test
    fun `clip closes 3-point polygon correctly`() {
        val input = listOf(CoordGrid(60, 60), CoordGrid(68, 60), CoordGrid(68, 68))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 = setOf(CoordGrid(60, 60), CoordGrid(63, 60), CoordGrid(63, 63))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 =
            setOf(CoordGrid(64, 60), CoordGrid(68, 60), CoordGrid(68, 63), CoordGrid(64, 63))
        assertEquals(expected2, result[MapSquareKey(1, 0)]?.toSet())

        val expected3 = setOf(CoordGrid(68, 64), CoordGrid(68, 68), CoordGrid(64, 64))
        assertEquals(expected3, result[MapSquareKey(1, 1)]?.toSet())
    }

    @Test
    fun `clip handles polygon with implicit closing corner correctly`() {
        val input =
            listOf(CoordGrid(60, 60), CoordGrid(68, 60), CoordGrid(68, 68), CoordGrid(60, 60))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 = setOf(CoordGrid(60, 60), CoordGrid(63, 60), CoordGrid(63, 63))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 =
            setOf(CoordGrid(64, 60), CoordGrid(68, 60), CoordGrid(68, 63), CoordGrid(64, 63))
        assertEquals(expected2, result[MapSquareKey(1, 0)]?.toSet())

        val expected3 = setOf(CoordGrid(68, 64), CoordGrid(68, 68), CoordGrid(64, 64))
        assertEquals(expected3, result[MapSquareKey(1, 1)]?.toSet())
    }

    @Test
    fun `clip returns 9 points for polygon spanning 4 map squares`() {
        val input =
            listOf(CoordGrid(60, 60), CoordGrid(68, 60), CoordGrid(68, 68), CoordGrid(60, 68))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 =
            setOf(CoordGrid(60, 60), CoordGrid(63, 60), CoordGrid(60, 63), CoordGrid(63, 63))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 =
            setOf(CoordGrid(64, 60), CoordGrid(68, 60), CoordGrid(68, 63), CoordGrid(64, 63))
        assertEquals(expected2, result[MapSquareKey(1, 0)]?.toSet())

        val expected3 =
            setOf(CoordGrid(68, 64), CoordGrid(64, 68), CoordGrid(68, 68), CoordGrid(64, 64))
        assertEquals(expected3, result[MapSquareKey(1, 1)]?.toSet())

        val expected4 =
            setOf(CoordGrid(60, 64), CoordGrid(60, 68), CoordGrid(63, 68), CoordGrid(63, 64))
        assertEquals(expected4, result[MapSquareKey(0, 1)]?.toSet())
    }

    @Test
    fun `clip handles polygon traversed in reverse order`() {
        val input =
            listOf(CoordGrid(60, 60), CoordGrid(60, 68), CoordGrid(68, 68), CoordGrid(68, 60))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 =
            setOf(CoordGrid(60, 60), CoordGrid(60, 63), CoordGrid(63, 60), CoordGrid(63, 63))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 =
            setOf(CoordGrid(60, 64), CoordGrid(60, 68), CoordGrid(63, 68), CoordGrid(63, 64))
        assertEquals(expected2, result[MapSquareKey(0, 1)]?.toSet())

        val expected3 =
            setOf(CoordGrid(68, 64), CoordGrid(64, 68), CoordGrid(68, 68), CoordGrid(64, 64))
        assertEquals(expected3, result[MapSquareKey(1, 1)]?.toSet())

        val expected4 =
            setOf(CoordGrid(64, 60), CoordGrid(68, 60), CoordGrid(68, 63), CoordGrid(64, 63))
        assertEquals(expected4, result[MapSquareKey(1, 0)]?.toSet())
    }

    @Test
    fun `clip handles polygon that re-crosses same boundary multiple times`() {
        val input =
            listOf(
                CoordGrid(60, 60),
                CoordGrid(68, 60),
                CoordGrid(60, 61),
                CoordGrid(68, 61),
                CoordGrid(60, 62),
            )
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 =
            setOf(
                CoordGrid(60, 60),
                CoordGrid(63, 60),
                CoordGrid(63, 61),
                CoordGrid(60, 61),
                CoordGrid(63, 62),
                CoordGrid(60, 62),
            )
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 =
            setOf(
                CoordGrid(64, 60),
                CoordGrid(68, 60),
                CoordGrid(64, 61),
                CoordGrid(68, 61),
                CoordGrid(64, 62),
            )
        assertEquals(expected2, result[MapSquareKey(1, 0)]?.toSet())
    }

    @Test
    fun `clip returns original points when all in same map square`() {
        val input =
            listOf(CoordGrid(10, 10), CoordGrid(20, 10), CoordGrid(20, 20), CoordGrid(10, 20))
        val result = PolygonMapSquareClipper.clip(input)
        assertEquals(input.toSet(), result[MapSquareKey(0, 0)]?.toSet())
    }

    @Test
    fun `clip correctly includes corner point when it lies on map square boundary`() {
        val input = listOf(CoordGrid(63, 50), CoordGrid(64, 50), CoordGrid(64, 56))
        val result = PolygonMapSquareClipper.closeAndClip(input)

        val expected1 = setOf(CoordGrid(63, 50))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 = setOf(CoordGrid(64, 50), CoordGrid(64, 56))
        assertEquals(expected2, result[MapSquareKey(1, 0)]?.toSet())
    }

    @Test
    fun `clip handles long diagonal across multiple map squares`() {
        val input = listOf(CoordGrid(60, 60), CoordGrid(75, 75))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 = setOf(CoordGrid(60, 60), CoordGrid(63, 63))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 = setOf(CoordGrid(64, 64), CoordGrid(75, 75))
        assertEquals(expected2, result[MapSquareKey(1, 1)]?.toSet())
    }

    @Test
    fun `clip handles zig-zag crossing same boundary diagonally`() {
        val input =
            listOf(
                CoordGrid(63, 60),
                CoordGrid(64, 61),
                CoordGrid(63, 62),
                CoordGrid(64, 63),
                CoordGrid(63, 64),
            )
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 = setOf(CoordGrid(63, 60), CoordGrid(63, 62), CoordGrid(63, 63))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 = setOf(CoordGrid(64, 61), CoordGrid(64, 63))
        assertEquals(expected2, result[MapSquareKey(1, 0)]?.toSet())

        val expected3 = setOf(CoordGrid(63, 64))
        assertEquals(expected3, result[MapSquareKey(0, 1)]?.toSet())
    }

    @Test
    fun `clip handles diagonal corner square transition`() {
        val input = listOf(CoordGrid(63, 63), CoordGrid(65, 65))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 = setOf(CoordGrid(63, 63))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 = setOf(CoordGrid(64, 64), CoordGrid(65, 65))
        assertEquals(expected2, result[MapSquareKey(1, 1)]?.toSet())
    }

    @Test
    fun `clip handles re-entering same map square multiple times`() {
        val input =
            listOf(CoordGrid(60, 60), CoordGrid(68, 60), CoordGrid(60, 60), CoordGrid(68, 60))
        val result = PolygonMapSquareClipper.clip(input)

        val expected1 = setOf(CoordGrid(60, 60), CoordGrid(63, 60))
        assertEquals(expected1, result[MapSquareKey(0, 0)]?.toSet())

        val expected2 = setOf(CoordGrid(64, 60), CoordGrid(68, 60))
        assertEquals(expected2, result[MapSquareKey(1, 0)]?.toSet())
    }

    @Test
    fun `clip spans 3x3 grid and clips each map square with 4 corners`() {
        val input =
            listOf(
                CoordGrid(0, 0, 0, 0, 0),
                CoordGrid(0, 3, 0, 0, 0),
                CoordGrid(0, 3, 3, 0, 0),
                CoordGrid(0, 0, 3, 0, 0),
            )
        val result = PolygonMapSquareClipper.clip(input)
        for (x in 0..2) {
            for (z in 0..2) {
                val key = MapSquareKey(x, z)
                val actual = result[key]?.toSet()
                assertEquals(4, actual?.size) { "Expected 4 corners in $key but was $actual" }
            }
        }
    }

    @Test
    fun `clip spans 3x3 grid with large concave polygon`() {
        val input =
            listOf(
                CoordGrid(0, 0, 0, 0, 0),
                CoordGrid(0, 3, 0, 0, 0),
                CoordGrid(0, 3, 1, 0, 0),
                CoordGrid(0, 2, 1, 0, 0),
                CoordGrid(0, 2, 2, 0, 0),
                CoordGrid(0, 3, 2, 0, 0),
                CoordGrid(0, 3, 3, 0, 0),
                CoordGrid(0, 0, 3, 0, 0),
            )
        val result = PolygonMapSquareClipper.clip(input)

        val expectedSquares =
            listOf(
                MapSquareKey(0, 0),
                MapSquareKey(1, 0),
                MapSquareKey(2, 0),
                MapSquareKey(0, 1),
                MapSquareKey(1, 1),
                MapSquareKey(0, 2),
                MapSquareKey(1, 2),
                MapSquareKey(2, 2),
            )

        for (key in expectedSquares) {
            val points = result[key]?.size
            assertNotNull(points) { "Expected polygon to intersect $key" }
            assertTrue(checkNotNull(points) >= 2) {
                "Expected clipped polygon in $key to have at least 3 points: $points"
            }
        }

        val expectedKey = MapSquareKey(2, 1)
        val expectedPoints =
            setOf(CoordGrid(0, 2, 1, 0, 0), CoordGrid(0, 2, 1, 63, 0), CoordGrid(0, 2, 1, 0, 63))
        assertEquals(expectedPoints, result[expectedKey]?.toSet())
    }
}
