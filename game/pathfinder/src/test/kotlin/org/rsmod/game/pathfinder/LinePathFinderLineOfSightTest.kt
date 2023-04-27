package org.rsmod.game.pathfinder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_PLAYERS
import org.rsmod.game.pathfinder.flag.CollisionFlag.OBJECT
import org.rsmod.game.pathfinder.flag.CollisionFlag.OBJECT_PROJECTILE_BLOCKER
import kotlin.math.max
import kotlin.math.min

class LinePathFinderLineOfSightTest {

    @Test
    fun testOnTopOfObjectFailsLineOfSight() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, OBJECT)
        with(LinePathFinder(map)) {
            val rayCast = lineOfSight(0, 3200, 3200, 3200, 3201)
            assertTrue(rayCast.isEmpty())
            assertFalse(rayCast.success)
            assertFalse(rayCast.alternative)
        }
    }

    @Test
    fun testOnTopOfExtraFlagFailsLineOfSight() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, BLOCK_PLAYERS)
        with(LinePathFinder(map)) {
            val rayCast = lineOfSight(0, 3200, 3200, 3200, 3201, extraFlag = BLOCK_PLAYERS)
            assertTrue(rayCast.isEmpty())
            assertFalse(rayCast.success)
            assertFalse(rayCast.alternative)
        }
    }

    @Test
    fun testSameTileHasLineOfSight() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        with(LinePathFinder(map)) {
            val rayCast = lineOfSight(0, 3200, 3200, 3200, 3200)
            assertTrue(rayCast.isEmpty())
            assertTrue(rayCast.success)
            assertFalse(rayCast.alternative)
        }
    }

    @Test
    fun testPartialLineOfSight() {
        val map = CollisionFlagMap()
        map[3200, 3205, 0] = OBJECT_PROJECTILE_BLOCKER
        with(LinePathFinder(map)) {
            val rayCast = lineOfSight(0, 3200, 3200, 3200, 3207)
            assertEquals(4, rayCast.size)
            assertFalse(rayCast.success)
            assertTrue(rayCast.alternative)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ValidLineOfSightFlagsProvider::class)
    fun testValidLineOfSight(dir: Direction, collisionFlags: Int) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + (dir.offX * 3)
        val destZ = srcZ + (dir.offZ * 3)
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        for (level in 0 until 4) {
            map[srcX + dir.offX, srcZ + dir.offZ, level] = collisionFlags
        }
        with(LinePathFinder(map)) {
            lineOfSight(level = 0, srcX, srcZ, destX, destZ).let { rayCast ->
                assertTrue(rayCast.isNotEmpty())
                assertTrue(rayCast.success)
                assertFalse(rayCast.alternative)
            }
            lineOfSight(level = 1, srcX, srcZ, destX, destZ).let { rayCast ->
                assertTrue(rayCast.isNotEmpty())
                assertTrue(rayCast.success)
                assertFalse(rayCast.alternative)
            }
            lineOfSight(level = 2, srcX, srcZ, destX, destZ).let { rayCast ->
                assertTrue(rayCast.isNotEmpty())
                assertTrue(rayCast.success)
                assertFalse(rayCast.alternative)
            }
            lineOfSight(level = 3, srcX, srcZ, destX, destZ).let { rayCast ->
                assertTrue(rayCast.isNotEmpty())
                assertTrue(rayCast.success)
                assertFalse(rayCast.alternative)
            }
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun testObjectBlocking(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + (dir.offX * 3)
        val destZ = srcZ + (dir.offZ * 3)
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        for (level in 0 until 4) {
            map[srcX + dir.offX, srcZ + dir.offZ, level] = OBJECT_PROJECTILE_BLOCKER
        }
        with(LinePathFinder(map)) {
            assertFalse(lineOfSight(level = 0, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfSight(level = 1, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfSight(level = 2, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfSight(level = 3, srcX, srcZ, destX, destZ).success)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(DirectionalExtraFlagProvider::class)
    fun testExtraFlagBlocking(dir: Direction, extraFlag: Int) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + (dir.offX * 3)
        val destZ = srcZ + (dir.offZ * 3)
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        for (level in 0 until 4) {
            map[srcX + dir.offX, srcZ + dir.offZ, level] = extraFlag
        }
        with(LinePathFinder(map)) {
            assertFalse(lineOfSight(level = 0, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
            assertFalse(lineOfSight(level = 1, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
            assertFalse(lineOfSight(level = 2, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
            assertFalse(lineOfSight(level = 3, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
        }
    }
}
