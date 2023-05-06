package org.rsmod.game.pathfinder.reach

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.rsmod.game.pathfinder.Direction
import org.rsmod.game.pathfinder.flag.DirectionFlag
import java.util.stream.Stream

object BlockAccessFlagProvider : ArgumentsProvider {

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(Direction.North, DirectionFlag.NORTH),
            Arguments.of(Direction.East, DirectionFlag.EAST),
            Arguments.of(Direction.South, DirectionFlag.SOUTH),
            Arguments.of(Direction.West, DirectionFlag.WEST)
        )
    }
}
