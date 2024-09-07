package org.rsmod.game.loc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.loc.LocZoneKey.Companion.LAYER_BIT_MASK
import org.rsmod.game.loc.LocZoneKey.Companion.X_BIT_MASK
import org.rsmod.game.loc.LocZoneKey.Companion.Z_BIT_MASK

class LocZoneKeyTest {
    @Test
    fun `construct every loc key combination`() {
        for (layer in 0..LAYER_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val key = LocZoneKey(x, z, layer)
                    assertEquals(x, key.x)
                    assertEquals(z, key.z)
                    assertEquals(layer, key.layer)
                }
            }
        }
    }

    @Test
    fun `deconstruct every loc key combination`() {
        for (layer in 0..LAYER_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val key = LocZoneKey(x, z, layer)
                    val (c1, c2, c3) = key
                    assertEquals(x, c1)
                    assertEquals(z, c2)
                    assertEquals(layer, c3)
                }
            }
        }
    }

    @Test
    fun `fail to construct on out of bound parameter`() {
        assertThrows<IllegalArgumentException> { LocZoneKey(X_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { LocZoneKey(0, Z_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { LocZoneKey(0, 0, LAYER_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { LocZoneKey(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { LocZoneKey(0, -1, 0) }
        assertThrows<IllegalArgumentException> { LocZoneKey(0, 0, -1) }
    }
}
