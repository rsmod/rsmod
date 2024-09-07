package org.rsmod.map.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.map.CoordGrid

class BoundsIteratorTest {
    @Test
    fun `iterator covers single coordinate in single unit bounds`() {
        val bounds = Bounds(3200, 3200, 0, 1, 1)
        val expectedPoints = listOf(CoordGrid(3200, 3200))
        val actualPoints = bounds.iterator().asSequence().toList()
        assertEquals(expectedPoints, actualPoints)
    }

    @Test
    fun `iterator covers all points within double unit bounds`() {
        val bounds = Bounds(3200, 3200, 0, 2, 2)
        val expectedPoints =
            setOf(
                CoordGrid(3200, 3200),
                CoordGrid(3200, 3201),
                CoordGrid(3201, 3200),
                CoordGrid(3201, 3201),
            )
        val actualPoints = bounds.iterator().asSequence().toSet()
        assertEquals(expectedPoints, actualPoints)
    }

    @Test
    fun `iterator covers all points within multi unit bounds`() {
        val bounds = Bounds(3200, 3200, 0, 3, 3)
        val expectedPoints =
            setOf(
                CoordGrid(3200, 3200),
                CoordGrid(3200, 3201),
                CoordGrid(3200, 3202),
                CoordGrid(3201, 3200),
                CoordGrid(3201, 3201),
                CoordGrid(3201, 3202),
                CoordGrid(3202, 3200),
                CoordGrid(3202, 3201),
                CoordGrid(3202, 3202),
            )
        val actualPoints = bounds.iterator().asSequence().toSet()
        assertEquals(expectedPoints, actualPoints)
    }

    @Test
    fun `iterate over rectangular bounds`() {
        val bounds = Bounds(3200, 3200, 0, 2, 3)
        val expectedPoints =
            setOf(
                CoordGrid(3200, 3200),
                CoordGrid(3201, 3200),
                CoordGrid(3200, 3201),
                CoordGrid(3201, 3201),
                CoordGrid(3200, 3202),
                CoordGrid(3201, 3202),
            )
        val actualPoints = bounds.iterator().asSequence().toSet()
        assertEquals(expectedPoints, actualPoints)
    }

    @Test
    fun `iterate over bounds at edge of valid grid`() {
        val bounds = Bounds(0, 0, 0, 2, 2)
        val expectedPoints =
            setOf(CoordGrid(0, 0), CoordGrid(1, 0), CoordGrid(0, 1), CoordGrid(1, 1))
        val actualPoints = bounds.iterator().asSequence().toSet()
        assertEquals(expectedPoints, actualPoints)
    }

    @Test
    fun `iterator handles large bounds`() {
        val bounds = Bounds(3200, 3200, 0, 100, 100)
        val actualPoints = bounds.iterator().asSequence().toList().distinct()
        assertEquals(10000, actualPoints.size)
        assertEquals(CoordGrid(3200, 3200), actualPoints.first())
        assertEquals(CoordGrid(3299, 3299), actualPoints.last())
    }

    @Test
    fun `iterator does not iterate outside of bounds`() {
        val bounds = Bounds(3200, 3200, 0, 3, 3)
        val actualPoints = bounds.iterator().asSequence().toList()
        val pointsOutsideBounds =
            actualPoints.filter { it.x < 3200 || it.x > 3202 || it.z < 3200 || it.z > 3202 }
        assertTrue(pointsOutsideBounds.isEmpty())
    }

    @Test
    fun `iterator prioritizes x-coordinates first`() {
        val bounds = Bounds(3200, 3200, 0, 2, 2)
        val iterator = bounds.iterator()
        assertEquals(CoordGrid(3200, 3200), iterator.next())
        assertEquals(CoordGrid(3201, 3200), iterator.next())
        assertEquals(CoordGrid(3200, 3201), iterator.next())
        assertEquals(CoordGrid(3201, 3201), iterator.next())
    }
}
