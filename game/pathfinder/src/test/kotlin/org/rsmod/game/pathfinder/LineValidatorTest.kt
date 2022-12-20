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
        val dest = RouteCoordinates(src.x + 3, src.y)
        val validPath = validator.hasLineOfSight(
            level = src.level,
            srcX = src.x,
            srcY = src.y,
            destX = dest.x,
            destY = dest.y
        )
        Assertions.assertTrue(validPath)
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidDirectionPath::class)
    internal fun `invalidate blocked path`(dir: Direction, flag: Int) {
        val src = RouteCoordinates(3200, 3200)
        val dest = RouteCoordinates(src.x + (dir.offX * 6), src.y + (dir.offY * 6))
        flags[src.x + dir.offX, src.y + dir.offY, src.level] = flag
        val validPath = validator.hasLineOfSight(
            level = src.level,
            srcX = src.x,
            srcY = src.y,
            destX = dest.x,
            destY = dest.y
        )
        Assertions.assertFalse(validPath)
    }

    private object InvalidDirectionPath : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments>? {
            return Stream.of(
                Arguments.of(
                    North,
                    CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER
                ),
                Arguments.of(
                    South,
                    CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER
                ),
                Arguments.of(
                    East,
                    CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER
                ),
                Arguments.of(
                    West,
                    CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER
                )
            )
        }
    }
}
