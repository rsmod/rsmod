package org.rsmod.pathfinder

import kotlin.math.max
import kotlin.math.min
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.pathfinder.flag.CollisionFlag.LOC

class LineRouteFindingLineOfWalkTest {
    @Test
    fun `valid when on top of target coordinates`() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        with(LineRouteFinding(map)) { assertTrue(lineOfWalk(0, 3200, 3200, 3200, 3200).success) }
    }

    @Test
    fun `partial ray-cast when blocked`() {
        val map = CollisionFlagMap()
        map[3200, 3205, 0] = LOC
        with(LineRouteFinding(map)) {
            val rayCast = lineOfWalk(0, 3200, 3200, 3200, 3207)
            assertEquals(4, rayCast.size)
            assertFalse(rayCast.success)
            assertTrue(rayCast.alternative)
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `valid when path clear of collision flags`(dir: Direction) {
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
        with(LineRouteFinding(map)) {
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
    fun `fail when path blocked by loc`(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + (dir.offX * 3)
        val destZ = srcZ + (dir.offZ * 3)
        for (level in 0 until 4) {
            map[srcX + dir.offX, srcZ + dir.offZ, level] = LOC
        }
        with(LineRouteFinding(map)) {
            assertFalse(lineOfWalk(0, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfWalk(1, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfWalk(2, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfWalk(3, srcX, srcZ, destX, destZ).success)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(DirectionalExtraFlagProvider::class)
    fun `fail when path blocked by extraFlag collision flag`(dir: Direction, extraFlag: Int) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + (dir.offX * 3)
        val destZ = srcZ + (dir.offZ * 3)
        for (level in 0 until 4) {
            map[srcX + dir.offX, srcZ + dir.offZ, level] = extraFlag
        }
        with(LineRouteFinding(map)) {
            assertFalse(lineOfWalk(0, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
            assertFalse(lineOfWalk(1, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
            assertFalse(lineOfWalk(2, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
            assertFalse(lineOfWalk(3, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success)
        }
    }
}
