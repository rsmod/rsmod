package org.rsmod.routefinder

import java.util.stream.Stream
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider

object RotatedLocProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(3203, 3203, Dimension(1, 1)),
            Arguments.of(3203, 3203, Dimension(1, 2)),
            Arguments.of(3203, 3203, Dimension(1, 3)),
            Arguments.of(3203, 3203, Dimension(2, 1)),
            Arguments.of(3203, 3203, Dimension(2, 2)),
            Arguments.of(3203, 3203, Dimension(2, 3)),
            Arguments.of(3203, 3203, Dimension(3, 1)),
            Arguments.of(3203, 3203, Dimension(3, 2)),
            Arguments.of(3203, 3203, Dimension(3, 3)),
        )
    }
}
