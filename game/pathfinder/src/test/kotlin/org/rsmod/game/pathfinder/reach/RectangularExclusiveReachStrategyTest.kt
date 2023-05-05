package org.rsmod.game.pathfinder.reach

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
import org.rsmod.game.pathfinder.reach.ReachStrategy.reachExclusiveRectangle
import java.util.stream.Stream

class RectangularExclusiveReachStrategyTest {

    @ParameterizedTest
    @ArgumentsSource(BlockAccessFlagProvider::class)
    fun testBlockAccessFlag(blockedDir: Direction, blockAccessFlag: Int) {
        val (objX, objZ) = 3205 to 3205
        val map = buildCollisionMap(objX, objZ, objX, objZ)
            .flag(objX, objZ, width = 1, height = 1, mask = CollisionFlag.OBJECT)
        Direction.cardinal.forEach { dir ->
            val (srcX, srcZ) = (objX + dir.offX) to (objZ + dir.offZ)
            map.allocateIfAbsent(srcX, srcZ, 0)
            val reached = reachExclusiveRectangle(
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
        val (objX, objZ) = 3202 + width to 3202
        val map = buildCollisionMap(objX, objZ, objX + width + 1, objZ + height + 1)
            .flag(objX, objZ, width, height, CollisionFlag.OBJECT)
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
                destHeight = height,
                blockAccessFlags = 0
            )
        }
        assertFalse(reached(objX - 2, objZ - 1, objX, objZ))
        assertFalse(reached(objX - 1, objZ - 2, objX, objZ))
        for (z in -1 until height + 1) {
            for (x in -1 until width + 1) {
                val reached = reached(objX + x, objZ + z, objX, objZ)
                val diagonal = z == -1 && x == -1 || z == height && x == width
                    || z == -1 && x == width || z == height && x == -1
                if (diagonal) {
                    assertFalse(reached) { "Should not reach with offset ($x, $z)" }
                    continue
                }
                val inObjectArea = x in 0 until width && z in 0 until height
                if (inObjectArea) {
                    assertFalse(reached) { "Should not reach from within object area. ($x, $z)" }
                    continue
                }
                assertTrue(reached) { "Should reach with offset ($x, $z)" }
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
