package org.rsmod.plugins.info.model.coord

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class LowResCoordTest {

    @Test
    fun testArithmeticOperations() {
        val lhs = LowResCoord(1, 1, 1)
        val rhs = LowResCoord(0, 1, 0)
        require(lhs.x == 1 && lhs.y == 1 && lhs.level == 1)
        require(rhs.x == 0 && rhs.y == 1 && rhs.level == 0)

        val sum = lhs + rhs
        Assertions.assertEquals(lhs.x + rhs.x, sum.x)
        Assertions.assertEquals(lhs.y + rhs.y, sum.y)
        Assertions.assertEquals(lhs.level + rhs.level, sum.level)

        val diff = lhs - rhs
        Assertions.assertEquals(lhs.x - rhs.x, diff.x)
        Assertions.assertEquals(lhs.y - rhs.y, diff.y)
        Assertions.assertEquals((lhs.level - rhs.level) and 0x3, diff.level)
    }

    @ParameterizedTest
    @ArgumentsSource(CoordinateProvider::class)
    fun testConstructCoord(x: Int, y: Int, vararg levels: Int) {
        levels.forEach { level ->
            val coord = LowResCoord(x, y, level)
            Assertions.assertEquals(x, coord.x)
            Assertions.assertEquals(y, coord.y)
            Assertions.assertEquals(level, coord.level)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(CoordinateProvider::class)
    fun testDeconstructCoord(x: Int, y: Int, vararg levels: Int) {
        levels.forEach { level ->
            val coord = LowResCoord(x, y, level)
            check(coord.x == x)
            check(coord.y == y)
            check(coord.level == level)

            val (cx, cy, clevel) = coord
            Assertions.assertEquals(x, cx)
            Assertions.assertEquals(y, cy)
            Assertions.assertEquals(level, clevel)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ConversionProvider::class)
    fun testHighResCoordConversion(
        lowX: Int,
        lowY: Int,
        expectedHighX: Int,
        expectedHighY: Int,
        vararg levels: Int
    ) {
        levels.forEach { level ->
            val lowRes = LowResCoord(lowX, lowY, level)
            val conversion = lowRes.toHighRes()
            Assertions.assertEquals(expectedHighX, conversion.x)
            Assertions.assertEquals(expectedHighY, conversion.y)
            Assertions.assertEquals(level, conversion.level)
        }
    }

    private object CoordinateProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(1, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(0, 1, intArrayOf(0, 1, 2, 3)),
                Arguments.of(1, 1, intArrayOf(0, 1, 2, 3))
            )
        }
    }

    private object ConversionProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(0, 0, 0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(1, 0, 8192, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(0, 1, 0, 8192, intArrayOf(0, 1, 2, 3)),
                Arguments.of(1, 1, 8192, 8192, intArrayOf(0, 1, 2, 3))
            )
        }
    }
}
