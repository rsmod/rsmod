package org.rsmod.game.model.map

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class CoordinatesTest {

    @ParameterizedTest
    @ArgumentsSource(CoordinateProvider::class)
    fun `create coordinates within bounds`(x: Int, y: Int, level: Int) {
        val coords = Coordinates(x, y, level)
        Assertions.assertEquals(x, coords.x)
        Assertions.assertEquals(y, coords.y)
        Assertions.assertEquals(level, coords.level)
    }

    @ParameterizedTest
    @ArgumentsSource(TranslationProvider::class)
    fun `translate coordinates within bounds`(x: Int, y: Int, level: Int, offX: Int, offY: Int, offLevel: Int) {
        val coords = Coordinates(x, y, level)
        val translation = coords.translate(offX, offY, offLevel)
        Assertions.assertEquals(coords.x + offX, translation.x)
        Assertions.assertEquals(coords.y + offY, translation.y)
        Assertions.assertEquals(coords.level + offLevel, translation.level)
    }

    private object CoordinateProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(0, 0, 0),
                Arguments.of(0, 0, 1),
                Arguments.of(0, 0, 2),
                Arguments.of(0, 0, 3),
                Arguments.of(3200, 3200, 0),
                Arguments.of(3200, 3200, 1),
                Arguments.of(3200, 3200, 2),
                Arguments.of(3200, 3200, 3),
                Arguments.of(9600, 6400, 0),
                Arguments.of(9600, 6400, 1),
                Arguments.of(9600, 6400, 2),
                Arguments.of(9600, 6400, 3),
                Arguments.of(6400, 9600, 0),
                Arguments.of(6400, 9600, 1),
                Arguments.of(6400, 9600, 2),
                Arguments.of(6400, 9600, 3),
                Arguments.of(16383, 16383, 0),
                Arguments.of(16383, 16383, 1),
                Arguments.of(16383, 16383, 2),
                Arguments.of(16383, 16383, 3),
                Arguments.of(Coordinates.MAX_XY, 0, 0),
                Arguments.of(Coordinates.MAX_XY, 0, 1),
                Arguments.of(Coordinates.MAX_XY, 0, 2),
                Arguments.of(Coordinates.MAX_XY, 0, 3),
                Arguments.of(Coordinates.MAX_XY, Coordinates.MAX_XY, 0),
                Arguments.of(Coordinates.MAX_XY, Coordinates.MAX_XY, 1),
                Arguments.of(Coordinates.MAX_XY, Coordinates.MAX_XY, 2),
                Arguments.of(Coordinates.MAX_XY, Coordinates.MAX_XY, Coordinates.MAX_LEVEL)
            )
        }
    }

    private object TranslationProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(3200, 3200, 0, 0, 0, 0),
                Arguments.of(3200, 3200, 0, 1, 0, 0),
                Arguments.of(3200, 3200, 0, 2, 0, 0),
                Arguments.of(3200, 3200, 0, 0, 1, 0),
                Arguments.of(3200, 3200, 0, 0, 2, 0),
                Arguments.of(3200, 3200, 0, 0, 0, 1),
                Arguments.of(3200, 3200, 0, 0, 0, 2),
                Arguments.of(3200, 3200, 0, 1, 1, 1),
                Arguments.of(3200, 3200, 0, 1, 2, 1),
                Arguments.of(3200, 3200, 0, 1, 3, 1),
                Arguments.of(3200, 3200, 0, 2, 1, 1),
                Arguments.of(3200, 3200, 0, 3, 1, 1),
                Arguments.of(3200, 3200, 0, 1, 1, 2),
                Arguments.of(3200, 3200, 0, 1, 1, 3)
            )
        }
    }
}
