package org.rsmod.pathfinder.reach

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.pathfinder.Dimension
import org.rsmod.pathfinder.RotatedLocProvider
import org.rsmod.pathfinder.collision.buildCollisionMap
import org.rsmod.pathfinder.collision.flag
import org.rsmod.pathfinder.flag.BlockAccessFlag.EAST
import org.rsmod.pathfinder.flag.BlockAccessFlag.NORTH
import org.rsmod.pathfinder.flag.BlockAccessFlag.SOUTH
import org.rsmod.pathfinder.flag.BlockAccessFlag.WEST
import org.rsmod.pathfinder.flag.CollisionFlag

class ReachStrategyTest {
    /**
     * Test that loc angles are taken into account within [ReachStrategy.reached] and do not rely on
     * external modifications.
     *
     * For example, given the parameters of a loc in coordinates (3203, 3203) with a dimension of 3
     * x 1 (width x length), the following test should pass:
     *
     * Loc angle of [0] or [2]. (normal)
     *
     * ```
     * --------
     * --------
     * --------
     * ---ooo--
     * --o   o-
     * ---ooo--
     * --------
     * --------
     * ```
     *
     * Where:
     * - Area starts from bottom-left and makes its way to top-right. (3200,3200 - 3207,3207)
     * - ' ' (whitespace) are the tiles occupied by the rotated loc.
     * - 'o' are the tiles that successfully pass [ReachStrategy.reached].
     * - '-' represents every other tile in the area. (in this case a zone, or 8x8 tile area)
     */
    @ParameterizedTest
    @ArgumentsSource(RotatedLocProvider::class)
    fun `reach loc with angle 0 and 2`(locX: Int, locZ: Int, dimension: Dimension) {
        val (width, length) = dimension
        val (minX, minZ) = locX - 16 to locZ - 16
        val (maxX, maxZ) = locX + 16 to locZ + 16
        val map =
            buildCollisionMap(minX, minZ, maxX, maxZ)
                .flag(locX, locZ, width = width, length = length, mask = CollisionFlag.LOC)
        fun reached(srcX: Int, srcZ: Int, angle: Int, blockAccessFlags: Int = 0): Boolean {
            return ReachStrategy.reached(
                flags = map,
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = locX,
                destZ = locZ,
                destWidth = width,
                destLength = length,
                srcSize = 1,
                locAngle = angle,
                locShape = -2, //  Use rectangular exclusive strategy
                blockAccessFlags = blockAccessFlags,
            )
        }
        for (x in 0 until width) {
            // Test coming from south tiles.
            assertTrue(reached(locX + x, locZ - 1, angle = 0))
            assertTrue(reached(locX + x, locZ - 1, angle = 2))
            // Test coming from north tiles.
            assertTrue(reached(locX + x, locZ + length, angle = 0))
            assertTrue(reached(locX + x, locZ + length, angle = 2))
            // Test coming from south tiles with access blocked.
            assertFalse(reached(locX + x, locZ - 1, angle = 0, blockAccessFlags = SOUTH))
            assertFalse(reached(locX + x, locZ - 1, angle = 2, blockAccessFlags = NORTH))
            // Test coming from north tiles with access blocked.
            assertFalse(reached(locX + x, locZ + length, angle = 0, blockAccessFlags = NORTH))
            assertFalse(reached(locX + x, locZ + length, angle = 2, blockAccessFlags = SOUTH))
        }
        for (z in 0 until length) {
            // Test coming from west tiles.
            assertTrue(reached(locX - 1, locZ + z, angle = 0))
            assertTrue(reached(locX - 1, locZ + z, angle = 2))
            // Test coming from east tiles.
            assertTrue(reached(locX + width, locZ + z, angle = 0))
            assertTrue(reached(locX + width, locZ + z, angle = 2))
            // Test coming from west tiles with access blocked.
            assertFalse(reached(locX - 1, locZ + z, angle = 0, blockAccessFlags = WEST))
            assertFalse(reached(locX - 1, locZ + z, angle = 2, blockAccessFlags = EAST))
            // Test coming from east tiles with access blocked.
            assertFalse(reached(locX + width, locZ + z, angle = 0, blockAccessFlags = EAST))
            assertFalse(reached(locX + width, locZ + z, angle = 2, blockAccessFlags = WEST))
        }
    }

