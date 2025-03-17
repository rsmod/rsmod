package org.rsmod.utils.sorting

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

class QuickSortArrayTest {
    @Test
    fun `alternating sort`() {
        val elements = arrayOf(5, 3, 8, 1, 2)
        QuickSort.alternating(elements) { a, b -> a - b }
        assertArrayEquals(arrayOf(1, 2, 3, 5, 8), elements)
    }

    @Test
    fun `alternating sort with duplicates`() {
        val elements = arrayOf(5, 3, 8, 1, 2, 5, 3)
        QuickSort.alternating(elements) { a, b -> a - b }
        assertArrayEquals(arrayOf(1, 2, 3, 3, 5, 5, 8), elements)
    }

    @Test
    fun `alternating sort with single element`() {
        val elements = arrayOf(42)
        QuickSort.alternating(elements) { a, b -> a - b }
        assertArrayEquals(arrayOf(42), elements)
    }

    @Test
    fun `alternating sort with no elements`() {
        val elements = arrayOf<Int>()
        QuickSort.alternating(elements) { a, b -> a - b }
        assertArrayEquals(emptyArray<Int>(), elements)
    }

    @Test
    fun `lessThanZero sort`() {
        val elements = arrayOf(5, 3, 8, 1, 2)
        QuickSort.lessThanZero(elements) { a, b -> a - b }
        assertArrayEquals(arrayOf(1, 2, 3, 5, 8), elements)
    }

    @Test
    fun `lessThanZero sort with duplicates`() {
        val elements = arrayOf(5, 3, 8, 1, 2, 5, 3)
        QuickSort.lessThanZero(elements) { a, b -> a - b }
        assertArrayEquals(arrayOf(1, 2, 3, 3, 5, 5, 8), elements)
    }

    @Test
    fun `lessThanZero sort with single element`() {
        val elements = arrayOf(42)
        QuickSort.lessThanZero(elements) { a, b -> a - b }
        assertArrayEquals(arrayOf(42), elements)
    }

    @Test
    fun `lessThanZero sort with no elements`() {
        val elements = arrayOf<Int>()
        QuickSort.lessThanZero(elements) { a, b -> a - b }
        assertArrayEquals(emptyArray<Int>(), elements)
    }

    @Test
    fun `lessThanOne sort`() {
        val elements = arrayOf(5, 3, 8, 1, 2)
        QuickSort.lessThanOne(elements) { a, b -> a - b }
        assertArrayEquals(arrayOf(1, 2, 3, 5, 8), elements)
    }

    @Test
    fun `lessThanOne sort with duplicates`() {
        val elements = arrayOf(5, 3, 8, 1, 2, 5, 3)
        QuickSort.lessThanOne(elements) { a, b -> a - b }
        assertArrayEquals(arrayOf(1, 2, 3, 3, 5, 5, 8), elements)
    }

    @Test
    fun `lessThanOne sort with single element`() {
        val elements = arrayOf(42)
        QuickSort.lessThanOne(elements) { a, b -> a - b }
        assertArrayEquals(arrayOf(42), elements)
    }

    @Test
    fun `lessThanOne sort with no elements`() {
        val elements = arrayOf<Int>()
        QuickSort.lessThanOne(elements) { a, b -> a - b }
        assertArrayEquals(emptyArray<Int>(), elements)
    }

    @Test
    fun `alternating sort with length comparator`() {
        val elements = arrayOf("apple", "banana", "cherry", "date")
        QuickSort.alternating(elements) { a, b -> a.length - b.length }
        assertArrayEquals(arrayOf("date", "apple", "banana", "cherry"), elements)
    }

    @Test
    fun `lessThanZero sort with length comparator`() {
        val elements = arrayOf("apple", "banana", "cherry", "date")
        QuickSort.lessThanZero(elements) { a, b -> a.length - b.length }
        assertArrayEquals(arrayOf("date", "apple", "banana", "cherry"), elements)
    }

    @Test
    fun `lessThanOne sort with length comparator`() {
        val elements = arrayOf("apple", "banana", "cherry", "date")
        QuickSort.lessThanOne(elements) { a, b -> a.length - b.length }
        assertArrayEquals(arrayOf("date", "apple", "cherry", "banana"), elements)
    }
}
