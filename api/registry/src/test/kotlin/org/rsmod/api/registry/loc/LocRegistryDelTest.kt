package org.rsmod.api.registry.loc

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.factory.collisionFactory
import org.rsmod.api.testing.factory.locFactory
import org.rsmod.api.testing.factory.locRegistryFactory
import org.rsmod.api.testing.factory.locTypeListFactory
import org.rsmod.api.testing.factory.mediumBlockRange
import org.rsmod.api.testing.factory.mediumBlockWalk
import org.rsmod.api.testing.factory.smallBlockWalk
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocZoneKey
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.game.map.collision.get
import org.rsmod.game.map.collision.set
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag

class LocRegistryDelTest {
    @Test
    fun `delete spawned loc`() {
        val types = locTypeListFactory.createDefault()
        val loc = locFactory.create(types.smallBlockWalk())

        val locZones = LocZoneStorage()
        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision, locZones)
        check(locZones.totalLocCount() == 0)

        registry.add(loc)
        check(locZones.totalLocCount() == 1)
        check((collision[loc.coords] and CollisionFlag.LOC) != 0)

        registry.del(loc)
        assertEquals(0, locZones.totalLocCount())
        assertEquals(0, locZones.spawnedLocCount())
        assertEquals(0, (collision[loc.coords] and CollisionFlag.LOC))
    }

    @Test
    fun `delete static map loc`() {
        val types = locTypeListFactory.createDefault()
        val mapLoc = locFactory.create(types.smallBlockWalk())

        val zoneKey = ZoneKey.from(mapLoc.coords)
        val zoneGrid = ZoneGrid.from(mapLoc.coords)
        val locZoneKey = LocZoneKey(zoneGrid.x, zoneGrid.z, mapLoc.layer)

        val locZones = LocZoneStorage()
        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision, locZones)
        locZones.mapLocs[zoneKey, locZoneKey] = mapLoc.entity
        check(locZones.totalLocCount() == 1)
        check(locZones.mapLocCount() == 1)

        // Manually set the collision flag under the map loc.
        collision[mapLoc.coords] = CollisionFlag.LOC

        // "Deleting" map locs consists of spawning a "deleted" loc on top of them.
        registry.del(mapLoc)
        assertEquals(1, locZones.mapLocCount())
        assertEquals(1, locZones.spawnedLocCount())

        // Deleting the map loc should have removed its collision.
        assertEquals(0, collision[mapLoc.coords])
    }

    @Test
    fun `restore map loc when deleting spawned loc that replaced it`() {
        val types = locTypeListFactory.createDefault()
        val boundMapLoc = locFactory.createBound(types.smallBlockWalk())
        val boundSpawnLoc = locFactory.createBound(types.mediumBlockRange())

        val mapLoc = boundMapLoc.toLocInfo()
        val spawnLoc = boundSpawnLoc.toLocInfo()

        val zoneKey = ZoneKey.from(mapLoc.coords)
        val zoneGrid = ZoneGrid.from(mapLoc.coords)
        val locZoneKey = LocZoneKey(zoneGrid.x, zoneGrid.z, mapLoc.layer)

        val locZones = LocZoneStorage()
        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision, locZones)

        locZones.mapLocs[zoneKey, locZoneKey] = mapLoc.entity
        check(locZones.totalLocCount() == 1)
        check(locZones.mapLocCount() == 1)

        registry.add(spawnLoc)
        assertEquals(1, locZones.spawnedLocCount())
        // Spawning a loc on top of a map loc should not remove it from map locs collection.
        assertEquals(1, locZones.mapLocCount())

        // Collision for spawned loc should be applied.
        val spawnLocBound = boundSpawnLoc.bounds()
        for (coords in spawnLocBound) {
            assertNotEquals(0, collision[coords] and CollisionFlag.LOC_PROJ_BLOCKER)
        }

        // Delete spawned loc.
        registry.del(spawnLoc)
        assertEquals(0, locZones.spawnedLocCount())
        assertEquals(1, locZones.mapLocCount())

        // Collision for map loc should be restored.
        val mapLocBound = boundMapLoc.bounds()
        for (coords in mapLocBound) {
            assertNotEquals(0, collision[coords] and CollisionFlag.LOC)
        }

        // Collision for spawned loc should be removed.
        for (coords in spawnLocBound) {
            assertEquals(0, collision[coords] and CollisionFlag.LOC_PROJ_BLOCKER)
        }
    }

    @Test
    fun `delete map loc with matching key but mismatching entity metadata`() {
        val types = locTypeListFactory.createDefault()

        val locZones = LocZoneStorage()
        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision, locZones)

        val small = locFactory.create(types.smallBlockWalk())
        val medium = locFactory.create(types.mediumBlockWalk())

        // Manually set the map loc and its collision data.
        val zoneKey = ZoneKey.from(small.coords)
        val locZoneKey = LocZoneKey(ZoneGrid.from(small.coords), small.layer)
        locZones.mapLocs[zoneKey, locZoneKey] = LocEntity(small.id, small.shapeId, small.angleId)
        collision[small.coords] = CollisionFlag.LOC

        // Take a snapshot of the collision data for later.
        val expectedCollision = CollisionFlagMap(collision.flags.copyOf())

        // Since `small` loc shares the same loc shape (thus, layer) and coord grid, `medium` loc
        // would cause the registry to perform the lookups with the same local key as `small` would.
        val removed = registry.del(medium)

        assertEquals(LocRegistryResult.Delete.LocNotFound, removed)
        assertEquals(0, locZones.spawnedLocCount())

        // The `small` map loc shouldn't of have been removed.
        assertEquals(1, locZones.mapLocCount())
        assertNotEquals(0, collision[small.coords] and CollisionFlag.LOC)
        assertArrayEquals(expectedCollision.flags, collision.flags)
    }

    @Test
    fun `delete spawned loc with matching key but mismatching entity metadata`() {
        val types = locTypeListFactory.createDefault()

        val locZones = LocZoneStorage()
        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision, locZones)

        val small = locFactory.create(types.smallBlockWalk())
        val medium = locFactory.create(types.mediumBlockWalk())

        // Add `small` loc into registry.
        registry.add(small)
        check(locZones.spawnedLocCount() == 1)
        check(collision[small.coords] and CollisionFlag.LOC != 0)

        // Take a snapshot of the collision data that was set from `small` being added for later.
        val expectedCollision = CollisionFlagMap(collision.flags.copyOf())

        // Since `small` loc shares the same loc shape (thus, layer) and coord grid, `medium` loc
        // would cause the registry to perform the lookups with the same local key as `small` would.
        val removed = registry.del(medium)

        assertEquals(LocRegistryResult.Delete.LocNotFound, removed)

        // The `small` loc shouldn't of have been removed.
        assertEquals(1, locZones.spawnedLocCount())
        assertNotEquals(0, collision[small.coords] and CollisionFlag.LOC)
        assertArrayEquals(expectedCollision.flags, collision.flags)
    }

    private fun BoundLocInfo.toLocInfo(): LocInfo = LocInfo(layer, coords, entity)
}
