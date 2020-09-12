package gg.rsmod.util

/**
 *
 *
 * An implementation of the [ISAAC](http://www.burtleburtle.net/bob/rand/isaacafa.html) psuedorandom number
 * generator.
 *
 *
 * <pre>
 * ------------------------------------------------------------------------------
 * Rand.java: By Bob Jenkins.  My random number generator, ISAAC.
 * rand.init() -- initialize
 * rand.val()  -- get a random value
 * MODIFIED:
 * 960327: Creation (addition of randinit, really)
 * 970719: use context, not global variables, for internal state
 * 980224: Translate to Java
 * ------------------------------------------------------------------------------
</pre> *
 *
 *
 * This class has been changed to be more conformant to Java and javadoc conventions.
 *
 *
 * @author Bob Jenkins
 */
class IsaacRandom {

    /**
     * The accumulator.
     */
    private var a: Int = 0

    /**
     * The last result.
     */
    private var b: Int = 0

    /**
     * The counter.
     */
    private var c: Int = 0

    /**
     * The count through the results in the results array.
     */
    private var count: Int = 0

    /**
     * The internal state.
     */
    private lateinit var mem: IntArray

    /**
     * The results given to the user.
     */
    private lateinit var rsl: IntArray

    /**
     * Initializes the random number generator with the specified seed.
     *
     * @param seed The seed.
     */
    fun init(seed: IntArray) {
        mem = IntArray(SIZE)
        rsl = IntArray(SIZE)
        for (i in seed.indices) {
            rsl[i] = seed[i]
        }
        init()
    }

    /**
     * Initialises this random number generator.
     *
     * @param hasSeed Set to `true` if a seed was passed to the constructor.
     */
    private fun init(hasSeed: Boolean = true) {
        var i: Int
        var a: Int
        var b: Int
        var c: Int
        var d: Int
        var e: Int
        var f: Int
        var g: Int
        var h: Int
        h = GOLDEN_RATIO
        g = h
        f = g
        e = f
        d = e
        c = d
        b = c
        a = b

        i = 0
        while (i < 4) {
            a = a xor (b shl 11)
            d += a
            b += c
            b = b xor c.ushr(2)
            e += b
            c += d
            c = c xor (d shl 8)
            f += c
            d += e
            d = d xor e.ushr(16)
            g += d
            e += f
            e = e xor (f shl 10)
            h += e
            f += g
            f = f xor g.ushr(4)
            a += f
            g += h
            g = g xor (h shl 8)
            b += g
            h += a
            h = h xor a.ushr(9)
            c += h
            a += b
            ++i
        }

        i = 0
        while (i < SIZE) { /* fill in mem[] with messy stuff */
            if (hasSeed) {
                a += rsl[i]
                b += rsl[i + 1]
                c += rsl[i + 2]
                d += rsl[i + 3]
                e += rsl[i + 4]
                f += rsl[i + 5]
                g += rsl[i + 6]
                h += rsl[i + 7]
            }
            a = a xor (b shl 11)
            d += a
            b += c
            b = b xor c.ushr(2)
            e += b
            c += d
            c = c xor (d shl 8)
            f += c
            d += e
            d = d xor e.ushr(16)
            g += d
            e += f
            e = e xor (f shl 10)
            h += e
            f += g
            f = f xor g.ushr(4)
            a += f
            g += h
            g = g xor (h shl 8)
            b += g
            h += a
            h = h xor a.ushr(9)
            c += h
            a += b
            mem[i] = a
            mem[i + 1] = b
            mem[i + 2] = c
            mem[i + 3] = d
            mem[i + 4] = e
            mem[i + 5] = f
            mem[i + 6] = g
            mem[i + 7] = h
            i += 8
        }

        if (hasSeed) { /* second pass makes all of seed affect all of mem */
            i = 0
            while (i < SIZE) {
                a += mem[i]
                b += mem[i + 1]
                c += mem[i + 2]
                d += mem[i + 3]
                e += mem[i + 4]
                f += mem[i + 5]
                g += mem[i + 6]
                h += mem[i + 7]
                a = a xor (b shl 11)
                d += a
                b += c
                b = b xor c.ushr(2)
                e += b
                c += d
                c = c xor (d shl 8)
                f += c
                d += e
                d = d xor e.ushr(16)
                g += d
                e += f
                e = e xor (f shl 10)
                h += e
                f += g
                f = f xor g.ushr(4)
                a += f
                g += h
                g = g xor (h shl 8)
                b += g
                h += a
                h = h xor a.ushr(9)
                c += h
                a += b
                mem[i] = a
                mem[i + 1] = b
                mem[i + 2] = c
                mem[i + 3] = d
                mem[i + 4] = e
                mem[i + 5] = f
                mem[i + 6] = g
                mem[i + 7] = h
                i += 8
            }
        }

        isaac()
        count = SIZE
    }

