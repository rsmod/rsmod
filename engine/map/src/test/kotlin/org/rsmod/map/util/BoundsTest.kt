package org.rsmod.map.util

import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource

class BoundsTest {
    @Test
    fun `get min bounds from x-coordinate`() {
        val boundsA = Bounds(3208, 3212, 0, 1, 1)
        val boundsB = Bounds(3210, 3210, 0, 1, 1)
        val minX = Bounds.minX(boundsA, boundsB)
        assertEquals(boundsA, minX)
    }

    @Test
    fun `get max bounds from x-coordinate`() {
        val boundsA = Bounds(3208, 3212, 0, 1, 1)
        val boundsB = Bounds(3210, 3210, 0, 1, 1)
        val maxX = Bounds.maxX(boundsA, boundsB)
        assertEquals(boundsB, maxX)
    }

    @Test
    fun `get min bounds from z-coordinate`() {
        val boundsA = Bounds(3208, 3212, 0, 1, 1)
        val boundsB = Bounds(3210, 3210, 0, 1, 1)
        val minZ = Bounds.minZ(boundsA, boundsB)
        assertEquals(boundsB, minZ)
    }

    @Test
    fun `get max bounds from z-coordinate`() {
        val boundsA = Bounds(3208, 3212, 0, 1, 1)
        val boundsB = Bounds(3210, 3210, 0, 1, 1)
        val maxZ = Bounds.maxZ(boundsA, boundsB)
        assertEquals(boundsA, maxZ)
    }

    @Test
    fun `can be on top of one another`() {
        val boundsA = Bounds(3208, 3212, 0, 1, 1)
        val boundsB = Bounds(3208, 3212, 0, 1, 1)
        assertTrue(boundsA.isWithinDistance(boundsB, distance = 0))
        assertTrue(boundsA.isWithinDistance(boundsB, distance = 1))
    }

    @Test
    fun `size 2 bounds is within a one tile distance`() {
        val boundsA = Bounds(3208, 3212, 0, 2, 2)
        val boundsB = Bounds(3210, 3210, 0, 2, 2)
        assertTrue(boundsA.isWithinDistance(boundsB, distance = 1))
        assertFalse(boundsA.isWithinDistance(boundsB, distance = 0))
        assertFalse(boundsA.isWithinDistance(boundsB, distance = -1))
    }

    @Test
    fun `should never be within negative distance`() {
        val boundsA = Bounds(3208, 3212, 0, 2, 2)
        val boundsB = Bounds(3208, 3212, 0, 2, 2)
        assertFalse(boundsA.isWithinDistance(boundsB, distance = -1))
    }

    @Test
    fun `x-distance should never be negative`() {
        val boundsA = Bounds(3210, 3210, 0, 1, 1)
        val below = Bounds(3200, 3210, 0, 10, 10)
        val exact = Bounds(3200, 3210, 0, 11, 11)
        val overstep = Bounds(3200, 3210, 0, 12, 12)
        assertEquals(1, Bounds.xDistanceBetween(boundsA, below))
        assertEquals(0, Bounds.xDistanceBetween(boundsA, exact))
        assertEquals(0, Bounds.xDistanceBetween(boundsA, overstep))
    }

    @Test
    fun `z-distance should never be negative`() {
        val boundsA = Bounds(3210, 3210, 0, 1, 1)
        val below = Bounds(3210, 3200, 0, 10, 10)
        val exact = Bounds(3210, 3200, 0, 11, 11)
        val overstep = Bounds(3210, 3200, 0, 12, 12)
        assertEquals(1, Bounds.zDistanceBetween(boundsA, below))
        assertEquals(0, Bounds.zDistanceBetween(boundsA, exact))
        assertEquals(0, Bounds.zDistanceBetween(boundsA, overstep))
    }

    @Test
    fun `distanceTo requires bounds to be on equal level`() {
        val boundsA = Bounds(3208, 3212, 0, 2, 2)
        val boundsB = Bounds(3208, 3212, 0, 2, 2)
        val boundsC = Bounds(3208, 3212, 1, 2, 2)
        val boundsD = Bounds(3208, 3212, 2, 2, 2)
        val boundsE = Bounds(3208, 3212, 3, 2, 2)
        assertDoesNotThrow { boundsA.distanceTo(boundsB) }
        assertThrows<IllegalArgumentException> { boundsA.distanceTo(boundsC) }
        assertThrows<IllegalArgumentException> { boundsA.distanceTo(boundsD) }
        assertThrows<IllegalArgumentException> { boundsA.distanceTo(boundsE) }
    }

