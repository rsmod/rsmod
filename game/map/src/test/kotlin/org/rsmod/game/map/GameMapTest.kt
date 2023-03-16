package org.rsmod.game.map

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.game.map.entity.obj.ObjectKey
import org.rsmod.game.map.util.collect.ImmutableObjectMap
import org.rsmod.game.map.util.collect.ImmutableZoneMap
import org.rsmod.game.map.util.collect.MutableZoneMap
import org.rsmod.game.map.zone.Zone
import org.rsmod.game.map.zone.ZoneKey

class GameMapTest {

    @Test
    fun testGetEmptyObjectEntries() {
        val map = GameMap(ImmutableZoneMap.empty(), MutableZoneMap.empty())
        assertTrue(map.objectEntries(ZoneKey.from(Coordinates(3200, 3200))).isEmpty())
        assertTrue(map.objectEntries(Coordinates(3200, 3200)).isEmpty())
    }

    @Test
    fun testGetZoneObjectEntries() {
        val zoneKey = ZoneKey(50, 50, 0)
        val zone = Zone(ImmutableObjectMap.empty())
        val objects = mapOf(
            ObjectKey(3, 3, 0) to ObjectEntity(id = 0, shape = 10, rot = 0),
            ObjectKey(3, 3, 1) to ObjectEntity(id = 100, shape = 3, rot = 0),
            ObjectKey(3, 3, 2) to ObjectEntity(id = 400, shape = 2, rot = 3),
            ObjectKey(3, 3, 3) to ObjectEntity(id = 12500, shape = 0, rot = 2),
            ObjectKey(2, 1, 0) to ObjectEntity(id = 2400, shape = 4, rot = 0),
            ObjectKey(5, 3, 2) to ObjectEntity(id = 3200, shape = 5, rot = 0)
        )
        objects.forEach { (key, entity) -> zone[key] = entity }

        // Pseudo-verify all objects added to zone dynamic object map.
        assertEquals(objects.size, zone.dynamicObjects?.size)

        val map = GameMap(
            MutableZoneMap.empty().apply { this[zoneKey.packed] = zone }.immutable(),
            MutableZoneMap.empty().apply { this[zoneKey.packed] = zone }
        )

        // Test object entries from zone key.
        map.objectEntries(zoneKey).let { entries ->
            assertEquals(objects.size, entries.size)
            objects.forEach { (key, entity) ->
                val coords = zoneKey.toCoords().translate(key.x, key.z)
                val entry = entries.firstOrNull { it.slot == key.slot && it.coords == coords }
                assertNotNull(entry)
                assertEquals(entity, entry!!.entity)
            }
        }

        // Test object entries from coords.
        objects.forEach { (key, entity) ->
            val coords = zoneKey.toCoords().translate(key.x, key.z)
            val entry = map.objectEntries(coords).firstOrNull { it.slot == key.slot }
            assertNotNull(entry)
            assertEquals(entity, entry!!.entity)
        }
    }
}
