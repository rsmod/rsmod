package org.rsmod.routefinder

import kotlin.math.max
import kotlin.math.min
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag.BLOCK_PLAYERS
import org.rsmod.routefinder.flag.CollisionFlag.LOC
import org.rsmod.routefinder.flag.CollisionFlag.LOC_PROJ_BLOCKER

class LineValidatorLineOfSightTest {
    @Test
    fun `valid when on top of blocking collision flag if target on same coordinates`() {
        val map = CollisionFlagMap()
        val (x, z) = 3200 to 3200
        for (level in 0 until 4) {
            map[x, z, level] = LOC
        }
        with(LineValidator(map)) {
            assertTrue(hasLineOfSight(level = 0, x, z, x, z))
            assertTrue(hasLineOfSight(level = 1, x, z, x, z))
            assertTrue(hasLineOfSight(level = 2, x, z, x, z))
            assertTrue(hasLineOfSight(level = 3, x, z, x, z))
        }
    }

    @Test
    fun `valid when target coordinate is marked with extraFlag collision flag`() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, BLOCK_PLAYERS)
        with(LineValidator(map)) {
            assertTrue(
                hasLineOfSight(
                    0,
                    3200,
                    3202,
                    3200,
                    3200,
                    destLength = 1,
                    destWidth = 1,
                    extraFlag = BLOCK_PLAYERS,
                )
            )
        }
    }

    @Test
    fun `fail when blocked by extraFlag before reaching target`() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, BLOCK_PLAYERS)
        with(LineValidator(map)) {
            assertFalse(
                hasLineOfSight(
                    0,
                    3200,
                    3202,
                    3200,
                    3199,
                    destLength = 1,
                    destWidth = 1,
                    extraFlag = BLOCK_PLAYERS,
                )
            )
        }
    }

    @Test
    fun `fail when on top of blocking collision flag`() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, LOC)
        with(LineValidator(map)) { assertFalse(hasLineOfSight(0, 3200, 3200, 3200, 3201)) }
    }

    @Test
    fun `fail when on top of extraFlag collision flag`() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, BLOCK_PLAYERS)
        with(LineValidator(map)) {
            assertFalse(hasLineOfSight(0, 3200, 3200, 3200, 3201, extraFlag = BLOCK_PLAYERS))
        }
    }

    @Test
    fun `valid when on top of target`() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        with(LineValidator(map)) { assertTrue(hasLineOfSight(0, 3200, 3200, 3200, 3200)) }
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
        with(LineValidator(map)) {
            assertTrue(hasLineOfSight(level = 0, srcX, srcZ, destX, destZ))
            assertTrue(hasLineOfSight(level = 1, srcX, srcZ, destX, destZ))
            assertTrue(hasLineOfSight(level = 2, srcX, srcZ, destX, destZ))
            assertTrue(hasLineOfSight(level = 3, srcX, srcZ, destX, destZ))
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
            map[srcX + dir.offX, srcZ + dir.offZ, level] = LOC_PROJ_BLOCKER
        }
        with(LineValidator(map)) {
            assertFalse(hasLineOfSight(level = 0, srcX, srcZ, destX, destZ))
            assertFalse(hasLineOfSight(level = 1, srcX, srcZ, destX, destZ))
            assertFalse(hasLineOfSight(level = 2, srcX, srcZ, destX, destZ))
            assertFalse(hasLineOfSight(level = 3, srcX, srcZ, destX, destZ))
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
        with(LineValidator(map)) {
            assertFalse(hasLineOfSight(level = 0, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
            assertFalse(hasLineOfSight(level = 1, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
            assertFalse(hasLineOfSight(level = 2, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
            assertFalse(hasLineOfSight(level = 3, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
        }
    }
}
