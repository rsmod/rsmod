package org.rsmod.api.random

import org.rsmod.map.CoordGrid

// Yet another Konsist bug... @Inject in the example code messes with the see tag parsing... for
// some ungodly reason.
@Suppress("konsist.test see tag references are formatted correctly")
/**
 * The [GameRandom] interface provides a flexible and testable alternative to standard random number
 * generators like [java.util.Random] and [kotlin.random.Random]. By requiring [GameRandom] to be
 * passed into classes as a dependency, it allows for easy substitution with a deterministic or
 * fixed random number generator during testing, ensuring reproducibility and control over random
 * behavior.
 *
 * This design promotes better management of randomness in game-related logic, where precise and
 * consistent outcomes are crucial. Unlike singletons used in standard libraries, [GameRandom]
 * enables you to manage and control random number generation more effectively, facilitating both
 * varied randomness during normal operation and deterministic results when needed for testing.
 *
 * ### Example Usage:
 * ```
 * class MyPlugin @Inject constructor(private val random: GameRandom) : PluginScript() {
 *     fun rollDie(sides: Int): Int {
 *         return random.of(sides)
 *     }
 * }
 * ```
 *
 * @see [DefaultGameRandom]
 */
public interface GameRandom {
    /**
     * Returns a random [CoordGrid] translated by a random offset within the range `-radius` to
     * `radius` in both x and z directions, relative to the given [coords].
     *
     * @param coords The base coordinate to translate.
     * @param radius The maximum distance in both x and z directions from the base coordinate.
     * @return A new [CoordGrid] with the translated coordinates. The resulting coordinate may be
     *   the same as the input [coords].
     */
    public fun of(coords: CoordGrid, radius: Int): CoordGrid =
        coords.translate(xOffset = of(-radius, radius), zOffset = of(-radius, radius))

    /** Returns one of the two provided values randomly. */
    public fun <T> pick(first: T, second: T): T =
        when (of(maxExclusive = 2)) {
            0 -> first
            1 -> second
            else -> throw IllegalStateException()
        }

    /** Returns one of the three provided values randomly. */
    public fun <T> pick(first: T, second: T, third: T): T =
        when (of(maxExclusive = 3)) {
            0 -> first
            1 -> second
            2 -> third
            else -> throw IllegalStateException()
        }

    /** Returns one of the four provided values randomly. */
    public fun <T> pick(first: T, second: T, third: T, fourth: T): T =
        when (of(maxExclusive = 4)) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            else -> throw IllegalStateException()
        }

    /** Returns one of the five provided values randomly. */
    public fun <T> pick(first: T, second: T, third: T, fourth: T, fifth: T): T =
        when (of(maxExclusive = 4)) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            else -> throw IllegalStateException()
        }

    /**
     * Returns a random element from the provided array.
     *
     * @throws NoSuchElementException if the array is empty.
     */
    public fun <T> pick(elements: Array<T>): T =
        pickOrNull(elements) ?: throw NoSuchElementException("Array is empty.")

    /**
     * Returns a random element from the provided array, or null if the array is empty.
     *
     * @return A randomly selected element from [elements], or null if [elements] is empty.
     */
    public fun <T> pickOrNull(elements: Array<T>): T? =
        if (elements.isEmpty()) null else elements[of(elements.size)]

    /**
     * Returns a random element from the provided collection.
     *
     * @throws NoSuchElementException if the collection is empty.
     */
    public fun <T> pick(elements: Collection<T>): T =
        pickOrNull(elements) ?: throw NoSuchElementException("Collection is empty.")

    /**
     * Returns a random element from the provided collection, or null if the collection is empty.
     *
     * @return A randomly selected element from [elements], or null if [elements] is empty.
     */
    public fun <T> pickOrNull(elements: Collection<T>): T? =
        if (elements.isEmpty()) null else elements.elementAt(of(elements.size))

    /**
     * Returns a random integer within the inclusive range specified by [rangeInclusive].
     *
     * @param rangeInclusive The inclusive range of values from which the random integer will be
     *   chosen. For example, if the range is `0..3`, the method can return `0`, `1`, `2`, or `3`.
     */
    public fun of(rangeInclusive: IntRange): Int = of(rangeInclusive.first, rangeInclusive.last)

    /**
     * Returns a random boolean value based on the upper bound (exclusive).
     *
     * @param maxExclusive The upper bound (exclusive) for generating the random integer that
     *   determines the boolean value. Defaults to `1`, simulating a 50/50 chance.
     * @return `true` if the random integer is `0`, otherwise `false`.
     * @throws IllegalArgumentException if [maxExclusive] is less than or equal to zero.
     */
    public fun randomBoolean(maxExclusive: Int = 1): Boolean = of(maxExclusive) == 0

    /**
     * Returns a random integer between 0 (inclusive) and the specified maximum value (exclusive).
     *
     * @param maxExclusive The upper bound (exclusive) for the random integer.
     * @return A random integer from 0 (inclusive) to `maxExclusive` (exclusive).
     * @throws IllegalArgumentException if [maxExclusive] is less than or equal to zero.
     */
    public fun of(maxExclusive: Int): Int

    /**
     * Returns a random integer within the specified inclusive range.
     *
     * @param minInclusive The lower bound (inclusive) for the random integer.
     * @param maxInclusive The upper bound (inclusive) for the random integer.
     * @return A random integer within the range from given bounds. For example, if [minInclusive]
     *   is `1` and [maxInclusive] is `3`, the method can return 1, 2, or 3.
     * @throws IllegalArgumentException if [minInclusive] is greater than [maxInclusive], or if
     *   either value is less than or equal to zero.
     */
    public fun of(minInclusive: Int, maxInclusive: Int): Int
}
