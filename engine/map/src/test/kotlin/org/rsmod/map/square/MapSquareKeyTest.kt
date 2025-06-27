package org.rsmod.map.square

import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey.Companion.X_BIT_MASK
import org.rsmod.map.square.MapSquareKey.Companion.Z_BIT_MASK

class MapSquareKeyTest {
    @Test
    fun `construct every map square key`() {
        for (z in 0..Z_BIT_MASK) {
            for (x in 0..X_BIT_MASK) {
                val key = MapSquareKey(x, z)
                assertEquals(x, key.x)
                assertEquals(z, key.z)
            }
        }
    }

    @Test
    fun `deconstruct every map square key`() {
        for (z in 0..Z_BIT_MASK) {
            for (x in 0..X_BIT_MASK) {
                val key = MapSquareKey(x, z)
                val (c1, c2) = key
                assertEquals(x, c1)
                assertEquals(z, c2)
            }
        }
    }

    @Test
    fun `fail to construct on out of bounds map square key`() {
        assertThrows<IllegalArgumentException> { MapSquareKey(X_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { MapSquareKey(0, Z_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { MapSquareKey(-1, 0) }
        assertThrows<IllegalArgumentException> { MapSquareKey(0, -1) }
    }

    @ParameterizedTest
    @ArgumentsSource(MapSquareRelativeCoordGridProvider::class)
    fun `construct from map square id`(
        mapSquareId: Int,
        expectedMapSquareX: Int,
        expectedMapSquareZ: Int,
    ) {
        val mapSquare = MapSquareKey(mapSquareId)
        assertEquals(expectedMapSquareX, mapSquare.x)
        assertEquals(expectedMapSquareZ, mapSquare.z)
    }

    @ParameterizedTest
    @ArgumentsSource(MapSquareAbsoluteCoordGridProvider::class)
    fun `construct from coord grid`(
        expectedMapSquareId: Int,
        absoluteCoordsX: Int,
        absoluteCoordsZ: Int,
    ) {
        val mapSquare = MapSquareKey.from(CoordGrid(absoluteCoordsX, absoluteCoordsZ))
        assertEquals(expectedMapSquareId, mapSquare.id)
    }

    @ParameterizedTest
    @ArgumentsSource(MapSquareAbsoluteCoordGridProvider::class)
    fun `convert into coord grid`(mapSquareId: Int, expectedCoordsX: Int, expectedCoordsZ: Int) {
        val mapSquare = MapSquareKey(mapSquareId)
        for (level in 0 until CoordGrid.LEVEL_COUNT) {
            val coords = mapSquare.toCoords(level)
            assertEquals(expectedCoordsX, coords.x)
            assertEquals(expectedCoordsZ, coords.z)
            assertEquals(level, coords.level)
        }
    }

    private object MapSquareRelativeCoordGridProvider : ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext,
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(LUMBRIDGE_MAP_SQUARE, 50, 50),
                Arguments.of(EDGEVILLE_MAP_SQUARE, 48, 54),
                Arguments.of(PEST_CONTROL_MAP_SQUARE, 41, 40),
            )
        }
    }

    private object MapSquareAbsoluteCoordGridProvider : ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext,
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(LUMBRIDGE_MAP_SQUARE, 3200, 3200),
                Arguments.of(EDGEVILLE_MAP_SQUARE, 3072, 3456),
                Arguments.of(PEST_CONTROL_MAP_SQUARE, 2624, 2560),
            )
        }
    }

    private companion object {
        private const val LUMBRIDGE_MAP_SQUARE = 12850
        private const val EDGEVILLE_MAP_SQUARE = 12342
        private const val PEST_CONTROL_MAP_SQUARE = 10536
    }
}
