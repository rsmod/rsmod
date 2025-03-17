package org.rsmod.utils.sorting

/**
 * This object contains variations of the quicksort algorithm, each with a unique partitioning
 * strategy.
 */
public object QuickSort {
    /**
     * Sorts the given [list] using an `alternating` quicksort algorithm.
     *
     * This implementation partitions the list such that elements are compared in an alternating
     * fashion based on their index parity. Specifically, elements at even indices are treated
     * differently than those at odd indices when compared to the pivot.
     *
     * **Implementation details:**
     * - The pivot is initially swapped to the end to simplify partitioning. After partitioning, it
     *   is restored to its correct position.
     * - The condition checks whether an element's index is odd or even to determine how it is
     *   swapped during partitioning. Elements **less than the pivot** always move, elements
     *   **greater than the pivot** never move, and elements **equal to the pivot move only if they
     *   are at an odd index**.
     *
     * @param list The [MutableList] to sort. **This list will be mutated during sorting.**
     * @param low The inclusive start index of the range to sort.
     * @param high The inclusive end index of the range to sort.
     * @param compare A comparison function to determine the order of elements. Returns a negative
     *   integer, zero, or a positive integer if the first argument is less than, equal to, or
     *   greater than the second, respectively.
     */
    public fun <T> alternating(
        list: MutableList<T>,
        low: Int = 0,
        high: Int = list.size - 1,
        compare: (T, T) -> Int,
    ) {
        if (low >= high || list.isEmpty()) {
            return
        }
        val pivotIndex = (low + high) / 2
        val pivotValue = list[pivotIndex]

        list[pivotIndex] = list[high]
        list[high] = pivotValue

        var counter = low
        var loopIndex = low

        while (loopIndex < high) {
            if (compare(list[loopIndex], pivotValue) < (loopIndex and 0x1)) {
                val tmp = list[loopIndex]
                list[loopIndex] = list[counter]
                list[counter] = tmp
                counter++
            }
            loopIndex++
        }

        list[high] = list[counter]
        list[counter] = pivotValue

        if (low < counter - 1) {
            alternating(list, low, counter - 1, compare)
        }

        if (counter + 1 < high) {
            alternating(list, counter + 1, high, compare)
        }
    }

    /**
     * Sorts the given [arr] using an `alternating` quicksort algorithm.
     *
     * This implementation partitions the array such that elements are compared in an alternating
     * fashion based on their index parity. Specifically, elements at even indices are treated
     * differently than those at odd indices when compared to the pivot.
     *
     * **Implementation details:**
     * - The pivot is initially swapped to the end to simplify partitioning. After partitioning, it
     *   is restored to its correct position.
     * - The condition checks whether an element's index is odd or even to determine how it is
     *   swapped during partitioning. Elements **less than the pivot** always move, elements
     *   **greater than the pivot** never move, and elements **equal to the pivot move only if they
     *   are at an odd index**.
     *
     * @param arr The [Array] to sort. **This array will be mutated during sorting.**
     * @param low The inclusive start index of the range to sort.
     * @param high The inclusive end index of the range to sort.
     * @param compare A comparison function to determine the order of elements. Returns a negative
     *   integer, zero, or a positive integer if the first argument is less than, equal to, or
     *   greater than the second, respectively.
     */
    public fun <T> alternating(
        arr: Array<T>,
        low: Int = 0,
        high: Int = arr.size - 1,
        compare: (T, T) -> Int,
    ) {
        if (low >= high || arr.isEmpty()) {
            return
        }
        val pivotIndex = (low + high) / 2
        val pivotValue = arr[pivotIndex]

        arr[pivotIndex] = arr[high]
        arr[high] = pivotValue

        var counter = low
        var loopIndex = low

        while (loopIndex < high) {
            if (compare(arr[loopIndex], pivotValue) < (loopIndex and 0x1)) {
                val tmp = arr[loopIndex]
                arr[loopIndex] = arr[counter]
                arr[counter] = tmp
                counter++
            }
            loopIndex++
        }

        arr[high] = arr[counter]
        arr[counter] = pivotValue

        if (low < counter - 1) {
            alternating(arr, low, counter - 1, compare)
        }

        if (counter + 1 < high) {
            alternating(arr, counter + 1, high, compare)
        }
    }

