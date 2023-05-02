package org.rsmod.game.pathfinder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.game.pathfinder.collision.buildCollisionMap
import org.rsmod.game.pathfinder.collision.flag
import org.rsmod.game.pathfinder.flag.CollisionFlag

class PathFinderTest {

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun testValidDirectionalPath(dir: Direction) {
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        val map = buildCollisionMap(srcX, srcZ, destX, destZ)
        val pathFinder = PathFinder(map)
        pathFinder.findPath(level = 0, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(0, route.last().level)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
        pathFinder.findPath(level = 1, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(1, route.last().level)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
        pathFinder.findPath(level = 2, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(2, route.last().level)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
        pathFinder.findPath(level = 3, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(3, route.last().level)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun testInvalidDirectionalPath(dir: Direction) {
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
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
