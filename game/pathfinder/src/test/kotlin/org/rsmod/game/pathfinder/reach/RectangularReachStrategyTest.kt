package org.rsmod.game.pathfinder.reach

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.pathfinder.Direction
import org.rsmod.game.pathfinder.collision.buildCollisionMap
import org.rsmod.game.pathfinder.collision.flag
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.game.pathfinder.flag.DirectionFlag
import java.util.stream.Stream

class RectangularReachStrategyTest {

    @Test
    fun testDividedByWall() {
        val (srcX, srcZ) = 3200 to 3200
        val (objX, objZ) = 3200 to 3201
        val map = buildCollisionMap(srcX, srcZ, objX, objZ)
        // Wall is located on same tile as source and flagged north.
        map[srcX, srcZ, 0] = CollisionFlag.WALL_NORTH
        assertFalse(
            reachRectangle(
                flags = map,
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = objX,
                destZ = objZ,
                srcSize = 1,
                destWidth = 1,
                destHeight = 1,
                blockAccessFlags = 0
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
            CollisionFlag.WALL_SOUTH_WEST
        ).forEach { flag ->
            map[srcX, srcZ, 0] = flag
            assertTrue(
                reachRectangle(
                    flags = map,
                    level = 0,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = objX,
                    destZ = objZ,
                    srcSize = 1,
                    destWidth = 1,
                    destHeight = 1,
                    blockAccessFlags = 0
                )
            ) { "Should be reachable with collision flag 0x${flag.toString(16)}." }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BlockAccessFlagProvider::class)
    fun testBlockAccessFlag(blockedDir: Direction, blockAccessFlag: Int) {
        val (objX, objZ) = 3205 to 3205
        val map = buildCollisionMap(objX, objZ, objX, objZ)
            .flag(objX, objZ, width = 1, height = 1, mask = CollisionFlag.OBJECT)
        Direction.cardinal.forEach { dir ->
            val (srcX, srcZ) = (objX + dir.offX) to (objZ + dir.offZ)
            map.allocateIfAbsent(srcX, srcZ, 0)
            val reached = reachRectangle(
                flags = map,
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = objX,
                destZ = objZ,
                srcSize = 1,
                destWidth = 1,
                destHeight = 1,
                blockAccessFlags = blockAccessFlag
            )
            if (dir == blockedDir) {
                assertFalse(reached) {
                    "Should not be able to reach object with " +
                        "`blockAccessFlag` 0x${blockAccessFlag.toString(16)} " +
                        "from direction $dir"
                }
            } else {
                assertTrue(reached) {
                    "Should be able to reach object with " +
                        "`blockAccessFlag` 0x${blockAccessFlag.toString(16)} " +
                        "from direction $dir"
                }
            }
        }
    }

    @Suppress("UnnecessaryVariable")
    @ParameterizedTest
    @ArgumentsSource(DimensionProvider::class)
    fun testReachWithDimensions(dimension: Dimension) {
        val (width, height) = dimension
        val (srcX, srcZ) = 3200 to 3200
        val (objX, objZ) = 3205 + width to 3200
        val destX = objX - 1
        val destZ = objZ
        val map = buildCollisionMap(srcX, srcZ, objX, objZ)
            .flag(objX, objZ, width, height, CollisionFlag.OBJECT)
        fun reached(level: Int, srcX: Int, srcZ: Int, destX: Int, destZ: Int): Boolean {
            return reachRectangle(
                flags = map,
                level = level,
                srcX = srcX,
                srcZ = srcZ,
                destX = destX,
                destZ = destZ,
                srcSize = 1,
                destWidth = width,
                destHeight = height,
                blockAccessFlags = 0
            )
        }
        assertFalse(reached(level = 0, srcX = destX - 1, srcZ = destZ, destX = objX, destZ = objZ))
        assertFalse(reached(level = 0, srcX = destX, srcZ = destZ - 1, destX = objX, destZ = objZ))
        assertTrue(reached(level = 0, srcX = destX, srcZ = destZ, destX = objX, destZ = objZ))
        // Being "inside" the object counts as reached.
        for (z in 1 until height) {
            for (x in 1 until width) {
                assertTrue(
                    reached(
                        level = 0,
                        srcX = destX + x,
                        srcZ = destZ + z,
                        destX = objX,
                        destZ = objZ
                    )
                )
            }
        }
    }

    data class Dimension(val width: Int, val height: Int)

    private object DimensionProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(Dimension(1, 1)),
                Arguments.of(Dimension(1, 2)),
                Arguments.of(Dimension(2, 1)),
                Arguments.of(Dimension(2, 2)),
                Arguments.of(Dimension(3, 3))
            )
        }
    }

    private object BlockAccessFlagProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(Direction.North, DirectionFlag.NORTH),
                Arguments.of(Direction.East, DirectionFlag.EAST),
                Arguments.of(Direction.South, DirectionFlag.SOUTH),
                Arguments.of(Direction.West, DirectionFlag.WEST)
            )
        }
    }
}
