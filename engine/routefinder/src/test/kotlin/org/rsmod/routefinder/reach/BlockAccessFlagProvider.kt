package org.rsmod.routefinder.reach

import java.util.stream.Stream
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.ParameterDeclarations
import org.rsmod.routefinder.Direction
import org.rsmod.routefinder.flag.DirectionFlag

object BlockAccessFlagProvider : ArgumentsProvider {
    override fun provideArguments(
        parameters: ParameterDeclarations,
        context: ExtensionContext,
    ): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(Direction.North, DirectionFlag.NORTH),
            Arguments.of(Direction.East, DirectionFlag.EAST),
            Arguments.of(Direction.South, DirectionFlag.SOUTH),
            Arguments.of(Direction.West, DirectionFlag.WEST),
        )
    }
}
