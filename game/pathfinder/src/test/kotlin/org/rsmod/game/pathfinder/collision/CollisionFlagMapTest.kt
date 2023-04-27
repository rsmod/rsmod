package org.rsmod.game.pathfinder.collision

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CollisionFlagMapTest {

    @Test
    fun testGetCollisionFlagNullZone() {
        val map = CollisionFlagMap()
        check(!map.isZoneAllocated(3200, 3200, 0))
        for (x in 3200 until 3208) {
            for (z in 3200 until 3208) {
                assertEquals(map.defaultFlag(), map[x, z, 0])
            }
        }
    }

    @Test
    fun testGetCollisionFlagAllocatedZone() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        check(map.isZoneAllocated(3200, 3200, 0))
        for (x in 3200 until 3208) {
            for (z in 3200 until 3208) {
                assertEquals(0, map[x, z, 0])
            }
        }
    }

    @Test
    fun testSetCollisionFlag() {
        val map = CollisionFlagMap()
        check(map[3200, 3200, 0] == map.defaultFlag())
        check(map[3200, 3200, 1] == map.defaultFlag())
        check(map[3200, 3200, 2] == map.defaultFlag())
        map[3200, 3200, 0] = 0x800
        map[3200, 3200, 1] = 0x200
        map[3200, 3200, 2] = 0
        assertEquals(0x800, map[3200, 3200, 0])
        assertEquals(0x200, map[3200, 3200, 1])
        assertEquals(0, map[3200, 3200, 2])
    }

    @Test
    fun testAddCollisionFlag() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        check(map[3200, 3200, 0] == 0)
        map.add(3200, 3200, 0, 0x1000)
        assertEquals(0x1000, map[3200, 3200, 0])
        // Add another collision bitflag.
        map.add(3200, 3200, 0, 0x400)
        // Test that initial bitflag has not been reset.
        assertTrue(map[3200, 3200, 0] and 0x1000 != 0)
        // Test that new bitflag has been set.
        assertTrue(map[3200, 3200, 0] and 0x400 != 0)
        // Other tiles in zone should be unaffected.
        for (z in 3201 until 3208) {
            for (x in 3201 until 3208) {
                assertEquals(0, map[x, z, 0])
            }
        }
    }

    @Test
    fun testAddCollisionFlagNullZone() {
        val map = CollisionFlagMap()
        check(!map.isZoneAllocated(3200, 3200, 0))
        map.add(3200, 3200, 0, 0x100)
        assertEquals(0x100, map[3200, 3200, 0])
    }

    @Test
    fun testRemoveSingleCollisionFlag() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        map[3200, 3200, 0] = 0x1000
        check(map[3200, 3200, 0] == 0x1000)
        map.remove(3200, 3200, 0, 0x1000)
        assertEquals(0, map[3200, 3200, 0])
    }

    @Test
    fun testRemoveMultipleCollisionFlags() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        check(map[3200, 3200, 0] == 0)
        map.add(3200, 3200, 0, 0x1000)
        map.add(3200, 3200, 0, 0x400)
        check(map[3200, 3200, 0] and 0x1000 != 0)
        check(map[3200, 3200, 0] and 0x400 != 0)
        map.remove(3200, 3200, 0, 0x1000)
        assertEquals(0x400, map[3200, 3200, 0])
        map.remove(3200, 3200, 0, 0x100)
        assertEquals(0x400, map[3200, 3200, 0])
        map.remove(3200, 3200, 0, 0x400)
        assertEquals(0, map[3200, 3200, 0])
    }

    @Test
    fun testAllocateIfAbsent() {
        val map = CollisionFlagMap()
        check(!map.isZoneAllocated(3200, 3200, 0))
        map.allocateIfAbsent(3200, 3200, 0)
        assertTrue(map.isZoneAllocated(3200, 3200, 0))
        // Test that neighboring zones did not get allocated.
        assertFalse(map.isZoneAllocated(3192, 3192, 0))
        assertFalse(map.isZoneAllocated(3192, 3208, 0))
        assertFalse(map.isZoneAllocated(3208, 3208, 0))
        assertFalse(map.isZoneAllocated(3208, 3192, 0))
    }

    @Test
    fun testDeallocateIfPresent() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        check(map.isZoneAllocated(3200, 3200, 0))
        // Test that deallocating neighboring zones won't affect previous zone.
        map.deallocateIfPresent(3208, 3208, 0)
        assertTrue(map.isZoneAllocated(3200, 3200, 0))
        map.deallocateIfPresent(3196, 3196, 0)
        assertTrue(map.isZoneAllocated(3200, 3200, 0))
        map.deallocateIfPresent(3202, 3202, 0)
        assertFalse(map.isZoneAllocated(3200, 3200, 0))
    }

    @Test
    fun testIsZoneAllocated() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        for (z in 3200 until 3208) {
            for (x in 3200 until 3208) {
                assertTrue(map.isZoneAllocated(x, z, 0))
            }
        }
        for (z in 3192 until 3200) {
            for (x in 3192 until 3200) {
                assertFalse(map.isZoneAllocated(x, z, 0))
            }
        }
        for (z in 3208 until 3216) {
            for (x in 3208 until 3216) {
                assertFalse(map.isZoneAllocated(x, z, 0))
            }
        }
    }
}