    @Test
    fun `isWithinDistance requires bounds to be on equal level`() {
        val boundsA = Bounds(3208, 3212, 0, 2, 2)
        val boundsB = Bounds(3208, 3212, 0, 2, 2)
        val boundsC = Bounds(3208, 3212, 1, 2, 2)
        val boundsD = Bounds(3208, 3212, 2, 2, 2)
        val boundsE = Bounds(3208, 3212, 3, 2, 2)
        assertDoesNotThrow { boundsA.isWithinDistance(boundsB, distance = 0) }
        assertThrows<IllegalArgumentException> { boundsA.isWithinDistance(boundsC, distance = 0) }
        assertThrows<IllegalArgumentException> { boundsA.isWithinDistance(boundsD, distance = 0) }
        assertThrows<IllegalArgumentException> { boundsA.isWithinDistance(boundsE, distance = 0) }
    }

    @ParameterizedTest
    @ArgumentsSource(VaryingRangesProvider::class)
    fun `varying bound ranges`(
        expectedDistance: Int,
        boundsX1: Int,
        boundsZ1: Int,
        boundsWidth1: Int,
        boundsLength1: Int,
        boundsX2: Int,
        boundsZ2: Int,
        boundsWidth2: Int,
        boundsLength2: Int,
    ) {
        val bounds1 = Bounds(boundsX1, boundsZ1, 0, boundsWidth1, boundsLength1)
        val bounds2 = Bounds(boundsX2, boundsZ2, 0, boundsWidth2, boundsLength2)
        assertEquals(expectedDistance, bounds1.distanceTo(bounds2))
    }

    @ParameterizedTest
    @ArgumentsSource(VaryingBoundsProvider::class)
    fun `varying bounds within distance`(
        distance: Int,
        boundsX1: Int,
        boundsZ1: Int,
        boundsWidth1: Int,
        boundsLength1: Int,
        boundsX2: Int,
        boundsZ2: Int,
        boundsWidth2: Int,
        boundsLength2: Int,
        shouldBeWithinDistance: Boolean,
    ) {
        val bounds1 = Bounds(boundsX1, boundsZ1, 0, boundsWidth1, boundsLength1)
        val bounds2 = Bounds(boundsX2, boundsZ2, 0, boundsWidth2, boundsLength2)
        if (!shouldBeWithinDistance) {
            assertFalse(bounds1.isWithinDistance(bounds2, distance))
            assertFalse(bounds2.isWithinDistance(bounds1, distance))
        } else {
            assertTrue(bounds1.isWithinDistance(bounds2, distance))
            assertTrue(bounds2.isWithinDistance(bounds1, distance))
        }
    }

