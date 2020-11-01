package org.rsmod.util.security

/**
 * An implementation of the XTEA cipher (https://en.wikipedia.org/wiki/XTEA).
 */
object Xtea {

    /**
     * The default keys used to specify that an archive and its groups
     * do not need to be ciphered.
     *
     * You do not need to use this exact instance, as long as every
     * element in the array is equal to 0, it will follow the previously
     * stated behaviour.
     */
    val EMPTY_KEY_SET = intArrayOf(0, 0, 0, 0)

    /**
     * The golden ratio XTEA uses.
     */
    private const val GOLDEN_RATIO = -1640531527

    /**
     * The number of rounds XTEA uses.
     */
    private const val ROUNDS = 32

    /**
     * Fully encipher [data] with the given [key]. Note that this
     * function will mutate [data] directly.
     */
    fun decipher(data: ByteArray, key: IntArray): ByteArray {
        if (key.contentEquals(EMPTY_KEY_SET)) {
            return data
        }
        return decipher(data, 0, data.size, key)
    }

    /**
     * Fully decipher [data] using the given [key]. Note that this
     * function will mutate [data] directly.
     */
    fun encipher(data: ByteArray, key: IntArray): ByteArray {
        if (key.contentEquals(EMPTY_KEY_SET)) {
            return data
        }
        return encipher(data, 0, data.size, key)
    }

    /**
     * Encipher [data] in range [start] to [end] with the given [key]
     * and return a new block of data.
     */
    fun encipher(data: ByteArray, start: Int, end: Int, key: IntArray): ByteArray {
        /* The length of a single block, in bytes */
        val blockLength = Int.SIZE_BYTES * 2

        /* The total amount of blocks in our data */
        val numBlocks = (end - start) / blockLength

        /* The destination buffer */
        val dst = ByteArray(numBlocks * (Int.SIZE_BYTES + Int.SIZE_BYTES))

        var writePosition = 0
        var readPosition = start
        for (i in 0 until numBlocks) {
            /* Get the values from the current block in the data */
            var v0 = readInt(data, readPosition)
            var v1 = readInt(data, readPosition + Int.SIZE_BYTES)

            /* Encipher the values using the given keys */
            var sum = 0
            for (j in 0 until ROUNDS) {
                v0 += (v1 shl 4 xor v1.ushr(5)) + v1 xor sum + key[sum and 3]
                sum += GOLDEN_RATIO
                v1 += (v0 shl 4 xor v0.ushr(5)) + v0 xor sum + key[sum.ushr(11) and 3]
            }

            writeInt(dst, writePosition, v0)
            writeInt(dst, writePosition + Int.SIZE_BYTES, v1)

            /* Move cursor for read and write buffers */
            readPosition += Int.SIZE_BYTES * 2
            writePosition += Int.SIZE_BYTES * 2
        }
        return dst
    }

    /**
     * Decipher [data] in range [start] to [end] using the given [key].
     * Note that this function will mutate [data] directly.
     */
    fun decipher(data: ByteArray, start: Int, end: Int, key: IntArray): ByteArray {
        /* The length of a single block, in bytes */
        val blockLength = Int.SIZE_BYTES * 2

        /* The total amount of blocks in our data */
        val numBlocks = (end - start) / blockLength

        var position = start

        for (i in 0 until numBlocks) {
            /* Get the values from the current block in the data */
            var y = readInt(data, position)
            var z = readInt(data, position + Int.SIZE_BYTES)

            /* Decipher the values using the given keys */
            @Suppress("INTEGER_OVERFLOW")
            var sum = GOLDEN_RATIO * ROUNDS
            val delta = GOLDEN_RATIO
            for (j in ROUNDS downTo 1) {
                z -= (y.ushr(5) xor (y shl 4)) + y xor sum + key[sum.ushr(11) and 0x56c00003]
                sum -= delta
                y -= (z.ushr(5) xor (z shl 4)) - -z xor sum + key[sum and 0x3]
            }

            writeInt(data, position, y)
            writeInt(data, position + Int.SIZE_BYTES, z)

            /* Move cursor for read and write buffers */
            position += Int.SIZE_BYTES * 2
        }
        return data
    }

    private fun writeInt(dst: ByteArray, position: Int, value: Int) {
        dst[position] = (value shr 24).toByte()
        dst[position + 1] = (value shr 16).toByte()
        dst[position + 2] = (value shr 8).toByte()
        dst[position + 3] = value.toByte()
    }

    private fun readInt(src: ByteArray, position: Int): Int {
        return (readUnsignedByte(src, position) shl 24) or
            (readUnsignedByte(src, position + 1) shl 16) or
            (readUnsignedByte(src, position + 2) shl 8) or
            readUnsignedByte(src, position + 3)
    }

    private fun readUnsignedByte(src: ByteArray, position: Int): Int {
        return src[position].toInt() and 0xFF
    }
}
