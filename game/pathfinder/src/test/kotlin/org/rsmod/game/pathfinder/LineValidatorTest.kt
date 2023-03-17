package org.rsmod.game.pathfinder

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

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LineValidatorTest {

    private val flags = CollisionFlagMap()
    private val validator = LineValidator(flags)

    @Test
    fun `validate empty path`() {
        val src = RouteCoordinates(3200, 3200)
        val dest = RouteCoordinates(src.x + 3, src.z)
        flags.allocateIfAbsent(src.x, src.z, src.level)
        val validPath = validator.hasLineOfSight(
            level = src.level,
            srcX = src.x,
            srcZ = src.z,
            destX = dest.x,
            destZ = dest.z
        )
        Assertions.assertTrue(validPath)
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidDirectionPath::class)
    internal fun `invalidate blocked path`(dir: Direction, flag: Int) {
        val src = RouteCoordinates(3200, 3200)
        val dest = RouteCoordinates(src.x + (dir.offX * 6), src.z + (dir.offZ * 6))
        flags[src.x + dir.offX, src.z + dir.offZ, src.level] = flag
        val validPath = validator.hasLineOfSight(
            level = src.level,
            srcX = src.x,
            srcZ = src.z,
            destX = dest.x,
            destZ = dest.z
        )
        Assertions.assertFalse(validPath)
    }

    private object InvalidDirectionPath : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments>? {
            return Stream.of(
                Arguments.of(
                    Direction.North,
                    CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER
                ),
                Arguments.of(
                    Direction.South,
                    CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER
                ),
                Arguments.of(
                    Direction.East,
                    CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER
                ),
                Arguments.of(
                    Direction.West,
                    CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER
                )
            )
        }
    }
}
