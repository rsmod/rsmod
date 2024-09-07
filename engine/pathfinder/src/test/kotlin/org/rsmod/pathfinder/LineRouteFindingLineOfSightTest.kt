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
import org.rsmod.pathfinder.flag.CollisionFlag.BLOCK_PLAYERS
import org.rsmod.pathfinder.flag.CollisionFlag.LOC
import org.rsmod.pathfinder.flag.CollisionFlag.LOC_PROJ_BLOCKER

class LineRouteFindingLineOfSightTest {
    @Test
    fun `valid when on top of blocking collision flag if target on same coordinates`() {
        val map = CollisionFlagMap()
        val (x, z) = 3200 to 3200
        for (level in 0 until 4) {
            map[x, z, level] = LOC
        }
        with(LineRouteFinding(map)) {
            assertTrue(lineOfSight(level = 0, x, z, x, z).success)
            assertTrue(lineOfSight(level = 1, x, z, x, z).success)
            assertTrue(lineOfSight(level = 2, x, z, x, z).success)
            assertTrue(lineOfSight(level = 3, x, z, x, z).success)
        }
    }

    @Test
    fun `valid when target coordinate is marked with extraFlag collision flag`() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, BLOCK_PLAYERS)
        with(LineRouteFinding(map)) {
            val rayCast =
                lineOfSight(
                    0,
                    3200,
                    3202,
                    3200,
                    3200,
                    destLength = 1,
                    destWidth = 1,
                    extraFlag = BLOCK_PLAYERS,
                )
            assertEquals(2, rayCast.size)
            assertTrue(rayCast.success)
            assertFalse(rayCast.alternative)
            assertEquals(RouteCoordinates(3200, 3201), rayCast.first())
            assertEquals(RouteCoordinates(3200, 3200), rayCast.last())
        }
    }

    @Test
    fun `fail when blocked by extraFlag before reaching target`() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, BLOCK_PLAYERS)
        with(LineRouteFinding(map)) {
            val rayCast =
                lineOfSight(
                    0,
                    3200,
                    3202,
                    3200,
                    3199,
                    destLength = 1,
                    destWidth = 1,
                    extraFlag = BLOCK_PLAYERS,
                )
            assertFalse(rayCast.isEmpty())
            assertFalse(rayCast.success)
            assertTrue(rayCast.alternative)
            assertEquals(RouteCoordinates(3200, 3201), rayCast.last())
        }
    }

    @Test
    fun `fail when on top of blocking collision flag`() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, LOC)
        with(LineRouteFinding(map)) {
            val rayCast = lineOfSight(0, 3200, 3200, 3200, 3201)
            assertTrue(rayCast.isEmpty())
            assertFalse(rayCast.success)
            assertFalse(rayCast.alternative)
        }
    }

    @Test
    fun `fail when on top of extraFlag collision flag`() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, BLOCK_PLAYERS)
        with(LineRouteFinding(map)) {
            val rayCast = lineOfSight(0, 3200, 3200, 3200, 3201, extraFlag = BLOCK_PLAYERS)
            assertTrue(rayCast.isEmpty())
            assertFalse(rayCast.success)
            assertFalse(rayCast.alternative)
        }
    }

    @Test
    fun `valid and empty ray-cast when on top of target`() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        with(LineRouteFinding(map)) {
            val rayCast = lineOfSight(0, 3200, 3200, 3200, 3200)
            assertTrue(rayCast.isEmpty())
            assertTrue(rayCast.success)
            assertFalse(rayCast.alternative)
        }
    }

    @Test
    fun `partial ray-cast when blocked`() {
        val map = CollisionFlagMap()
        map[3200, 3205, 0] = LOC_PROJ_BLOCKER
        with(LineRouteFinding(map)) {
            val rayCast = lineOfSight(0, 3200, 3200, 3200, 3207)
            assertEquals(4, rayCast.size)
            assertFalse(rayCast.success)
            assertTrue(rayCast.alternative)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ValidLineOfSightFlagsProvider::class)
    fun `valid when passing through non-blocking collision flags`(
        dir: Direction,
        collisionFlags: Int,
    ) {
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
        with(LineRouteFinding(map)) {
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
    fun `fail when blocked by loc`(dir: Direction) {
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
            map[srcX + dir.offX, srcZ + dir.offZ, level] = LOC_PROJ_BLOCKER
        }
        with(LineRouteFinding(map)) {
            assertFalse(lineOfSight(level = 0, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfSight(level = 1, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfSight(level = 2, srcX, srcZ, destX, destZ).success)
            assertFalse(lineOfSight(level = 3, srcX, srcZ, destX, destZ).success)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(DirectionalExtraFlagProvider::class)
    fun `fail when blocked by extraFlag collision flag`(dir: Direction, extraFlag: Int) {
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
        with(LineRouteFinding(map)) {
            assertFalse(
                lineOfSight(level = 0, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success
            )
            assertFalse(
                lineOfSight(level = 1, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success
            )
            assertFalse(
                lineOfSight(level = 2, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success
            )
            assertFalse(
                lineOfSight(level = 3, srcX, srcZ, destX, destZ, extraFlag = extraFlag).success
            )
        }
    }
}
