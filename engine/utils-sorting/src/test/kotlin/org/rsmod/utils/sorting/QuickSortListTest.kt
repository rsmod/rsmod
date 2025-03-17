package org.rsmod.utils.sorting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QuickSortListTest {
    @Test
    fun `alternating sort`() {
        val list = mutableListOf(5, 3, 8, 1, 2)
        QuickSort.alternating(list) { a, b -> a - b }
        assertEquals(listOf(1, 2, 3, 5, 8), list)
    }

    @Test
    fun `alternating sort with duplicates`() {
        val list = mutableListOf(5, 3, 8, 1, 2, 5, 3)
        QuickSort.alternating(list) { a, b -> a - b }
        assertEquals(listOf(1, 2, 3, 3, 5, 5, 8), list)
    }

    @Test
    fun `alternating sort with single element`() {
        val list = mutableListOf(42)
        QuickSort.alternating(list) { a, b -> a - b }
        assertEquals(listOf(42), list)
    }

    @Test
    fun `alternating sort with no elements`() {
        val list = mutableListOf<Int>()
        QuickSort.alternating(list) { a, b -> a - b }
        assertEquals(emptyList<Int>(), list)
    }

    @Test
    fun `lessThanZero sort`() {
        val list = mutableListOf(5, 3, 8, 1, 2)
        QuickSort.lessThanZero(list) { a, b -> a - b }
        assertEquals(listOf(1, 2, 3, 5, 8), list)
    }

    @Test
    fun `lessThanZero sort with duplicates`() {
        val list = mutableListOf(5, 3, 8, 1, 2, 5, 3)
        QuickSort.lessThanZero(list) { a, b -> a - b }
        assertEquals(listOf(1, 2, 3, 3, 5, 5, 8), list)
    }

    @Test
    fun `lessThanZero sort with single element`() {
        val list = mutableListOf(42)
        QuickSort.lessThanZero(list) { a, b -> a - b }
        assertEquals(listOf(42), list)
    }

    @Test
    fun `lessThanZero sort with no elements`() {
        val list = mutableListOf<Int>()
        QuickSort.lessThanZero(list) { a, b -> a - b }
        assertEquals(emptyList<Int>(), list)
    }

    @Test
    fun `lessThanOne sort`() {
        val list = mutableListOf(5, 3, 8, 1, 2)
        QuickSort.lessThanOne(list) { a, b -> a - b }
        assertEquals(listOf(1, 2, 3, 5, 8), list)
    }

    @Test
    fun `lessThanOne sort with duplicates`() {
        val list = mutableListOf(5, 3, 8, 1, 2, 5, 3)
        QuickSort.lessThanOne(list) { a, b -> a - b }
        assertEquals(listOf(1, 2, 3, 3, 5, 5, 8), list)
    }

    @Test
    fun `lessThanOne sort with single element`() {
        val list = mutableListOf(42)
        QuickSort.lessThanOne(list) { a, b -> a - b }
        assertEquals(listOf(42), list)
    }

    @Test
    fun `lessThanOne sort with no elements`() {
        val list = mutableListOf<Int>()
        QuickSort.lessThanOne(list) { a, b -> a - b }
        assertEquals(emptyList<Int>(), list)
    }

    @Test
    fun `alternating sort with length comparator`() {
        val list = mutableListOf("apple", "banana", "cherry", "date")
        QuickSort.alternating(list) { a, b -> a.length - b.length }
        assertEquals(listOf("date", "apple", "banana", "cherry"), list)
    }

    @Test
    fun `lessThanZero sort with length comparator`() {
        val list = mutableListOf("apple", "banana", "cherry", "date")
        QuickSort.lessThanZero(list) { a, b -> a.length - b.length }
        assertEquals(listOf("date", "apple", "banana", "cherry"), list)
    }

    @Test
    fun `lessThanOne sort with length comparator`() {
        val list = mutableListOf("apple", "banana", "cherry", "date")
        QuickSort.lessThanOne(list) { a, b -> a.length - b.length }
        assertEquals(listOf("date", "apple", "cherry", "banana"), list)
    }
}
