package org.rsmod.pathfinder

import kotlin.math.max
import kotlin.math.min
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.pathfinder.collision.CollisionStrategy
import org.rsmod.pathfinder.flag.CollisionFlag
import org.rsmod.pathfinder.flag.CollisionFlag.LOC
import org.rsmod.pathfinder.flag.CollisionFlag.LOC_PROJ_BLOCKER

class StepValidatorTest {
    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `validate clear path`(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        // Make sure to allocate every zone in between the source
        // and destination coordinates.
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        with(StepValidator(map)) {
            assertTrue(canTravel(level = 0, srcX, srcZ, dir.offX, dir.offZ))
            assertTrue(canTravel(level = 1, srcX, srcZ, dir.offX, dir.offZ))
            assertTrue(canTravel(level = 2, srcX, srcZ, dir.offX, dir.offZ))
            assertTrue(canTravel(level = 3, srcX, srcZ, dir.offX, dir.offZ))
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `fail when path blocked`(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        // Make sure to allocate every zone in between the source
        // and destination coordinates.
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        for (level in 0 until 4) {
            map[destX, destZ, level] = LOC
        }
        with(StepValidator(map)) {
            assertFalse(canTravel(level = 0, srcX, srcZ, dir.offX, dir.offZ))
            assertFalse(canTravel(level = 1, srcX, srcZ, dir.offX, dir.offZ))
            assertFalse(canTravel(level = 2, srcX, srcZ, dir.offX, dir.offZ))
            assertFalse(canTravel(level = 3, srcX, srcZ, dir.offX, dir.offZ))
        }
    }

    @ParameterizedTest
    @ArgumentsSource(DirectionalExtraFlagProvider::class)
    fun `test when path blocked by dynamic extraFlag parameter`(dir: Direction, extraFlag: Int) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        // Make sure to allocate every zone in between the source
        // and destination coordinates.
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        for (level in 0 until 4) {
            map[destX, destZ, level] = extraFlag
        }
        with(StepValidator(map)) {
            assertFalse(canTravel(level = 0, srcX, srcZ, dir.offX, dir.offZ, extraFlag = extraFlag))
            assertFalse(canTravel(level = 1, srcX, srcZ, dir.offX, dir.offZ, extraFlag = extraFlag))
            assertFalse(canTravel(level = 2, srcX, srcZ, dir.offX, dir.offZ, extraFlag = extraFlag))
            assertFalse(canTravel(level = 3, srcX, srcZ, dir.offX, dir.offZ, extraFlag = extraFlag))
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `validate Blocked strategy path`(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        // Need to make sure every tile in between the two coordinates
        // is marked properly, otherwise the collision strategy will not
        // allow diagonal movement.
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map[x, z, level] = CollisionFlag.BLOCK_WALK
                }
            }
        }
        with(StepValidator(map)) {
            val strategy = CollisionStrategy.Blocked
            assertTrue(canTravel(level = 0, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 1, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 2, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 3, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `validate Indoors strategy path`(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        val outdoorsX = destX + dir.offX
        val outdoorsZ = destZ + dir.offZ
        // Need to make sure every tile in between the two coordinates
        // is marked properly, otherwise the collision strategy will not
        // allow diagonal movement.
        for (level in 0 until 4) {
            for (z in min(srcZ, min(destZ, outdoorsZ))..max(srcZ, max(destZ, outdoorsZ))) {
                for (x in min(srcX, min(destX, outdoorsX))..max(srcX, max(destX, outdoorsX))) {
                    map[x, z, level] = CollisionFlag.ROOF
                }
            }
        }
        // Overwrite the outdoors tiles to remove indoor flag.
        for (level in 0 until 4) {
            map[outdoorsX, outdoorsZ, level] = 0
        }
        with(StepValidator(map)) {
            val strategy = CollisionStrategy.Indoors
            // Test step is valid if destination is also flagged as indoors.
            assertTrue(canTravel(level = 0, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 1, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 2, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 3, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            // Test step is invalid if destination is not flagged as indoors.
            assertFalse(
                canTravel(level = 0, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
            assertFalse(
                canTravel(level = 1, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
            assertFalse(
                canTravel(level = 2, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
            assertFalse(
                canTravel(level = 3, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `validate Outdoors strategy path`(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        val indoorsX = destX + dir.offX
        val indoorsZ = destZ + dir.offZ
        // Make sure to allocate every zone in between the source
        // and destination coordinates.
        for (level in 0 until 4) {
            for (z in min(srcZ, min(destZ, indoorsZ))..max(srcZ, max(destZ, indoorsZ))) {
                for (x in min(srcX, min(destX, indoorsX))..max(srcX, max(destX, indoorsX))) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        // Set the indoor tile flags.
        for (level in 0 until 4) {
            map[indoorsX, indoorsZ, level] = CollisionFlag.ROOF
        }
        with(StepValidator(map)) {
            val strategy = CollisionStrategy.Outdoors
            // Test step is valid if destination _is not_ flagged as indoors.
            assertTrue(canTravel(level = 0, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 1, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 2, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 3, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            // Test step is invalid if destination _is_ flagged as indoors.
            assertFalse(
                canTravel(level = 0, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
            assertFalse(
                canTravel(level = 1, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
            assertFalse(
                canTravel(level = 2, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
            assertFalse(
                canTravel(level = 3, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `validate LineOfSight strategy path`(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + dir.offX
        val destZ = srcZ + dir.offZ
        val blockedX = destX + dir.offX
        val blockedZ = destZ + dir.offZ
        // Make sure to allocate every zone in between the source
        // and destination coordinates.
        for (level in 0 until 4) {
            for (z in min(srcZ, min(destZ, blockedZ))..max(srcZ, max(destZ, blockedZ))) {
                for (x in min(srcX, min(destX, blockedX))..max(srcX, max(destX, blockedX))) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        for (level in 0 until 4) {
            map[blockedX, blockedZ, level] = LOC_PROJ_BLOCKER
        }
        with(StepValidator(map)) {
            val strategy = CollisionStrategy.LineOfSight
            // Test step is valid if destination _is not_ flagged with projectile block flag.
            assertTrue(canTravel(level = 0, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 1, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 2, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            assertTrue(canTravel(level = 3, srcX, srcZ, dir.offX, dir.offZ, collision = strategy))
            // Test step is invalid if destination _is_ flagged with projectile block flag.
            assertFalse(
                canTravel(level = 0, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
            assertFalse(
                canTravel(level = 1, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
            assertFalse(
                canTravel(level = 2, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
            assertFalse(
                canTravel(level = 3, destX, destZ, dir.offX, dir.offZ, collision = strategy)
            )
        }
    }
}