    /**
     * Generates 256 results.
     */
    private fun isaac() {
        var i: Int
        var j: Int
        var x: Int
        var y: Int

        b += ++c
        i = 0
        j = SIZE / 2
        while (i < SIZE / 2) {
            x = mem[i]
            a = a xor (a shl 13)
            a += mem[j++]
            y = mem[x and MASK shr 2] + a + b
            mem[i] = y
            b = mem[y shr LOG_SIZE and MASK shr 2] + x
            rsl[i++] = b

            x = mem[i]
            a = a xor a.ushr(6)
            a += mem[j++]
            y = mem[x and MASK shr 2] + a + b
            mem[i] = y
            b = mem[y shr LOG_SIZE and MASK shr 2] + x
            rsl[i++] = b

            x = mem[i]
            a = a xor (a shl 2)
            a += mem[j++]
            y = mem[x and MASK shr 2] + a + b
            mem[i] = y
            b = mem[y shr LOG_SIZE and MASK shr 2] + x
            rsl[i++] = b

            x = mem[i]
            a = a xor a.ushr(16)
            a += mem[j++]
            y = mem[x and MASK shr 2] + a + b
            mem[i] = y
            b = mem[y shr LOG_SIZE and MASK shr 2] + x
            rsl[i++] = b
        }

        j = 0
        while (j < SIZE / 2) {
            x = mem[i]
            a = a xor (a shl 13)
            a += mem[j++]
            y = mem[x and MASK shr 2] + a + b
            mem[i] = y
            b = mem[y shr LOG_SIZE and MASK shr 2] + x
            rsl[i++] = b

            x = mem[i]
            a = a xor a.ushr(6)
            a += mem[j++]
            y = mem[x and MASK shr 2] + a + b
            mem[i] = y
            b = mem[y shr LOG_SIZE and MASK shr 2] + x
            rsl[i++] = b

            x = mem[i]
            a = a xor (a shl 2)
            a += mem[j++]
            y = mem[x and MASK shr 2] + a + b
            mem[i] = y
            b = mem[y shr LOG_SIZE and MASK shr 2] + x
            rsl[i++] = b

            x = mem[i]
            a = a xor a.ushr(16)
            a += mem[j++]
            y = mem[x and MASK shr 2] + a + b
            mem[i] = y
            b = mem[y shr LOG_SIZE and MASK shr 2] + x
            rsl[i++] = b
        }
    }

    /**
     * Gets the next random value.
     *
     * @return The next random value.
     */
    fun nextInt(): Int {
        if (0 == count--) {
            isaac()
            count = SIZE - 1
        }
        return rsl[count]
    }

    fun opcodeModifier(): Int = if (this === ZERO) 0 else nextInt()

    companion object {
        /**
         * A constant, uninitialized [IsaacRandom] instance.
         */
        val ZERO = IsaacRandom()

        /**
         * The golden ratio.
         */
        private const val GOLDEN_RATIO = -0x61c88647

        /**
         * The log of the size of the result and memory arrays.
         */
        private const val LOG_SIZE = 8

        /**
         * The size of the result and memory arrays.
         */
        private const val SIZE = 1 shl LOG_SIZE

        /**
         * A mask for pseudorandom lookup.
         */
        private const val MASK = SIZE - 1 shl 2
    }
}
