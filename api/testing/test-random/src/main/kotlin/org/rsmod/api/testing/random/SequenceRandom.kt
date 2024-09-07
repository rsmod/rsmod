package org.rsmod.api.testing.random

import org.rsmod.api.random.GameRandom

/**
 * [SequenceRandom] is an implementation of the [GameRandom] interface that allows for deterministic
 * random number generation based on a predefined sequence of integers. This class is particularly
 * useful in scenarios where predictable and repeatable random values are required.
 *
 * ## Key Features
 * - **Predefined Sequence**: The core of this class is a mutable list of integers (`sequence`),
 *   which serves as the source for all generated random numbers. Users can either provide an
 *   initial sequence or fill it out as needed. (though it should be noted that the maximum capacity
 *   of the list cannot be exceeded)
 * - **Controlled Access**: The class maintains separate indices for reading and writing values
 *   (`readerIndex` and `writerIndex`). This ensures that random numbers are consumed in the order
 *   they were added, providing a clear and controlled flow of values.
 * - **Idiomatic Access**: The `next` and `then` properties provide a more idiomatic way to set the
 *   next value in the sequence. This can enhance code readability by emphasizing the chronological
 *   order of events in the sequence.
 * - **Reset Capability**: The `reset` function allows users to reset the reading and writing
 *   indexes, effectively reusing the sequence from the beginning without the need to create a new
 *   instance.
 *
 * ## Example Usage
 *
 * ```
 * // Initialize with a predefined sequence of size [DEFAULT_CAPACITY].
 * val random = SequenceRandom()
 *
 * // Set specific values in the sequence.
 * random.next = 42
 * random.then = 99 // Equivalent to random.next = 99
 *
 * // Generate random numbers.
 * val value1 = random.of(100)       // Will return 42
 * val value2 = random.of(50, 100)   // Will return 99
 *
 * // Reset the sequence for reuse if needed.
 * random.reset()
 * ```
 *
 * ## Notes
 * - **Index Bounds**: The class ensures that any attempt to read beyond the available sequence or
 *   write beyond its capacity will throw an [IndexOutOfBoundsException].
 * - **Deterministic Behavior**: Since the sequence of random values is predetermined, the behavior
 *   of [SequenceRandom] is fully deterministic, meaning the same sequence of operations will always
 *   yield the same results.
 *
 * @param sequence A mutable list of integers that serves as the source for random values. If not
 *   provided, a sequence of size [DEFAULT_CAPACITY] filled with zeros is created.
 * @constructor Creates a [SequenceRandom] instance with a predefined sequence of integers.
 */
public class SequenceRandom(private val sequence: MutableList<Int>) : GameRandom {
    public constructor(size: Int = DEFAULT_CAPACITY) : this(MutableList(size) { 0 })

    private var writerIndex = 0
    private var readerIndex = 0

    private val size: Int
        get() = sequence.size

    /**
     * - **Setter**: assigns the next value in the sequence, advancing the [writerIndex] to maintain
     *   chronological order.
     * - **Getter**: retrieves (without advancing) the next value in the sequence based on the
     *   current [readerIndex].
     */
    public var next: Int
        get() = sequence[readerIndex]
        set(value) {
            sequence[writerIndex++] = value
        }

    /**
     * Provides an idiomatic way to set the next value in the sequence.
     *
     * This property functions as an alias to the [next] property, allowing you to set the next
     * predetermined value in the sequence. This can be useful in scenarios where more fluent or
     * descriptive code is desired. For example, you can use `then` right after `next` to emphasize
     * the chronological order of events in a sequence, improving readability and showing intent.
     *
     * @see [next]
     */
    public var then: Int
        get() = sequence[readerIndex]
        set(value) {
            sequence[writerIndex++] = value
        }

    /**
     * Resets both the [writerIndex] and [readerIndex], but does _not_ clear the elements in the
     * [sequence].
     */
    public fun reset() {
        writerIndex = 0
        readerIndex = 0
    }

    public operator fun set(index: Int, value: Int) {
        if (index < 0) {
            throw IndexOutOfBoundsException("Index `$index` must be positive.")
        } else if (index >= size) {
            throw IndexOutOfBoundsException("Index `$index` must not exceed `$size`.")
        }
        sequence[index] = value
    }

    override fun of(maxExclusive: Int): Int = nextInt()

    override fun of(minInclusive: Int, maxInclusive: Int): Int {
        val next = nextInt()
        if (next !in minInclusive..maxInclusive) {
            throw IllegalStateException(
                "The next value `$next` in the sequence is not between $minInclusive-$maxInclusive."
            )
        }
        return next
    }

    private fun nextInt(): Int {
        if (readerIndex !in sequence.indices) {
            throw IndexOutOfBoundsException("There are no more predetermined values in `sequence.")
        }
        return sequence[readerIndex++]
    }

    public companion object {
        public const val DEFAULT_CAPACITY: Int = 32
    }
}
