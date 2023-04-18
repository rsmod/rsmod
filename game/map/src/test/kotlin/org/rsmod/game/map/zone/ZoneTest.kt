package org.rsmod.game.map.zone

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.game.map.entity.obj.ObjectKey
import org.rsmod.game.map.util.collect.ImmutableObjectMap

class ZoneTest {

    @Test
    fun testEmptyZone() {
        val zone = Zone(ImmutableObjectMap.empty())
        assertTrue(zone.entrySet().isEmpty())
        for (slot in 0..ObjectKey.SLOT_BIT_MASK) {
            for (z in 0..ObjectKey.Z_BIT_MASK) {
                for (x in 0..ObjectKey.X_BIT_MASK) {
                    val key = ObjectKey(x, z, slot)
                    assertNull(zone[key])
                    assertThrows<NoSuchElementException> { zone.getValue(key) }
                }
            }
        }
    }

    @Test
    fun testSetAndGet() {
        val zone = Zone(ImmutableObjectMap.empty())
        val key = ObjectKey(x = 0, z = 1, slot = 0)
        val entity = ObjectEntity(id = 1, shape = 10, rot = 0)
        zone[key] = entity
        assertNotNull(zone.dynamicObjects)
        assertEquals(1, zone.dynamicObjects?.size)
        assertEquals(0, zone.staticObjects.size)
        assertEquals(entity.packed, zone[key])
    }
}
