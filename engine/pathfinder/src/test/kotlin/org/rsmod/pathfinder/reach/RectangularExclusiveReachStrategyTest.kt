package org.rsmod.pathfinder.reach

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.pathfinder.Dimension
import org.rsmod.pathfinder.DimensionProvider
import org.rsmod.pathfinder.Direction
import org.rsmod.pathfinder.collision.buildCollisionMap
import org.rsmod.pathfinder.collision.flag
import org.rsmod.pathfinder.flag.CollisionFlag
import org.rsmod.pathfinder.reach.ReachStrategy.reachExclusiveRectangle

class RectangularExclusiveReachStrategyTest {
    @ParameterizedTest
    @ArgumentsSource(BlockAccessFlagProvider::class)
    fun `reach with block-access flag from appropriate directions`(
        blockedDir: Direction,
        blockAccessFlag: Int,
    ) {
        val (locX, locZ) = 3205 to 3205
        val map =
            buildCollisionMap(locX, locZ, locX, locZ)
                .flag(locX, locZ, width = 1, length = 1, mask = CollisionFlag.LOC)
        Direction.cardinal.forEach { dir ->
            val (srcX, srcZ) = (locX + dir.offX) to (locZ + dir.offZ)
            map.allocateIfAbsent(srcX, srcZ, 0)
            val reached =
                reachExclusiveRectangle(
                    flags = map,
                    level = 0,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = locX,
                    destZ = locZ,
                    srcSize = 1,
                    destWidth = 1,
                    destLength = 1,
                    blockAccessFlags = blockAccessFlag,
                )
            if (dir == blockedDir) {
                assertFalse(reached) {
                    "Should not be able to reach loc with " +
                        "`blockAccessFlag` 0x${blockAccessFlag.toString(16)} " +
                        "from direction $dir"
                }
            } else {
                assertTrue(reached) {
                    "Should be able to reach loc with " +
                        "`blockAccessFlag` 0x${blockAccessFlag.toString(16)} " +
                        "from direction $dir"
                }
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(DimensionProvider::class)
    fun `reach from all valid border coordinates`(dimension: Dimension) {
        val (width, length) = dimension
        val (locX, locZ) = 3202 + width to 3202
        val map =
            buildCollisionMap(locX, locZ, locX + width + 1, locZ + length + 1)
                .flag(locX, locZ, width, length, CollisionFlag.LOC)
        fun reached(srcX: Int, srcZ: Int, destX: Int, destZ: Int): Boolean {
            return reachExclusiveRectangle(
                flags = map,
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = destX,
                destZ = destZ,
                srcSize = 1,
                destWidth = width,
                destLength = length,
            )
        }
        assertFalse(reached(locX - 2, locZ - 1, locX, locZ))
        assertFalse(reached(locX - 1, locZ - 2, locX, locZ))
        for (z in -1 until length + 1) {
            for (x in -1 until width + 1) {
                val reached = reached(locX + x, locZ + z, locX, locZ)
                val southWest = z == -1 && x == -1
                val southEast = z == -1 && x == width
                val northWest = z == length && x == -1
                val northEast = z == length && x == width
                val diagonal = southWest || northEast || southEast || northWest
                if (diagonal) {
                    assertFalse(reached) { "Should not reach with offset ($x, $z)" }
                    continue
                }
                val inLocArea = x in 0 until width && z in 0 until length
                if (inLocArea) {
                    assertFalse(reached) { "Should not reach from within loc area. ($x, $z)" }
                    continue
                }
                assertTrue(reached) { "Should reach with offset ($x, $z)" }
            }
        }
    }
}
