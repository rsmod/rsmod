package org.rsmod.map.square

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MapSquareKeyDocTest {
    /** @see [MapSquareKey] */
    @Test
    fun `validate Example Usage code`() {
        val mapSquareKey = MapSquareKey.fromAbsolute(3220, 3205)
        assertEquals(50, mapSquareKey.x)
        assertEquals(50, mapSquareKey.z)
    }
}
