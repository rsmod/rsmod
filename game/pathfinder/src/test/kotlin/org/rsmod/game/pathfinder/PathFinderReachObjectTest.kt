package org.rsmod.game.pathfinder

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag
import java.util.stream.Stream
import kotlin.math.max
import kotlin.math.min

class PathFinderReachObjectTest {

    // TODO: add tests for rest of object shapes.

    @ParameterizedTest
    @ArgumentsSource(DimensionProvider::class)
    fun testRectangle(width: Int, height: Int) {
        val map = CollisionFlagMap()
        val pathFinder = PathFinder(map)
        val srcX = 3200
        val srcZ = 3200
        val objX = 3203 + width // Ensures object is further than width
        val objZ = 3200
        // Allocate every zone in between the source and
        // destination coordinates.
        for (level in 0 until 4) {
            for (z in min(srcZ, objZ)..max(srcZ, objZ)) {
                for (x in min(srcX, objX)..max(srcX, objX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        // Mark tiles below object.
        for (z in 0 until height) {
            for (x in 0 until width) {
                map[objX + x, objZ + z, 0] = CollisionFlag.OBJECT
            }
        }
        with(
            pathFinder.findPath(
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = objX,
                destZ = objZ,
                objShape = RECTANGLE,
                destWidth = width,
                destHeight = height
            )
        ) {
            assertTrue(success)
            assertFalse(alternative)
        }
    }

    private object DimensionProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(1, 1),
                Arguments.of(1, 2),
                Arguments.of(2, 1),
                Arguments.of(2, 2),
                Arguments.of(3, 3)
            )
        }
    }

    private companion object {

        private const val RECTANGLE: Int = 10
    }
}
