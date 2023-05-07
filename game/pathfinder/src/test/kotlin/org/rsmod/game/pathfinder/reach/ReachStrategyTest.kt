package org.rsmod.game.pathfinder.reach

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.pathfinder.Dimension
import org.rsmod.game.pathfinder.RotatedObjectProvider
import org.rsmod.game.pathfinder.collision.buildCollisionMap
import org.rsmod.game.pathfinder.collision.flag
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_EAST
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_NORTH
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_SOUTH
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_WEST
import org.rsmod.game.pathfinder.flag.CollisionFlag

class ReachStrategyTest {

    /**
     * Test that object rotations are taken into account within [ReachStrategy.reached]
     * and do not rely on external modifications. For example, given the parameters of
     * an object in coordinates (3203, 3203) with a dimension of 3 x 1 (width x height),
     * the following test should pass:
     *
     * Object rotation of [0] or [2]. (normal)
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
     * Where:
     * - Area starts from bottom-left and makes its way to top-right. (3200,3200 - 3207,3207)
     * - ' ' (whitespace) are the tiles occupied by the rotated object.
     * - 'o' are the tiles that successfully pass [ReachStrategy.reached].
     * - '-' represents every other tile in the area. (in this case a zone, or 8x8 tile area)
     */
    @ParameterizedTest
    @ArgumentsSource(RotatedObjectProvider::class)
    fun testRotatedObjectNormal(objX: Int, objZ: Int, dimension: Dimension) {
        val (width, height) = dimension
        val (minX, minZ) = objX - 16 to objZ - 16
        val (maxX, maxZ) = objX + 16 to objZ + 16
        val map = buildCollisionMap(minX, minZ, maxX, maxZ)
            .flag(objX, objZ, width = width, height = height, mask = CollisionFlag.OBJECT)
        fun reached(srcX: Int, srcZ: Int, rot: Int, blockAccessFlags: Int = 0): Boolean {
            return ReachStrategy.reached(
                flags = map,
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = objX,
                destZ = objZ,
                destWidth = width,
                destHeight = height,
                srcSize = 1,
                objRot = rot,
                objShape = -2, //  Use rectangular exclusive strategy
                blockAccessFlags = blockAccessFlags
            )
        }
        for (x in 0 until width) {
            // Test coming from south tiles.
            assertTrue(reached(objX + x, objZ - 1, rot = 0))
            assertTrue(reached(objX + x, objZ - 1, rot = 2))
            // Test coming from north tiles.
            assertTrue(reached(objX + x, objZ + height, rot = 0))
            assertTrue(reached(objX + x, objZ + height, rot = 2))
            // Test coming from south tiles with access blocked.
            assertFalse(reached(objX + x, objZ - 1, rot = 0, blockAccessFlags = BLOCK_SOUTH))
            assertFalse(reached(objX + x, objZ - 1, rot = 2, blockAccessFlags = BLOCK_NORTH))
            // Test coming from north tiles with access blocked.
            assertFalse(reached(objX + x, objZ + height, rot = 0, blockAccessFlags = BLOCK_NORTH))
            assertFalse(reached(objX + x, objZ + height, rot = 2, blockAccessFlags = BLOCK_SOUTH))
        }
        for (z in 0 until height) {
            // Test coming from west tiles.
            assertTrue(reached(objX - 1, objZ + z, rot = 0))
            assertTrue(reached(objX - 1, objZ + z, rot = 2))
            // Test coming from east tiles.
            assertTrue(reached(objX + width, objZ + z, rot = 0))
            assertTrue(reached(objX + width, objZ + z, rot = 2))
            // Test coming from west tiles with access blocked.
            assertFalse(reached(objX - 1, objZ + z, rot = 0, blockAccessFlags = BLOCK_WEST))
            assertFalse(reached(objX - 1, objZ + z, rot = 2, blockAccessFlags = BLOCK_EAST))
            // Test coming from east tiles with access blocked.
            assertFalse(reached(objX + width, objZ + z, rot = 0, blockAccessFlags = BLOCK_EAST))
            assertFalse(reached(objX + width, objZ + z, rot = 2, blockAccessFlags = BLOCK_WEST))
        }
    }

