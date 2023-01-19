package org.rsmod.game.pathfinder.collision

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CollisionFlagMapTest {

    private lateinit var data: Array<IntArray?>
    private lateinit var collisions: CollisionFlagMap

    @BeforeEach
    fun setup() {
        data = arrayOfNulls(2048 * 2048 * 4)
        collisions = CollisionFlagMap(data)
    }

    @Test
    fun `Append mask`() {
        // Given
        val mask = 0x8
        collisions[3001, 3201, 3] = 0x4
        // When
        collisions.add(3001, 3201, 3, mask)
        // Then
        assertEquals(0xC, 3001, 3201, 3)
    }

    @Test
    fun `Add mask`() {
        // Given
        val mask = 0x8
        // When
        collisions.add(2, 0, 3, mask)
        // Then
        assertEquals(mask, 2, 0, 3)
        assertEquals(0, 2050, 0, 3)
        assertEquals(0, 10, 0, 3)
    }

    @Test
    fun `Set mask`() {
        // Given
        collisions[1, 2, 3] = 0x4
        val mask = 0x8
        // When
        collisions[1, 2, 3] = mask
        // Then
        assertEquals(mask, 1, 2, 3)
    }

    @Test
    fun `Remove mask`() {
        // Given
        collisions[1, 2, 3] = 0x4
        // When
        collisions.remove(1, 2, 3, 0x4)
        // Then
        assertEquals(0, 1, 2, 3)
    }

    @Test
    fun `Reduce mask`() {
        // Given
        collisions[1, 2, 3] = 0xC
        // When
        collisions.remove(1, 2, 3, 0x4)
        // Then
        assertEquals(0x8, 1, 2, 3)
    }

    @Test
    fun `Get empty mask`() {
        // When
        val result = collisions[1, 2, 3]
        // Then
        assertEquals(0, result)
    }

    private fun assertEquals(expected: Int, x: Int, y: Int, level: Int) {
        assertEquals(expected, collisions[x, y, level]) { "x=$x, y=$y, level=$level" }
    }
}
