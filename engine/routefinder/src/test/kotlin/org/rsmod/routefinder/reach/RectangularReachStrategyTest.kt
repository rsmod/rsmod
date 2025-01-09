package org.rsmod.routefinder.reach

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.routefinder.Dimension
import org.rsmod.routefinder.DimensionProvider
import org.rsmod.routefinder.Direction
import org.rsmod.routefinder.collision.buildCollisionMap
import org.rsmod.routefinder.collision.flag
import org.rsmod.routefinder.flag.CollisionFlag
import org.rsmod.routefinder.reach.ReachStrategy.reachRectangle

class RectangularReachStrategyTest {
    @Test
    fun `fail reach when divided by appropriate wall collision`() {
        val (srcX, srcZ) = 3200 to 3200
        val (locX, locZ) = 3200 to 3201
        val map = buildCollisionMap(srcX, srcZ, locX, locZ)
        // Wall is located on same tile as source and flagged north.
        map[srcX, srcZ, 0] = CollisionFlag.WALL_NORTH
        assertFalse(
            reachRectangle(
                flags = map,
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = locX,
                destZ = locZ,
                srcSize = 1,
                destWidth = 1,
                destLength = 1,
            )
        )
        // Wall in every other direction should allow reach rectangle
        // to return true.
        listOf(
                CollisionFlag.WALL_EAST,
                CollisionFlag.WALL_SOUTH,
                CollisionFlag.WALL_WEST,
                CollisionFlag.WALL_NORTH_WEST,
                CollisionFlag.WALL_NORTH_EAST,
                CollisionFlag.WALL_SOUTH_EAST,
                CollisionFlag.WALL_SOUTH_WEST,
            )
            .forEach { flag ->
                map[srcX, srcZ, 0] = flag
                assertTrue(
                    reachRectangle(
                        flags = map,
                        level = 0,
                        srcX = srcX,
                        srcZ = srcZ,
                        destX = locX,
                        destZ = locZ,
                        srcSize = 1,
                        destWidth = 1,
                        destLength = 1,
                    )
                ) {
                    "Should be reachable with collision flag 0x${flag.toString(16)}."
                }
            }
    }

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
                reachRectangle(
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
            buildCollisionMap(locX - 1, locZ - 1, locX + width, locZ + length)
                .flag(locX, locZ, width, length, CollisionFlag.LOC)
        fun reached(srcX: Int, srcZ: Int, destX: Int, destZ: Int): Boolean {
            return reachRectangle(
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
        // Touching the loc _and_ being inside its occupied area counts
        // as reached.
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
                assertTrue(reached) { "Should reach with offset ($x, $z)" }
            }
        }
    }
}
