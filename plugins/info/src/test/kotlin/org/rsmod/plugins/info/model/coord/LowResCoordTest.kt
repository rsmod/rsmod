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
        require(lhs.x == 1 && lhs.z == 1 && lhs.level == 1)
        require(rhs.x == 0 && rhs.z == 1 && rhs.level == 0)

        val sum = lhs + rhs
        Assertions.assertEquals(lhs.x + rhs.x, sum.x)
        Assertions.assertEquals(lhs.z + rhs.z, sum.z)
        Assertions.assertEquals(lhs.level + rhs.level, sum.level)

        val diff = lhs - rhs
        Assertions.assertEquals(lhs.x - rhs.x, diff.x)
        Assertions.assertEquals(lhs.z - rhs.z, diff.z)
        Assertions.assertEquals((lhs.level - rhs.level) and 0x3, diff.level)
    }

    @ParameterizedTest
    @ArgumentsSource(CoordinateProvider::class)
    fun testConstructCoord(x: Int, z: Int, vararg levels: Int) {
        levels.forEach { level ->
            val coord = LowResCoord(x, z, level)
            Assertions.assertEquals(x, coord.x)
            Assertions.assertEquals(z, coord.z)
            Assertions.assertEquals(level, coord.level)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(CoordinateProvider::class)
    fun testDeconstructCoord(x: Int, z: Int, vararg levels: Int) {
        levels.forEach { level ->
            val coord = LowResCoord(x, z, level)
            check(coord.x == x)
            check(coord.z == z)
            check(coord.level == level)

            val (cx, cz, clevel) = coord
            Assertions.assertEquals(x, cx)
            Assertions.assertEquals(z, cz)
            Assertions.assertEquals(level, clevel)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ConversionProvider::class)
    fun testHighResCoordConversion(
        lowX: Int,
        lowZ: Int,
        expectedHighX: Int,
        expectedHighZ: Int,
        vararg levels: Int
    ) {
        levels.forEach { level ->
            val lowRes = LowResCoord(lowX, lowZ, level)
            val conversion = lowRes.toHighRes()
            Assertions.assertEquals(expectedHighX, conversion.x)
            Assertions.assertEquals(expectedHighZ, conversion.z)
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
