package org.rsmod.game.pathfinder

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag
import java.util.stream.Stream
import kotlin.math.sqrt

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PathFinderTest {

    private val flags = CollisionFlagMap()
    private val pathFinder = PathFinder(flags)

    @Test
    fun `reach empty tile`() {
        val src = RouteCoordinates(0, 0)
        val dest = RouteCoordinates(1, 0)
        val route = pathFinder.findPath(
            level = src.level,
            srcX = src.x,
            srcZ = src.z,
            destX = dest.x,
            destZ = dest.z
        )
        Assertions.assertEquals(1, route.size)
        Assertions.assertEquals(dest.x, route.last().x)
        Assertions.assertEquals(dest.z, route.last().z)
    }

    @Test
    fun `fail occupied tile`() {
        val src = RouteCoordinates(0, 0)
        val dest = RouteCoordinates(1, 0)
        flags[dest.x, dest.z, dest.level] = CollisionFlag.FLOOR
        val route = pathFinder.findPath(
            level = src.level,
            srcX = src.x,
            srcZ = src.z,
            destX = dest.x,
            destZ = dest.z
        )
        Assertions.assertTrue(route.failed)
        Assertions.assertTrue(route.isEmpty())
    }

    @ParameterizedTest
    @ArgumentsSource(DirectionProvider::class)
    internal fun `reach directional dest path`(dir: Direction) {
        val src = RouteCoordinates(3200, 3200)
        val dest = RouteCoordinates(src.x + dir.offX, src.z + dir.offZ)
        val route = pathFinder.findPath(
            level = src.level,
            srcX = src.x,
            srcZ = src.z,
            destX = dest.x,
            destZ = dest.z
        )
        Assertions.assertTrue(route.isNotEmpty())
        Assertions.assertEquals(dest.x, route.last().x)
        Assertions.assertEquals(dest.z, route.last().z)
    }

    @ParameterizedTest
    @ArgumentsSource(DirectionProvider::class)
    internal fun `fail blocked direction path`(dir: Direction) {
        val src = RouteCoordinates(3200, 3200)
        val dest = RouteCoordinates(src.x + dir.offX, src.z + dir.offZ)
        flags[dest.x, dest.z, dest.level] = CollisionFlag.OBJECT
        val route = pathFinder.findPath(
            level = src.level,
            srcX = src.x,
            srcZ = src.z,
            destX = dest.x,
            destZ = dest.z
        )
        Assertions.assertTrue(route.isEmpty())
    }

    @ParameterizedTest
    @ArgumentsSource(DimensionParameterProvider::class)
    fun `reach rect objects`(width: Int, height: Int) {
        val src = RouteCoordinates(0, 0)
        val dest = RouteCoordinates(3 + width, 0) /* ensure destination is further than width */
        /* mark tiles with object */
        for (z in 0 until height) {
            for (x in 0 until width) {
                flags[dest.x + x, dest.z + z, dest.level] = CollisionFlag.OBJECT
            }
        }
        val route = pathFinder.findPath(
            level = src.level,
            srcX = src.x,
            srcZ = src.z,
            destX = dest.x,
            destZ = dest.z,
            objShape = 10,
            destWidth = width,
            destHeight = height
        )
        Assertions.assertTrue(route.success)
        Assertions.assertFalse(route.alternative)
    }

    @ParameterizedTest
    @ArgumentsSource(ParameterFileNameProvider::class)
    fun `match destination from resource file args`(resourceFile: String) {
        val params = loadParameters(resourceFile)
        val flags = params.toCollisionFlags()
        val pathFinder = PathFinder(flags)
        val route = pathFinder.findPath(
            level = params.level,
            srcX = params.srcX,
            srcZ = params.srcZ,
            destX = params.destX,
            destZ = params.destZ
        )
        Assertions.assertEquals(params.expectedX, route.last().x)
        Assertions.assertEquals(params.expectedZ, route.last().z)
    }

    private fun loadParameters(resourceFile: String): PathParameter {
        val mapper = ObjectMapper(JsonFactory())
        val input = Route::class.java.getResourceAsStream(resourceFile)
        return input.use { mapper.readValue(it, PathParameter::class.java) }
    }

    private object DirectionProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(Direction.North),
                Arguments.of(Direction.South),
                Arguments.of(Direction.East),
                Arguments.of(Direction.West)
            )
        }
    }

    private object DimensionParameterProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(1, 1),
                Arguments.of(2, 2),
                Arguments.of(3, 3),
                Arguments.of(1, 2),
                Arguments.of(2, 1)
            )
        }
    }

    private object ParameterFileNameProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of("lumbridge.json"),
                Arguments.of("barb-village.json"),
                Arguments.of("gnome-maze.json") /* stops after 24 turns */
            )
        }
    }

    private data class PathParameter(
        val level: Int,
        val srcX: Int,
        val srcZ: Int,
        val destX: Int,
        val destZ: Int,
        val expectedX: Int,
        val expectedZ: Int,
        val flags: IntArray
    ) {

        constructor() : this(0, 0, 0, 0, 0, 0, 0, intArrayOf())

        fun toCollisionFlags(): CollisionFlagMap {
            val collisionFlags = CollisionFlagMap()
            val mapSearchSize = sqrt(flags.size.toDouble()).toInt()
            val half = mapSearchSize / 2
            val centerX = srcX
            val centerZ = srcZ
            val rangeX = centerX - half until centerX + half
            val rangeZ = centerZ - half until centerZ + half
            for (z in rangeZ) {
                for (x in rangeX) {
                    val lx = x - (centerX - half)
                    val lz = z - (centerZ - half)
                    val index = (lz * mapSearchSize) + lx
                    collisionFlags[x, z, level] = flags[index]
                }
            }
            return collisionFlags
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PathParameter

            if (level != other.level) return false
            if (srcX != other.srcX) return false
            if (srcZ != other.srcZ) return false
            if (destX != other.destX) return false
            if (destZ != other.destZ) return false
            if (expectedX != other.expectedX) return false
            if (expectedZ != other.expectedZ) return false
            if (!flags.contentEquals(other.flags)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = level
            result = 31 * result + srcX
            result = 31 * result + srcZ
            result = 31 * result + destX
            result = 31 * result + destZ
            result = 31 * result + expectedX
            result = 31 * result + expectedZ
            result = 31 * result + flags.contentHashCode()
            return result
        }
    }
}