    /**
     * Sorts the given [list] using a `lessThanZero` quicksort algorithm.
     *
     * This implementation partitions the list such that elements are moved to the left of the pivot
     * if the result of the [compare] function is **less than 0**.
     *
     * **Implementation details:**
     * - The pivot is initially swapped to the end to simplify partitioning. After partitioning, it
     *   is restored to its correct position.
     * - Unlike traditional quicksort, which typically moves elements **less than or equal to** the
     *   pivot to the left partition, this implementation **only moves elements strictly less than
     *   the pivot**. Elements equal to the pivot remain in the right partition.
     *
     * @param list The [MutableList] to sort. **This list will be mutated during sorting.**
     * @param low The inclusive start index of the range to sort.
     * @param high The inclusive end index of the range to sort.
     * @param compare A comparison function to determine the order of elements. Returns a negative
     *   integer, zero, or a positive integer if the first argument is less than, equal to, or
     *   greater than the second, respectively.
     */
    public fun <T> lessThanZero(
        list: MutableList<T>,
        low: Int = 0,
        high: Int = list.size - 1,
        compare: (T, T) -> Int,
    ) {
        if (low >= high || list.isEmpty()) {
            return
        }
        val pivotIndex = (low + high) / 2
        val pivotValue = list[pivotIndex]

        list[pivotIndex] = list[high]
        list[high] = pivotValue

        var counter = low
        var loopIndex = low

        while (loopIndex < high) {
            if (compare(list[loopIndex], pivotValue) < 0) {
                val tmp = list[loopIndex]
                list[loopIndex] = list[counter]
                list[counter] = tmp
                counter++
            }
            loopIndex++
        }

        list[high] = list[counter]
        list[counter] = pivotValue

        if (low < counter - 1) {
            lessThanZero(list, low, counter - 1, compare)
        }

        if (counter + 1 < high) {
            lessThanZero(list, counter + 1, high, compare)
        }
    }

    /**
     * Sorts the given [arr] using a `lessThanZero` quicksort algorithm.
     *
     * This implementation partitions the array such that elements are moved to the left of the
     * pivot if the result of the [compare] function is **less than 0**.
     *
     * **Implementation details:**
     * - The pivot is initially swapped to the end to simplify partitioning. After partitioning, it
     *   is restored to its correct position.
     * - Unlike traditional quicksort, which typically moves elements **less than or equal to** the
     *   pivot to the left partition, this implementation **only moves elements strictly less than
     *   the pivot**. Elements equal to the pivot remain in the right partition.
     *
     * @param arr The [Array] to sort. **This array will be mutated during sorting.**
     * @param low The inclusive start index of the range to sort.
     * @param high The inclusive end index of the range to sort.
     * @param compare A comparison function to determine the order of elements. Returns a negative
     *   integer, zero, or a positive integer if the first argument is less than, equal to, or
     *   greater than the second, respectively.
     */
    public fun <T> lessThanZero(
        arr: Array<T>,
        low: Int = 0,
        high: Int = arr.size - 1,
        compare: (T, T) -> Int,
    ) {
        if (low >= high || arr.isEmpty()) {
            return
        }
        val pivotIndex = (low + high) / 2
        val pivotValue = arr[pivotIndex]

        arr[pivotIndex] = arr[high]
        arr[high] = pivotValue

        var counter = low
        var loopIndex = low

        while (loopIndex < high) {
            if (compare(arr[loopIndex], pivotValue) < 0) {
                val tmp = arr[loopIndex]
                arr[loopIndex] = arr[counter]
                arr[counter] = tmp
                counter++
            }
            loopIndex++
        }

        arr[high] = arr[counter]
        arr[counter] = pivotValue

        if (low < counter - 1) {
            lessThanZero(arr, low, counter - 1, compare)
        }

        if (counter + 1 < high) {
            lessThanZero(arr, counter + 1, high, compare)
        }
    }

