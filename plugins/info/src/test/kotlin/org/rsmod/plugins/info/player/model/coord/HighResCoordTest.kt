package org.rsmod.plugins.info.player.model.coord

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class HighResCoordTest {

    @Test
    fun testArithmeticOperations() {
        val lhs = HighResCoord(3200, 2000, 0)
        val rhs = HighResCoord(1800, 1600, 1)
        require(lhs.x == 3200 && lhs.z == 2000 && lhs.level == 0)
        require(rhs.x == 1800 && rhs.z == 1600 && rhs.level == 1)

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
            val coord = HighResCoord(x, z, level)
            Assertions.assertEquals(x, coord.x)
            Assertions.assertEquals(z, coord.z)
            Assertions.assertEquals(level, coord.level)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(CoordinateProvider::class)
    fun testDeconstructCoord(x: Int, z: Int, vararg levels: Int) {
        levels.forEach { level ->
            val coord = HighResCoord(x, z, level)
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
    fun testLowResCoordConversion(
        highX: Int,
        highZ: Int,
        expectedLowX: Int,
        expectedLowZ: Int,
        vararg levels: Int
    ) {
        levels.forEach { level ->
            val highRes = HighResCoord(highX, highZ, level)
            val conversion = highRes.toLowRes()
            Assertions.assertEquals(expectedLowX, conversion.x)
            Assertions.assertEquals(expectedLowZ, conversion.z)
            Assertions.assertEquals(level, conversion.level)
        }
    }

    private object CoordinateProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(256, 128, intArrayOf(0, 1, 2, 3)),
                Arguments.of(512, 384, intArrayOf(0, 1, 2, 3)),
                Arguments.of(1024, 640, intArrayOf(0, 1, 2, 3)),
                Arguments.of(2048, 888, intArrayOf(0, 1, 2, 3)),
                Arguments.of(3200, 3200, intArrayOf(0, 1, 2, 3)),
                Arguments.of(8192, 8200, intArrayOf(0, 1, 2, 3)),
                Arguments.of(16383, 8192, intArrayOf(0, 1, 2, 3))
            )
        }
    }

    private object ConversionProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(0, 0, 0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(3200, 0, 0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(0, 3200, 0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(3200, 3200, 0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(8191, 3200, 0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(3200, 8191, 0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(8191, 8191, 0, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(8192, 3200, 1, 0, intArrayOf(0, 1, 2, 3)),
                Arguments.of(3200, 8192, 0, 1, intArrayOf(0, 1, 2, 3)),
                Arguments.of(8192, 8192, 1, 1, intArrayOf(0, 1, 2, 3)),
                Arguments.of(9600, 10200, 1, 1, intArrayOf(0, 1, 2, 3))
            )
        }
    }
}
