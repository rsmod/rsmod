package org.rsmod.map.zone

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ZoneGridDocTest {
    /** @see [ZoneGrid] */
    @Test
    fun `validate Example Usage code`() {
        val zoneGrid = ZoneGrid.fromAbsolute(3220, 3205, 0)
        assertEquals(4, zoneGrid.x)
        assertEquals(5, zoneGrid.z)
        assertEquals(0, zoneGrid.level)
    }
}
