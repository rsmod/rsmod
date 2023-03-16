package org.rsmod.game.map.square

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.map.square.MapSquareKey.Companion.X_BIT_MASK
import org.rsmod.game.map.square.MapSquareKey.Companion.Z_BIT_MASK

class MapSquareKeyTest {

    @Test
    fun testConstruct() {
        for (z in 0..Z_BIT_MASK) {
            for (x in 0..X_BIT_MASK) {
                val key = MapSquareKey(x, z)
                assertEquals(x, key.x)
                assertEquals(z, key.z)
            }
        }
    }

    @Test
    fun testConstructFail() {
        assertThrows<IllegalArgumentException> { MapSquareKey(X_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { MapSquareKey(0, Z_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { MapSquareKey(-1, 0) }
        assertThrows<IllegalArgumentException> { MapSquareKey(0, -1) }
    }
}
