package org.rsmod.map.zone

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ZoneKeyDocTest {
    /** @see [ZoneKey] */
    @Test
    fun `validate Example Usage code`() {
        val zoneKey = ZoneKey.fromAbsolute(3220, 3205, 2)
        assertEquals(402, zoneKey.x)
        assertEquals(400, zoneKey.z)
        assertEquals(2, zoneKey.level)
    }
}
