package org.rsmod.map.square

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MapSquareGridDocTest {
    /** @see [MapSquareGrid] */
    @Test
    fun `validate Example Usage code`() {
        val grid = MapSquareGrid.fromAbsolute(3220, 3205, 0)
        assertEquals(20, grid.x)
        assertEquals(5, grid.z)
        assertEquals(0, grid.level)
    }
}
