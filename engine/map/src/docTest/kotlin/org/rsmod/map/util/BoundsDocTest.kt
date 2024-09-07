package org.rsmod.map.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BoundsDocTest {
    /** @see [Bounds] */
    @Test
    fun `validate Example Usage code`() {
        val bounds1 = Bounds(x = 3200, z = 3200, level = 0, width = 2, length = 3)
        val bounds2 = Bounds(x = 3205, z = 3207, level = 0, width = 3, length = 1)

        val isWithinDistance = bounds1.isWithinDistance(bounds2, 5)
        assertTrue(isWithinDistance)

        val distance = bounds1.distanceTo(bounds2)
        assertEquals(5, distance)
    }
}
