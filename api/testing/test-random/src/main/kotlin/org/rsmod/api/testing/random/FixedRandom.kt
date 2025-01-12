package org.rsmod.api.testing.random

import org.rsmod.api.random.GameRandom

/**
 * [FixedRandom] is an implementation of the [GameRandom] interface that consistently returns a
 * predetermined random value. This class is useful for scenarios where you need to ensure that a
 * specific value is returned as the random result.
 *
 * ## Key Features
 * - **Fixed Value Generation**: Instead of generating random numbers, [FixedRandom] always returns
 *   a specific value that is set using the [set] function. This allows for controlled testing
 *   environments where the randomness needs to be predictable.
 * - **Error Handling**: The class ensures that the expected value is within the requested range
 *   when using `of(minInclusive: Int, maxInclusive: Int)`. If the value falls outside the specified
 *   bounds, an [IllegalStateException] is thrown, highlighting potential misconfigurations.
 * - **Optional Initialization**: You can optionally set the initial expected value at the time of
 *   instantiation. If not provided, the value must be set before any random number generation
 *   operations are performed.
 *
 * ## When To Use
 * [FixedRandom] is ideal for scenarios where you need a consistent and predictable random value,
 * but do not require control over a sequence of future random values. This makes it a simpler
 * alternative to [SequenceRandom], which is better suited for cases where you need to manage and
 * retrieve a series of predetermined values in a specific order.
 *
 * Use [FixedRandom] when:
 * - You only need a single, consistent random value for testing purposes.
 * - The focus is on ensuring that a specific value is returned for all random number requests.
 * - You do not need to simulate or iterate through multiple random values.
 *
 * If you require more complex behavior involving sequences of random values, consider using
 * [SequenceRandom] instead.
 *
 * ## Example Usage
 *
 * ```
 * // Initialize with a fixed value of 42.
 * val random = FixedRandom(42)
 *
 * // Alternatively, set the value later.
 * random.set(99)
 *
 * // Retrieve the fixed random value.
 * val value = random.of(100) // Always returns 99
 *
 * // Ensure the value is within the specified range.
 * val valueInRange = random.of(50, 100) // Will return 99
 * ```
 *
 * ## Notes
 * - **Mandatory Value Setting**: If no initial value is provided at construction, you must call
 *   [set] to define the expected value before invoking any of the random number generation methods.
 *   Failure to do so will result in an exception being thrown.
 *
 * @param start The initial value to be returned by the random generation methods. If not provided,
 *   the value must be set using [set] before use.
 * @constructor Creates a [FixedRandom] instance with an optional starting value.
 */
public class FixedRandom(start: Int? = null) : GameRandom {
    private var expected: Int? = start

    public fun set(expected: Int) {
        this.expected = expected
    }

    override fun of(maxExclusive: Int): Int = expected()

    override fun of(minInclusive: Int, maxInclusive: Int): Int {
        val expected = expected()
        if (expected !in minInclusive..maxInclusive) {
            throw IllegalStateException(
                "The set expected value `$expected` is not between $minInclusive-$maxInclusive."
            )
        }
        return expected
    }

    override fun randomDouble(): Double = expected() / 100.0

    private fun expected(): Int =
        checkNotNull(expected) { "`expected` value must be set. Use the `set` function." }
}