    /**
     * Sorts the given [list] using a `lessThanOne` quicksort algorithm.
     *
     * This implementation partitions the list such that elements are moved to the left of the pivot
     * if the result of the [compare] function is **less than 1**.
     *
     * **Implementation details:**
     * - The pivot is initially swapped to the end to simplify partitioning. After partitioning, it
     *   is restored to its correct position.
     * - Unlike [lessThanZero], this implementation **moves elements less than or equal to the
     *   pivot** to the left partition. Elements greater than the pivot remain in the right
     *   partition.
     *
     * @param list The [MutableList] to sort. **This list will be mutated during sorting.**
     * @param low The inclusive start index of the range to sort.
     * @param high The inclusive end index of the range to sort.
     * @param compare A comparison function to determine the order of elements. Returns a negative
     *   integer, zero, or a positive integer if the first argument is less than, equal to, or
     *   greater than the second, respectively.
     */
    public fun <T> lessThanOne(
        list: MutableList<T>,
        low: Int = 0,
        high: Int = list.size - 1,
        compare: (T, T) -> Int,
    ) {
        if (low >= high || list.isEmpty()) {
            return
        }
        val pivotIndex = (low + high) / 2
        val pivotValue = list[pivotIndex]

        list[pivotIndex] = list[high]
        list[high] = pivotValue

        var counter = low
        var loopIndex = low

        while (loopIndex < high) {
            if (compare(list[loopIndex], pivotValue) < 1) {
                val tmp = list[loopIndex]
                list[loopIndex] = list[counter]
                list[counter] = tmp
                counter++
            }
            loopIndex++
        }

        list[high] = list[counter]
        list[counter] = pivotValue

        if (low < counter - 1) {
            lessThanOne(list, low, counter - 1, compare)
        }

        if (counter + 1 < high) {
            lessThanOne(list, counter + 1, high, compare)
        }
    }

    /**
     * Sorts the given [arr] using a `lessThanOne` quicksort algorithm.
     *
     * This implementation partitions the array such that elements are moved to the left of the
     * pivot if the result of the [compare] function is **less than 1**.
     *
     * **Implementation details:**
     * - The pivot is initially swapped to the end to simplify partitioning. After partitioning, it
     *   is restored to its correct position.
     * - Unlike [lessThanZero], this implementation **moves elements less than or equal to the
     *   pivot** to the left partition. Elements greater than the pivot remain in the right
     *   partition.
     *
     * @param arr The [Array] to sort. **This array will be mutated during sorting.**
     * @param low The inclusive start index of the range to sort.
     * @param high The inclusive end index of the range to sort.
     * @param compare A comparison function to determine the order of elements. Returns a negative
     *   integer, zero, or a positive integer if the first argument is less than, equal to, or
     *   greater than the second, respectively.
     */
    public fun <T> lessThanOne(
        arr: Array<T>,
        low: Int = 0,
        high: Int = arr.size - 1,
        compare: (T, T) -> Int,
    ) {
        if (low >= high || arr.isEmpty()) {
            return
        }
        val pivotIndex = (low + high) / 2
        val pivotValue = arr[pivotIndex]

        arr[pivotIndex] = arr[high]
        arr[high] = pivotValue

        var counter = low
        var loopIndex = low

        while (loopIndex < high) {
            if (compare(arr[loopIndex], pivotValue) < 1) {
                val tmp = arr[loopIndex]
                arr[loopIndex] = arr[counter]
                arr[counter] = tmp
                counter++
            }
            loopIndex++
        }

        arr[high] = arr[counter]
        arr[counter] = pivotValue

        if (low < counter - 1) {
            lessThanOne(arr, low, counter - 1, compare)
        }

        if (counter + 1 < high) {
            lessThanOne(arr, counter + 1, high, compare)
        }
    }
}
