package org.rsmod.game.map.zone

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.map.zone.ZoneKey.Companion.LEVEL_BIT_MASK
import org.rsmod.game.map.zone.ZoneKey.Companion.X_BIT_MASK
import org.rsmod.game.map.zone.ZoneKey.Companion.Z_BIT_MASK

class ZoneKeyTest {

    @Test
    fun testConstruct() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val key = ZoneKey(x, z, level)
                    assertEquals(x, key.x)
                    assertEquals(z, key.z)
                    assertEquals(level, key.level)
                }
            }
        }
    }

    @Test
    fun testConstructFail() {
        assertThrows<IllegalArgumentException> { ZoneKey(X_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, Z_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, 0, LEVEL_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { ZoneKey(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, -1, 0) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, 0, -1) }
    }
}
