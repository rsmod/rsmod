package org.rsmod.game.pathfinder

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NPCS
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_PLAYERS
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class StepValidatorTest {

    private val flags = CollisionFlagMap()
    private val validator = StepValidator(flags)

    @ParameterizedTest
    @ArgumentsSource(DirectionProvider::class)
    internal fun `validate unblocked direction (size 1)`(dir: Direction) {
        val src = RouteCoordinates(3200, 3200)
        val valid = validator.canTravel(
            level = src.level,
            x = src.x,
            z = src.z,
            offsetX = dir.offX,
            offsetZ = dir.offZ,
            size = 1,
            extraFlag = 0
        )
        Assertions.assertTrue(valid)
    }

    @ParameterizedTest
    @ArgumentsSource(BlockedDirectionProvider::class)
    internal fun `invalidate blocked direction (size 1)`(dir: Direction, blockFlags: Int) {
        val src = RouteCoordinates(3200, 3200)
        val dest = RouteCoordinates(src.x + dir.offX, src.z + dir.offZ)
        flags[dest.x, dest.z, dest.level] = blockFlags
        val valid = validator.canTravel(
            level = src.level,
            x = src.x,
            z = src.z,
            offsetX = dir.offX,
            offsetZ = dir.offZ,
            size = 1,
            extraFlag = 0
        )
        Assertions.assertFalse(valid)
    }

    @ParameterizedTest
    @ArgumentsSource(BlockedCharacterProvider::class)
    internal fun `invalidate blocked character direction (size 1)`(dir: Direction, blockFlags: Int) {
        val src = RouteCoordinates(3200, 3200)
        val dest = RouteCoordinates(src.x + dir.offX, src.z + dir.offZ)
        flags[dest.x, dest.z, dest.level] = blockFlags
        val valid = validator.canTravel(
            level = src.level,
            x = src.x,
            z = src.z,
            offsetX = dir.offX,
            offsetZ = dir.offZ,
            size = 1,
            extraFlag = BLOCK_PLAYERS or BLOCK_NPCS
        )
        Assertions.assertFalse(valid)
    }

    private companion object {

        private object DirectionProvider : ArgumentsProvider {

            override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
                return Stream.of(
                    Arguments.of(Direction.South),
                    Arguments.of(Direction.North),
                    Arguments.of(Direction.West),
                    Arguments.of(Direction.East),
                    Arguments.of(Direction.SouthWest),
                    Arguments.of(Direction.NorthWest),
                    Arguments.of(Direction.SouthEast),
                    Arguments.of(Direction.NorthEast)
                )
            }
        }

        private object BlockedDirectionProvider : ArgumentsProvider {

            override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
                return Stream.of(
                    Arguments.of(Direction.South, CollisionFlag.BLOCK_SOUTH),
                    Arguments.of(Direction.North, CollisionFlag.BLOCK_NORTH),
                    Arguments.of(Direction.West, CollisionFlag.BLOCK_WEST),
                    Arguments.of(Direction.East, CollisionFlag.BLOCK_EAST),
                    Arguments.of(Direction.SouthWest, CollisionFlag.BLOCK_SOUTH_WEST),
                    Arguments.of(Direction.NorthWest, CollisionFlag.BLOCK_NORTH_WEST),
                    Arguments.of(Direction.SouthEast, CollisionFlag.BLOCK_SOUTH_EAST),
                    Arguments.of(Direction.NorthEast, CollisionFlag.BLOCK_NORTH_EAST)
                )
            }
        }

        private object BlockedCharacterProvider : ArgumentsProvider {

            override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
                return Stream.of(
                    Arguments.of(Direction.South, BLOCK_PLAYERS),
                    Arguments.of(Direction.North, BLOCK_NPCS),
                    Arguments.of(Direction.West, BLOCK_PLAYERS),
                    Arguments.of(Direction.East, BLOCK_NPCS),
                    Arguments.of(Direction.SouthWest, BLOCK_PLAYERS),
                    Arguments.of(Direction.NorthWest, BLOCK_NPCS),
                    Arguments.of(Direction.SouthEast, BLOCK_PLAYERS),
                    Arguments.of(Direction.NorthEast, BLOCK_NPCS)
                )
            }
        }
    }
}
