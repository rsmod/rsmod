package org.rsmod.game.map.entity.obj

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.map.entity.obj.ObjectKey.Companion.SLOT_BIT_MASK
import org.rsmod.game.map.entity.obj.ObjectKey.Companion.X_BIT_MASK
import org.rsmod.game.map.entity.obj.ObjectKey.Companion.Z_BIT_MASK

class ObjectKeyTest {

    @Test
    fun testConstruct() {
        for (slot in 0..SLOT_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val key = ObjectKey(x, z, slot)
                    assertEquals(x, key.x)
                    assertEquals(z, key.z)
                    assertEquals(slot, key.slot)
                }
            }
        }
    }

    @Test
    fun testConstructFail() {
        assertThrows<IllegalArgumentException> { ObjectKey(X_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { ObjectKey(0, Z_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { ObjectKey(0, 0, SLOT_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { ObjectKey(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { ObjectKey(0, -1, 0) }
        assertThrows<IllegalArgumentException> { ObjectKey(0, 0, -1) }
    }
}