    /**
     * Test that object rotations are taken into account within [ReachStrategy.reached]
     * and do not rely on external modifications. For example, given the parameters of
     * an object in coordinates (3203, 3203) with a dimension of 3 x 1 (width x height),
     * the following test should pass:
     *
     * Object rotation of [1] or [3]. (flipped)
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
     * Where:
     * - Area starts from bottom-left and makes its way to top-right. (3200,3200 - 3207,3207)
     * - ' ' (whitespace) are the tiles occupied by the rotated object.
     * - 'o' are the tiles that successfully pass [ReachStrategy.reached].
     * - '-' represents every other tile in the area. (in this case a zone, or 8x8 tile area)
     */
    @ParameterizedTest
    @ArgumentsSource(RotatedObjectProvider::class)
    fun testRotatedObjectFlipped(objX: Int, objZ: Int, dimension: Dimension) {
        val (width, height) = dimension
        val (minX, minZ) = objX - 16 to objZ - 16
        val (maxX, maxZ) = objX + 16 to objZ + 16
        val map = buildCollisionMap(minX, minZ, maxX, maxZ)
            .flag(objX, objZ, width = height, height = width, mask = CollisionFlag.OBJECT)
        fun reached(srcX: Int, srcZ: Int, rot: Int, blockAccessFlags: Int = 0): Boolean {
            return ReachStrategy.reached(
                flags = map,
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = objX,
                destZ = objZ,
                destWidth = width,
                destHeight = height,
                srcSize = 1,
                objRot = rot,
                objShape = -2, //  Use rectangular exclusive strategy
                blockAccessFlags = blockAccessFlags
            )
        }
        for (x in 0 until height) {
            // Test coming from south tiles.
            assertTrue(reached(objX + x, objZ - 1, rot = 1))
            assertTrue(reached(objX + x, objZ - 1, rot = 3))
            // Test coming from north tiles.
            assertTrue(reached(objX + x, objZ + width, rot = 1))
            assertTrue(reached(objX + x, objZ + width, rot = 3))
            // Test coming from south tiles with access blocked.
            assertFalse(reached(objX + x, objZ - 1, rot = 1, blockAccessFlags = BLOCK_EAST))
            assertFalse(reached(objX + x, objZ - 1, rot = 3, blockAccessFlags = BLOCK_WEST))
            // Test coming from north tiles with access blocked.
            assertFalse(reached(objX + x, objZ + width, rot = 1, blockAccessFlags = BLOCK_WEST))
            assertFalse(reached(objX + x, objZ + width, rot = 3, blockAccessFlags = BLOCK_EAST))
        }
        for (z in 0 until width) {
            // Test coming from west tiles.
            assertTrue(reached(objX - 1, objZ + z, rot = 1))
            assertTrue(reached(objX - 1, objZ + z, rot = 3))
            // Test coming from east tiles.
            assertTrue(reached(objX + height, objZ + z, rot = 1))
            assertTrue(reached(objX + height, objZ + z, rot = 3))
            // Test coming from west tiles with access blocked.
            assertFalse(reached(objX - 1, objZ + z, rot = 1, blockAccessFlags = BLOCK_SOUTH))
            assertFalse(reached(objX - 1, objZ + z, rot = 3, blockAccessFlags = BLOCK_NORTH))
            // Test coming from east tiles with access blocked.
            assertFalse(reached(objX + height, objZ + z, rot = 1, blockAccessFlags = BLOCK_NORTH))
            assertFalse(reached(objX + height, objZ + z, rot = 3, blockAccessFlags = BLOCK_SOUTH))
        }
    }
}