    private object VaryingRangesProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
            Stream.of(
                Arguments.of(0, 3210, 3210, 1, 1, 3210, 3210, 1, 1),
                Arguments.of(1, 3210, 3210, 1, 1, 3211, 3210, 1, 1),
                Arguments.of(1, 3210, 3210, 1, 1, 3209, 3210, 1, 1),
                Arguments.of(1, 3210, 3210, 1, 1, 3210, 3211, 1, 1),
                Arguments.of(1, 3210, 3210, 1, 1, 3210, 3209, 1, 1),
                Arguments.of(1, 3210, 3210, 1, 1, 3211, 3210, 2, 2),
                Arguments.of(0, 3210, 3210, 1, 1, 3209, 3210, 2, 2),
                Arguments.of(1, 3210, 3210, 1, 1, 3210, 3211, 2, 2),
                Arguments.of(0, 3210, 3210, 1, 1, 3210, 3209, 2, 2),
                Arguments.of(10, 3210, 3210, 1, 1, 3200, 3200, 5, 1),
                Arguments.of(10, 3210, 3210, 1, 1, 3200, 3200, 1, 5),
                Arguments.of(10, 3210, 3210, 1, 1, 3200, 3200, 1, 1),
                Arguments.of(9, 3210, 3210, 1, 1, 3200, 3200, 2, 2),
                Arguments.of(8, 3210, 3210, 1, 1, 3200, 3200, 3, 3),
                Arguments.of(7, 3210, 3210, 1, 1, 3200, 3200, 4, 4),
                Arguments.of(6, 3210, 3210, 1, 1, 3200, 3200, 5, 5),
                Arguments.of(5, 3210, 3210, 1, 1, 3200, 3200, 6, 6),
                Arguments.of(4, 3210, 3210, 1, 1, 3200, 3200, 7, 7),
                Arguments.of(3, 3210, 3210, 1, 1, 3200, 3200, 8, 8),
                Arguments.of(2, 3210, 3210, 1, 1, 3200, 3200, 9, 9),
                Arguments.of(1, 3210, 3210, 1, 1, 3200, 3200, 10, 10),
                Arguments.of(0, 3210, 3210, 1, 1, 3200, 3200, 11, 11),
                Arguments.of(9, 3220, 3220, 1, 1, 3210, 3210, 2, 2),
                Arguments.of(10, 3220, 3220, 1, 1, 3209, 3210, 2, 2),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3209, 2, 2),
                Arguments.of(10, 3220, 3220, 1, 1, 3209, 3209, 2, 2),
                Arguments.of(9, 3220, 3220, 2, 2, 3210, 3210, 2, 2),
                Arguments.of(10, 3220, 3220, 2, 2, 3209, 3210, 2, 2),
                Arguments.of(10, 3220, 3220, 2, 2, 3210, 3209, 2, 2),
                Arguments.of(10, 3220, 3220, 2, 2, 3209, 3209, 2, 2),
                Arguments.of(9, 3220, 3220, 3, 3, 3210, 3210, 2, 2),
                Arguments.of(10, 3220, 3220, 3, 3, 3209, 3210, 2, 2),
                Arguments.of(10, 3220, 3220, 3, 3, 3210, 3209, 2, 2),
                Arguments.of(10, 3220, 3220, 3, 3, 3209, 3209, 2, 2),
            )
    }

    private object VaryingBoundsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
            Stream.of(
                Arguments.of(0, 3208, 3212, 1, 1, 3208, 3212, 1, 1, true),
                Arguments.of(0, 3208, 3212, 1, 1, 3208, 3212, 2, 2, true),
                Arguments.of(0, 3208, 3212, 2, 2, 3208, 3212, 1, 1, true),
                Arguments.of(0, 3208, 3212, 2, 2, 3208, 3212, 2, 2, true),
                Arguments.of(0, 3208, 3212, 1, 1, 3209, 3212, 1, 1, false),
                Arguments.of(0, 3208, 3212, 1, 1, 3207, 3212, 1, 1, false),
                Arguments.of(0, 3208, 3212, 2, 2, 3208, 3214, 1, 1, false),
                Arguments.of(0, 3208, 3212, 2, 2, 3208, 3211, 1, 1, false),
                Arguments.of(1, 3208, 3212, 2, 2, 3209, 3212, 2, 2, true),
                Arguments.of(1, 3208, 3212, 2, 2, 3210, 3212, 2, 2, true),
                Arguments.of(1, 3208, 3212, 2, 2, 3211, 3212, 2, 2, false),
                Arguments.of(1, 3208, 3212, 2, 2, 3208, 3213, 2, 2, true),
                Arguments.of(1, 3208, 3212, 2, 2, 3208, 3214, 2, 2, true),
                Arguments.of(1, 3208, 3212, 2, 2, 3208, 3215, 2, 2, false),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3210, 1, 1, true),
                Arguments.of(10, 3220, 3220, 1, 1, 3209, 3210, 1, 1, false),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3209, 1, 1, false),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3210, 1, 1, true),
                Arguments.of(10, 3220, 3220, 1, 1, 3209, 3210, 2, 2, true),
                Arguments.of(10, 3220, 3220, 1, 1, 3208, 3210, 2, 2, false),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3209, 2, 2, true),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3208, 2, 2, false),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3210, 3, 3, true),
                Arguments.of(10, 3220, 3220, 1, 1, 3209, 3210, 3, 3, true),
                Arguments.of(10, 3220, 3220, 1, 1, 3208, 3210, 3, 3, true),
                Arguments.of(10, 3220, 3220, 1, 1, 3207, 3210, 3, 3, false),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3209, 3, 3, true),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3208, 3, 3, true),
                Arguments.of(10, 3220, 3220, 1, 1, 3210, 3207, 3, 3, false),
            )
    }
}
