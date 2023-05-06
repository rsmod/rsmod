package org.rsmod.game.pathfinder

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

object DimensionProvider : ArgumentsProvider {

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(Dimension(1, 1)),
            Arguments.of(Dimension(1, 2)),
            Arguments.of(Dimension(1, 3)),
            Arguments.of(Dimension(2, 1)),
            Arguments.of(Dimension(2, 2)),
            Arguments.of(Dimension(2, 3)),
            Arguments.of(Dimension(3, 1)),
            Arguments.of(Dimension(3, 2)),
            Arguments.of(Dimension(3, 3))
        )
    }
}
