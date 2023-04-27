package org.rsmod.game.pathfinder

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.game.pathfinder.flag.CollisionFlag.OBJECT
import kotlin.math.max
import kotlin.math.min

class LinePathFinderLineOfWalkTest {

    @Test
    fun testSameTileHasLineOfWalk() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        with(LinePathFinder(map)) {
            assertTrue(lineOfWalk(0, 3200, 3200, 3200, 3200).success)
        }
    }

    @Test
    fun testPartialLineOfWalk() {
        val map = CollisionFlagMap()
        map[3200, 3205, 0] = OBJECT
        with(LinePathFinder(map)) {
            val rayCast = lineOfWalk(0, 3200, 3200, 3200, 3207)
            Assertions.assertEquals(4, rayCast.size)
            assertFalse(rayCast.success)
            assertTrue(rayCast.alternative)
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun testClearLineOfWalk(dir: Direction) {
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
        with(LinePathFinder(map)) {
            lineOfWalk(level = 0, srcX, srcZ, destX, destZ).let { rayCast ->
                assertTrue(rayCast.isNotEmpty())
                assertTrue(rayCast.success)
                assertFalse(rayCast.alternative)
            }
            lineOfWalk(level = 1, srcX, srcZ, destX, destZ).let { rayCast ->
                assertTrue(rayCast.isNotEmpty())
                assertTrue(rayCast.success)
                assertFalse(rayCast.alternative)
            }
            lineOfWalk(level = 2, srcX, srcZ, destX, destZ).let { rayCast ->
                assertTrue(rayCast.isNotEmpty())
                assertTrue(rayCast.success)
                assertFalse(rayCast.alternative)
            }
            lineOfWalk(level = 3, srcX, srcZ, destX, destZ).let { rayCast ->
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
            map[srcX + dir.offX, srcZ + dir.offZ, level] = CollisionFlag.OBJECT
        }
        with(LinePathFinder(map)) {
            assertFalse(lineOfWalk(0, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfWalk(1, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfWalk(2, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfWalk(3, srcX, srcZ, destX, destZ).success)
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
            map[srcX + dir.offX, srcZ + dir.offZ, level] = extraFlag
        }
        with(LinePathFinder(map)) {
            assertFalse(lineOfWalk(0, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
            assertFalse(lineOfWalk(1, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
            assertFalse(lineOfWalk(2, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
            assertFalse(lineOfWalk(3, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
        }
    }
}
