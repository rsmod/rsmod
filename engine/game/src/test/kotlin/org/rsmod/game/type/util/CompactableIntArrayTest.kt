package org.rsmod.game.type.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.type.util.CompactableIntArray.Companion.DEFAULT_CAPACITY

class CompactableIntArrayTest {
    @Test
    fun `construct with default capacity`() {
        val array = CompactableIntArray()
        assertEquals(DEFAULT_CAPACITY, array.capacity)
    }

    @Test
    fun `construct with set capacity`() {
        val array = CompactableIntArray(capacity = 50)
        assertEquals(50, array.capacity)
    }

    @Test
    fun `construct with varargs`() {
        val array = CompactableIntArray(1, 2, 3, 4, 5)
        assertEquals(5, array.capacity)
        assertEquals(1, array[0])
        assertEquals(2, array[1])
        assertEquals(3, array[2])
        assertEquals(4, array[3])
        assertEquals(5, array[4])
    }

    @Test
    fun `get at index`() {
        val array = CompactableIntArray(1, 2, 3, 4, 5)
        assertEquals(2, array[1])
    }

    @Test
    fun `set at index`() {
        val array = CompactableIntArray(1, 2, 3, 4, 5)
        assertEquals(2, array[1])
        array[1] = 6
        assertEquals(6, array[1])
    }

    @Test
    fun `set at index out of bounds`() {
        val array = CompactableIntArray(1, 2, 3, 4, 5)
        assertThrows<IndexOutOfBoundsException> { array[5] = 6 }
    }

    @Test
    fun `compact on toIntArray`() {
        val array = CompactableIntArray(arrayOf(1, 2, 3, 4, 5, null, null))
        val compact = array.toIntArray()
        assertEquals(7, array.capacity)
        assertEquals(1, compact[0])
        assertEquals(2, compact[1])
        assertEquals(3, compact[2])
        assertEquals(4, compact[3])
        assertEquals(5, compact[4])
        assertEquals(5, compact.size)
    }

    @Test
    fun `compact on toShortArray`() {
        val array = CompactableIntArray(arrayOf(1, 2, 3, 4, 5, null, null))
        val compact = array.toShortArray()
        assertEquals(7, array.capacity)
        assertEquals(1, compact[0])
        assertEquals(2, compact[1])
        assertEquals(3, compact[2])
        assertEquals(4, compact[3])
        assertEquals(5, compact[4])
        assertEquals(5, compact.size)
    }

    @Test
    fun `compact on toByteArray`() {
        val array = CompactableIntArray(arrayOf(1, 2, 3, 4, 5, null, null))
        val compact = array.toByteArray()
        assertEquals(7, array.capacity)
        assertEquals(1, compact[0])
        assertEquals(2, compact[1])
        assertEquals(3, compact[2])
        assertEquals(4, compact[3])
        assertEquals(5, compact[4])
        assertEquals(5, compact.size)
    }

    @Test
    fun `throw error on gaps`() {
        val array = CompactableIntArray(arrayOf(1, 2, 3, 4, 5, null, null, 8, 9, 10))
        assertThrows<IllegalStateException> { array.toIntArray() }
    }

    @Test
    fun `iterate over elements in order`() {
        val array = CompactableIntArray(1, 2, 3, 4, 5)
        val result = mutableListOf<Int?>()
        for (element in array) {
            result += element
        }
        assertEquals(listOf(1, 2, 3, 4, 5), result)
    }
}
