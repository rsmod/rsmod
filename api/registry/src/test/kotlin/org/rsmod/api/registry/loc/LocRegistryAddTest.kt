package org.rsmod.api.registry.loc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.factory.collisionFactory
import org.rsmod.api.testing.factory.locFactory
import org.rsmod.api.testing.factory.locRegistryFactory
import org.rsmod.api.testing.factory.locTypeListFactory
import org.rsmod.api.testing.factory.mediumBlockWalk
import org.rsmod.api.testing.factory.smallBlockRange
import org.rsmod.api.testing.factory.smallBlockWalk
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocShape
import org.rsmod.game.loc.LocZoneKey
import org.rsmod.game.map.collision.get
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.pathfinder.flag.CollisionFlag

class LocRegistryAddTest {
    @Test
    fun `add loc to empty coord grid`() {
        val types = locTypeListFactory.createDefault()
        val loc = locFactory.create(types.smallBlockWalk())

        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision)
        check(registry.count() == 0)

        registry.add(loc)

        assertEquals(1, registry.count())
        assertEquals(1, registry.spawnedLocs.locCount())
        assertEquals(1, registry.findAll(ZoneKey.from(loc.coords)).count())
        assertEquals(loc, registry.findAll(ZoneKey.from(loc.coords)).single())

        assertNotEquals(0, collision[loc.coords] and CollisionFlag.LOC)
    }

    @Test
    fun `add locs to different coordinates in same zone`() {
        val types = locTypeListFactory.createDefault()
        val loc1 = locFactory.create(types.smallBlockWalk(), CoordGrid.ZERO)
        val loc2 = locFactory.create(types.smallBlockWalk(), CoordGrid.ZERO.translateX(1))

        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision)
        check(registry.count() == 0)

        registry.add(loc1)
        registry.add(loc2)

        assertEquals(2, registry.count())
        assertEquals(2, registry.spawnedLocs.locCount())
        assertEquals(2, registry.findAll(ZoneKey.from(loc1.coords)).count())
        assertEquals(setOf(loc1, loc2), registry.findAll(ZoneKey.from(loc1.coords)).toSet())

        assertNotEquals(0, collision[loc1.coords] and CollisionFlag.LOC)
        assertNotEquals(0, collision[loc2.coords] and CollisionFlag.LOC)
    }

    @Test
    fun `add loc on top of same-layer spawned loc`() {
        val types = locTypeListFactory.createDefault()
        val loc1 = locFactory.create(types.smallBlockWalk())
        val loc2 = locFactory.create(types.smallBlockRange())

        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision)
        check(registry.count() == 0)

        registry.add(loc1)
        check(registry.count() == 1)
        registry.add(loc2)

        assertEquals(1, registry.count())
        assertEquals(1, registry.spawnedLocs.locCount())
        assertEquals(1, registry.findAll(ZoneKey.from(loc2.coords)).count())
        assertEquals(loc2, registry.findAll(ZoneKey.from(loc2.coords)).single())

        assertNotEquals(0, collision[loc2.coords] and CollisionFlag.LOC)
    }

    @Test
    fun `add loc on top of differing layer spawned loc`() {
        val types = locTypeListFactory.createDefault()
        val loc1 = locFactory.create(types.smallBlockWalk(), shape = LocShape.GroundDecor)
        val loc2 = locFactory.create(types.smallBlockRange(), shape = LocShape.CentrepieceStraight)

        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision)
        check(registry.count() == 0)

        registry.add(loc1)
        check(registry.count() == 1)
        registry.add(loc2)

        assertEquals(2, registry.count())
        assertEquals(2, registry.spawnedLocs.locCount())
        assertEquals(2, registry.findAll(ZoneKey.from(loc2.coords)).count())
        assertEquals(setOf(loc1, loc2), registry.findAll(ZoneKey.from(loc1.coords)).toSet())
    }

    @Test
    fun `add spawned loc on top of map loc with identical entity metadata`() {
        val types = locTypeListFactory.createDefault()
        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision)

        collision.allocateIfAbsent(0, 0, 0)

        val mapLoc = locFactory.create(types.mediumBlockWalk())
        val spawnLoc = locFactory.create(types.mediumBlockWalk())

        // Manually set the map loc.
        val zoneKey = ZoneKey.from(mapLoc.coords)
        val locZoneKey = LocZoneKey(ZoneGrid.from(mapLoc.coords), mapLoc.layer)
        registry.mapLocs[zoneKey, locZoneKey] = LocEntity(mapLoc.id, mapLoc.shapeId, mapLoc.angleId)
        check(collision[mapLoc.coords] and CollisionFlag.LOC == 0)

        registry.add(spawnLoc)

        // As `spawnLoc` is the exact same (type, shape, and angle) as the already-existing map loc,
        // we don't want to add it to spawned locs. Though we will still perform other logic, such
        // as applying its collision data.
        assertEquals(0, registry.spawnedLocs.locCount())
        assertNotEquals(0, collision[mapLoc.coords] and CollisionFlag.LOC)
    }

    @Test
    fun `add spawned loc on top of map loc with differing entity metadata`() {
        val types = locTypeListFactory.createDefault()
        val collision = collisionFactory.create()
        val registry = locRegistryFactory.create(collision)

        collision.allocateIfAbsent(0, 0, 0)

        val mapLoc = locFactory.create(types.mediumBlockWalk(), angle = LocAngle.West)
        val spawnLoc = locFactory.create(types.mediumBlockWalk(), angle = LocAngle.South)

        // Manually set the map loc.
        val zoneKey = ZoneKey.from(mapLoc.coords)
        val locZoneKey = LocZoneKey(ZoneGrid.from(mapLoc.coords), mapLoc.layer)
        registry.mapLocs[zoneKey, locZoneKey] = LocEntity(mapLoc.id, mapLoc.shapeId, mapLoc.angleId)

        check(collision[mapLoc.coords] and CollisionFlag.LOC == 0)
        check(registry.spawnedLocs.locCount() == 0)
        check(registry.findAll(zoneKey).single() == mapLoc)

        registry.add(spawnLoc)

        assertEquals(1, registry.spawnedLocs.locCount())
        assertNotEquals(0, collision[mapLoc.coords] and CollisionFlag.LOC)

        // Map loc should be masked by newly-spawned loc and be ignored during find lookups.
        assertEquals(setOf(spawnLoc), registry.findAll(zoneKey).toSet())
    }
}
