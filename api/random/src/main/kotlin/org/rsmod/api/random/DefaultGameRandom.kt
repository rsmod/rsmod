@file:Suppress("konsist.avoid usage of stdlib Random in properties")

package org.rsmod.api.random

import kotlin.random.Random

/**
 * A default implementation of the [GameRandom] interface backed by Kotlin's [Random] class. This
 * implementation delegates random number generation to the standard [Random] class, with support
 * for seeding to control the randomness. It is suitable for general-purpose scenarios where you
 * want to leverage standard random number functionality while maintaining the flexibility to swap
 * out the underlying random generator for testing purposes.
 *
 * ### Example Usage:
 * ```
 * val gameRandom = DefaultGameRandom(seed = 1234L)
 * val randomValue = gameRandom.of(10) // Returns a random integer from 0 to 9
 * val randomRangeValue = gameRandom.of(1, 5) // Returns a random integer from 1 to 5
 * ```
 *
 * @see [GameRandom]
 */
public class DefaultGameRandom(private val random: Random) : GameRandom {
    public constructor(seed: Long) : this(Random(seed))

    public constructor() : this(Random)

    override fun of(maxExclusive: Int): Int = random.nextInt(maxExclusive)

    override fun of(minInclusive: Int, maxInclusive: Int): Int =
        random.nextInt(minInclusive, maxInclusive + 1)
}
