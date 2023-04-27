package org.rsmod.game.pathfinder

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import java.util.stream.Stream
import kotlin.math.sqrt

class PathFinderSimulationTest {

    @ParameterizedTest
    @ArgumentsSource(ParameterFileNameProvider::class)
    fun testSimulatedRouteResults(resourceFile: String) {
        // TODO: improve thoroughness by including waypoint comparison between
        // expected path parameter waypoints and findPath results.
        val params = PathParameter.of(resourceFile)
        val map = params.toCollisionFlags()
        val pathFinder = PathFinder(map)
        with(pathFinder) {
            val route = findPath(params.level, params.srcX, params.srcZ, params.destX, params.destZ)
            assertEquals(params.expectedX, route.last().x)
            assertEquals(params.expectedZ, route.last().z)
        }
    }

    private object ParameterFileNameProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of("lumbridge.json"),
                Arguments.of("barb-village.json"),
                Arguments.of("gnome-maze.json")
            )
        }
    }

    private data class PathParameter(
        val level: Int = 0,
        val srcX: Int = 0,
        val srcZ: Int = 0,
        val destX: Int = 0,
        val destZ: Int = 0,
        val expectedX: Int = 0,
        val expectedZ: Int = 0,
        val flags: List<Int> = emptyList()
    ) {

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

        companion object {

            fun of(resourceFile: String): PathParameter {
                val mapper = ObjectMapper(JsonFactory())
                val input = PathFinderTest::class.java.getResourceAsStream(resourceFile)
                return input.use { mapper.readValue(it, PathParameter::class.java) }
            }
        }
    }
}