    /**
     * Test that loc angles are taken into account within [ReachStrategy.reached] and do not rely on
     * external modifications.
     *
     * For example, given the parameters of a loc in coordinates (3203, 3203) with a dimension of 3
     * x 1 (width x length), the following test should pass:
     *
     * Loc angles of [1] or [3]. (flipped)
     *
     * ```
     * --------
     * ---o----
     * --o o---
     * --o o---
     * --o o---
     * ---o----
     * --------
     * --------
     * ```
     *
     * Where:
     * - Area starts from bottom-left and makes its way to top-right. (3200,3200 - 3207,3207)
     * - ' ' (whitespace) are the tiles occupied by the rotated loc.
     * - 'o' are the tiles that successfully pass [ReachStrategy.reached].
     * - '-' represents every other tile in the area. (in this case a zone, or 8x8 tile area)
     */
    @ParameterizedTest
    @ArgumentsSource(RotatedLocProvider::class)
    fun `reach loc with angle 1 and 3`(locX: Int, locZ: Int, dimension: Dimension) {
        val (width, length) = dimension
        val (minX, minZ) = locX - 16 to locZ - 16
        val (maxX, maxZ) = locX + 16 to locZ + 16
        val map =
            buildCollisionMap(minX, minZ, maxX, maxZ)
                .flag(locX, locZ, width = length, length = width, mask = CollisionFlag.LOC)
        fun reached(srcX: Int, srcZ: Int, angle: Int, blockAccessFlags: Int = 0): Boolean {
            return ReachStrategy.reached(
                flags = map,
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = locX,
                destZ = locZ,
                destWidth = width,
                destLength = length,
                srcSize = 1,
                locAngle = angle,
                locShape = -2, //  Use rectangular exclusive strategy
                blockAccessFlags = blockAccessFlags,
            )
        }
        for (x in 0 until length) {
            // Test coming from south tiles.
            assertTrue(reached(locX + x, locZ - 1, angle = 1))
            assertTrue(reached(locX + x, locZ - 1, angle = 3))
            // Test coming from north tiles.
            assertTrue(reached(locX + x, locZ + width, angle = 1))
            assertTrue(reached(locX + x, locZ + width, angle = 3))
            // Test coming from south tiles with access blocked.
            assertFalse(reached(locX + x, locZ - 1, angle = 1, blockAccessFlags = EAST))
            assertFalse(reached(locX + x, locZ - 1, angle = 3, blockAccessFlags = WEST))
            // Test coming from north tiles with access blocked.
            assertFalse(reached(locX + x, locZ + width, angle = 1, blockAccessFlags = WEST))
            assertFalse(reached(locX + x, locZ + width, angle = 3, blockAccessFlags = EAST))
        }
        for (z in 0 until width) {
            // Test coming from west tiles.
            assertTrue(reached(locX - 1, locZ + z, angle = 1))
            assertTrue(reached(locX - 1, locZ + z, angle = 3))
            // Test coming from east tiles.
            assertTrue(reached(locX + length, locZ + z, angle = 1))
            assertTrue(reached(locX + length, locZ + z, angle = 3))
            // Test coming from west tiles with access blocked.
            assertFalse(reached(locX - 1, locZ + z, angle = 1, blockAccessFlags = SOUTH))
            assertFalse(reached(locX - 1, locZ + z, angle = 3, blockAccessFlags = NORTH))
            // Test coming from east tiles with access blocked.
            assertFalse(reached(locX + length, locZ + z, angle = 1, blockAccessFlags = NORTH))
            assertFalse(reached(locX + length, locZ + z, angle = 3, blockAccessFlags = SOUTH))
        }
    }
}
