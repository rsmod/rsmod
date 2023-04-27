package org.rsmod.game.pathfinder

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

class LineValidatorLineOfSightTest {

    @Test
    fun testOnTopOfObjectFailsLineOfSight() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, OBJECT)
        with(LineValidator(map)) {
            assertFalse(hasLineOfSight(0, 3200, 3200, 3200, 3201))
        }
    }

    @Test
    fun testOnTopOfExtraFlagFailsLineOfSight() {
        val map = CollisionFlagMap()
        map.add(3200, 3200, 0, BLOCK_PLAYERS)
        with(LineValidator(map)) {
            assertFalse(hasLineOfSight(0, 3200, 3200, 3200, 3201, extraFlag = BLOCK_PLAYERS))
        }
    }

    @Test
    fun testSameTileHasLineOfSight() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        with(LineValidator(map)) {
            assertTrue(hasLineOfSight(0, 3200, 3200, 3200, 3200))
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
        with(LineValidator(map)) {
            assertTrue(hasLineOfSight(level = 0, srcX, srcZ, destX, destZ))
            assertTrue(hasLineOfSight(level = 1, srcX, srcZ, destX, destZ))
            assertTrue(hasLineOfSight(level = 2, srcX, srcZ, destX, destZ))
            assertTrue(hasLineOfSight(level = 3, srcX, srcZ, destX, destZ))
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
            map[srcX + dir.offX, srcZ + dir.offZ, level] = OBJECT_PROJECTILE_BLOCKER
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
        with(LineValidator(map)) {
            assertFalse(hasLineOfSight(level = 0, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
            assertFalse(hasLineOfSight(level = 1, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
            assertFalse(hasLineOfSight(level = 2, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
            assertFalse(hasLineOfSight(level = 3, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
        }
    }
}
