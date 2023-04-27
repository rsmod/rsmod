package org.rsmod.game.pathfinder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag
import kotlin.math.max
import kotlin.math.min

class PathFinderTest {

    @ParameterizedTest
    @EnumSource(Direction::class)
    internal fun testValidDirectionalPath(dir: Direction) {
        val map = CollisionFlagMap()
        val pathFinder = PathFinder(map)
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        // Allocate every zone in between the source and
        // destination coordinates.
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        with(pathFinder.findPath(level = 0, srcX, srcZ, destX, destZ)) {
            assertEquals(1, waypoints.size)
            assertEquals(destX, waypoints.last().x)
            assertEquals(destZ, waypoints.last().z)
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    internal fun testInvalidDirectionalPath(dir: Direction) {
        val map = CollisionFlagMap()
        val pathFinder = PathFinder(map)
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        // Allocate every zone in between the source and
        // destination coordinates.
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        for (level in 0 until 4) {
            map[destX, destZ, level] = CollisionFlag.FLOOR
        }
        with(pathFinder.findPath(level = 0, srcX, srcZ, destX, destZ, moveNear = false)) {
            assertTrue(failed)
            assertTrue(waypoints.isEmpty())
            assertFalse(alternative)
        }
    }
}
