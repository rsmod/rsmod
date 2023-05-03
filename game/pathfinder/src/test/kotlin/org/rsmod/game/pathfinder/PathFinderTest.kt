package org.rsmod.game.pathfinder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.game.pathfinder.collision.buildCollisionMap
import org.rsmod.game.pathfinder.collision.flag
import org.rsmod.game.pathfinder.flag.CollisionFlag

class PathFinderTest {

    @Test
    fun testRouteCoordinatesMatchLevelInput() {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = 3201 to 3200
        val map = buildCollisionMap(srcX, srcZ, destX, destZ)
        val pathFinder = PathFinder(map)
        pathFinder.findPath(level = 0, srcX, srcZ, destX, destZ).let { route ->
            check(route.success)
            assertTrue(route.all { it.level == 0 })
        }
        pathFinder.findPath(level = 1, srcX, srcZ, destX, destZ).let { route ->
            check(route.success)
            assertTrue(route.all { it.level == 1 })
        }
        pathFinder.findPath(level = 2, srcX, srcZ, destX, destZ).let { route ->
            check(route.success)
            assertTrue(route.all { it.level == 2 })
        }
        pathFinder.findPath(level = 3, srcX, srcZ, destX, destZ).let { route ->
            check(route.success)
            assertTrue(route.all { it.level == 3 })
        }
    }

    @Test
    fun testSurroundedByBoxes() {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = 3205 to 3200
        val map = buildCollisionMap(srcX, srcZ, destX, destZ)
        // Surround source tile with object collision flag.
        for (z in -1..1) {
            for (x in -1..1) {
                if (x == 0 && z == 0) continue // Skip center (source) tile.
                map[srcX + x, srcZ + z, 0] = CollisionFlag.OBJECT
            }
        }
        val pathFinder = PathFinder(map)
        pathFinder.findPath(level = 0, srcX, srcZ, destX, destZ).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
        }
    }

    @Test
    fun testSurroundedByBoxesSingleExitPoint() {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = 3200 to 3205
        val map = buildCollisionMap(srcX, srcZ, destX, destZ)
        // Surround source tile with object collision flag.
        for (z in -1..1) {
            for (x in -1..1) {
                if (x == 0 && z == 0) continue // Skip center (source) tile.
                if (x == 0 && z == -1) continue // Add exit point south of center tile.
                map[srcX + x, srcZ + z, 0] = CollisionFlag.OBJECT
            }
        }
        val pathFinder = PathFinder(map)
        pathFinder.findPath(level = 0, srcX, srcZ, destX, destZ).let { route ->
            assertTrue(route.success)
            assertEquals(4, route.waypoints.size)
            assertEquals(RouteCoordinates(3200, 3198), route.waypoints[0])
            assertEquals(RouteCoordinates(3198, 3198), route.waypoints[1])
            assertEquals(RouteCoordinates(3198, 3203), route.waypoints[2])
            assertEquals(RouteCoordinates(destX, destZ), route.waypoints.last())
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun testValidDirectionalPath(dir: Direction) {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = (srcX + dir.offX) to (srcZ + dir.offZ)
        val map = buildCollisionMap(srcX, srcZ, destX, destZ)
        val pathFinder = PathFinder(map)
        pathFinder.findPath(level = 0, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
        pathFinder.findPath(level = 1, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
        pathFinder.findPath(level = 2, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
        pathFinder.findPath(level = 3, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun testInvalidDirectionalPath(dir: Direction) {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = (srcX + dir.offX) to (srcZ + dir.offZ)
        val map = buildCollisionMap(srcX, srcZ, destX, destZ)
            .flag(destX, destZ, width = 1, height = 1, mask = CollisionFlag.FLOOR)
        val pathFinder = PathFinder(map)
        pathFinder.findPath(level = 0, srcX, srcZ, destX, destZ, moveNear = false).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
            assertFalse(route.alternative)
        }
        pathFinder.findPath(level = 1, srcX, srcZ, destX, destZ, moveNear = false).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
            assertFalse(route.alternative)
        }
        pathFinder.findPath(level = 2, srcX, srcZ, destX, destZ, moveNear = false).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
            assertFalse(route.alternative)
        }
        pathFinder.findPath(level = 3, srcX, srcZ, destX, destZ, moveNear = false).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
            assertFalse(route.alternative)
        }
    }
}
