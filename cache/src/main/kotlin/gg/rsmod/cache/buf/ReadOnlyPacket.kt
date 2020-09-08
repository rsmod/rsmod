package gg.rsmod.cache.buf

import gg.rsmod.cache.util.CharacterUtil

/**
 * A read-only packet is a buffer containing an array of bytes and a pointer
 * of the last position in said array that it has read data from.
 *
 * This buffer should only be used to read data and not write to it.
 * If you need to write to a buffer, consider using [WriteOnlyPacket]
 * or [ReadWritePacket].
 *
 * @author Tom
 */
internal class ReadOnlyPacket(private val buffer: ByteArray) {

    /**
     * The last position in [data] that was accessed.
     */
    var position = 0

    /**
     * Create a [ReadOnlyPacket] with a backing-array the size of [capacity].
     */
    constructor(capacity: Int) : this(ByteArray(capacity))

    /**
     * Return the the [Byte] located in position [index] on the backing array.
     */
    operator fun get(index: Int): Byte = buffer[index]

    /**
     * The backing array for this packet.
     */
    val data: ByteArray
        get() = buffer

    /**
     * The capacity of this packet.
     */
    val capacity: Int
        get() = data.size

    /**
     * The amount of bytes that can be read from this packet taking the
     * current [position] into account.
     */
    val readableBytes: Int
        get() = buffer.size - position

    /**
     * Check if this packet has any more [readableBytes].
     */
    val isReadable: Boolean
        get() = readableBytes > 0

    /**
     * Reset the read position of this packet.
     */
    fun reset(): ReadOnlyPacket {
        position = 0
        return this
    }

    /**
     * Read the next value as a signed byte.
     */
    val g1s: Int
        get() = this[position++].toInt()

    /**
     * Read the next signed byte minus 128 ([g1s] - 128) as a
     * signed byte.
     */
    val g1s_altA: Int
        get() = g1s - 128

    /**
     * Read the negated value of the next signed byte (-[g1s]).
     */
    val g1s_altC: Int
        get() = 0 - g1s

    /**
     * Read 128 subtracted by the next signed byte (128 - [g1s]).
     */
    val g1s_altS: Int
        get() = 128 - g1s

    /**
     * Read the next value as an unsigned byte.
     */
    val g1: Int
        get() = g1s and 0xFF

    /**
     * Read the next signed byte minus 128 ([g1s] - 128) as an
     * unsigned byte.
     */
    val g1_altA: Int
        get() = (g1s - 128) and 0xFF

    /**
     * Read the negated value of the next unsigned byte (-[g1]).
     */
    val g1_altC: Int
        get() = (0 - g1s) and 0xFF

    /**
     * Read 128 subtracted by the next unsigned byte (128 - [g1]).
     */
    val g1_altS: Int
        get() = (128 - g1s) and 0xFF

    /**
     * Read the next two bytes as a signed short.
     */
    val g2s: Int
        get() {
            val value = g2
            return if (value > Short.MAX_VALUE) {
                value - 0x10000
            } else {
                value
            }
        }

    /**
     * Read the next two bytes as a signed, little-endian short.
     */
    val g2sLE: Int
        get() {
            val value = g2LE
            return if (value > 0x7FFF) {
                value - 0x10000
            } else {
                value
            }
        }

    /**
     * Read the next two bytes as an unsigned short.
     */
    val g2: Int
        get() = (g1 shl 8) or g1

    /**
     * Read the next two bytes as an unsigned short with
     * the first byte being read as an unsigned byte and
     * second byte being read as type-A ([g1_altA]).
     */
    val g2_altA: Int
        get() = (g1 shl 8) or g1_altA

    /**
     * Read the next two bytes as an unsigned, little-endian short.
     */
    val g2LE: Int
        get() = g1 or (g1 shl 8)

    /**
     * Read the next two bytes as an unsigned, little-endian short
     * with the first byte being read as type-A ([g1_altA])
     * and second byte being read as an unsigned byte.
     */
    val g2LE_altA: Int
        get() = g1_altA or (g1 shl 8)

    /**
     * Read the next three bytes as a signed medium.
     */
    val g3s: Int
        get() {
            val value = g3
            return if (value > 0x7FFFFF) {
                value - 0x1000000
            } else {
                value
            }
        }

    /**
     * Read the next three bytes as an unsigned medium.
     */
    val g3: Int
        get() = (g1 shl 16) or (g1 shl 8) or g1

    /**
     * Read the next four bytes as an int.
     */
    val g4: Int
        get() = (g1 shl 24) or (g1 shl 16) or (g1 shl 8) or g1

    /**
     * Read the next four bytes as a big-endian int.
     */
    val g4_alt1: Int
        get() = g1 or (g1 shl 8) or (g1 shl 16) or (g1 shl 24)

    /**
     * Read the next four bytes as a big-endian int, with the
     * caveat that the order of the first two bytes are swapped,
     * and that the order of the last two bytes are swapped.
     * Also known as "V1 order", or "Middle order".
     */
    val g4_alt2: Int
        get() = (g1 shl 8) or g1 or (g1 shl 24) or (g1 shl 16)

    /**
     * Read the next four bytes as an (little-endian) int, with
     * the caveat that the order of the first two bytes are swapped,
     * and that the order of the last two bytes are swapped.
     * Also known as "V2 order", or "Inverse Middle order".
     */
    val g4_alt3: Int
        get() = (g1 shl 16) or (g1 shl 24) or g1 or (g1 shl 8)

    /**
     * Read the next four bytes as an long.
     */
    val g5: Long
        get() = (g1 shl 32).toLong() or (g4.toLong() and 0xFFFFFFFF)

    /**
     * Read the next one or two bytes based on the value
     * of the next byte.
     */
    val gsmart1or2: Int
        get() {
            return if ((this[position].toInt() and 0xFF) < 128) {
                g1
            } else {
                g2 - 0x8000
            }
        }

    /**
     * Read the next two or four bytes based on the value
     * of the next byte.
     */
    val gsmart2or4: Int
        get() {
            return if (this[position] < 0) {
                g4 and Int.MAX_VALUE
            } else {
                g2
            }
        }

    /**
     * Read the next one or two bytes as [gsmart1or2] continuously
     * until the value read is not equal to [Short.MAX_VALUE].
     * On each iteration, the return value will increment by
     * [Short.MAX_VALUE] and at the end of the iterations, the
     * value that did not equal to [Short.MAX_VALUE] will
     * be added to the return value.
     */
    val gincsmart: Int
        get() {
            var value = 0
            var current = gsmart1or2
            while (current == Short.MAX_VALUE.toInt()) {
                current = gsmart1or2
                value += Short.MAX_VALUE
            }
            value += current
            return value
        }

    /**
     * Read the next eight bytes as a long.
     */
    val g8: Long
        get() {
            val l = g4.toLong() and 4294967295L
            val r = g4.toLong() and 4294967295L
            return (l shl 32) or r
        }

    /**
     * Convert the next four bytes into a count of bits to convert
     * into, and return, a float.
     */
    val gfloat: Float
        get() = Float.fromBits(g4)

    /**
     * Read the the next bytes as a string until terminated.
     */
    val gjstr: String
        get() {
            val builder = StringBuilder(readableBytes)
            while (isReadable) {
                val next = g1
                if (next == 0) {
                    break
                }
                val character: Char = if (next in 128 until 160) {
                    var cleansed = CharacterUtil.VALID_CHARACTERS[next - 128]
                    if (cleansed.toInt() == 0) {
                        cleansed = '?'
                    }
                    cleansed
                } else {
                    next.toChar()
                }
                builder.append(character)
            }
            return builder.toString()
        }

    /**
     * Read the next bytes as a string until terminated.
     */
    val fastgjstr: String?
        get() {
            val empty = this[position].toInt() == 0
            if (empty) {
                position++
            }
            return if (empty) {
                null
            } else {
                gjstr
            }
        }

    /**
     * Transfers the data from the specified source data into this packet
     * starting from the current [position], reading the data until the amount
     * of bytes transferred equals [length].
     *
     * If [src] length is less than [length] - [srcOffset], an [ArrayIndexOutOfBoundsException]
     * will be thrown.
     *
     * @param src the array of bytes to transfer into this packet.
     * @param srcOffset the offset to begin reading from the source data.
     * @param length the amount of bytes to transfer from the source data.
     */
    fun gdata(src: ByteArray, srcOffset: Int, length: Int) {
        for (i in 0 until length) {
            data[position++] = src[srcOffset + i]
        }
    }

    /**
     * Transfers the data from the specified source data into this packet
     * starting from the current [position], fully reading the data until
     * the amount of bytes transferred equals to the size of the source
     * data.
     *
     * @param src the array of bytes to transfer into this packet.
     */
    fun gdata(src: ByteArray) = gdata(src, 0, src.size)

    /**
     * Get the the next [length] amount of bytes from this packet and
     * write them on [dst] starting from [position].
     */
    fun pdata(dst: ByteArray, position: Int, length: Int) {
        for (i in 0 until length) {
            dst[position + i] = g1.toByte()
        }
    }

    /**
     * Get and put the next bytes from this packet onto [dst].
     * The amount of bytes being transferred is equal to the
     * size of [dst].
     */
    fun pdata(dst: ByteArray) = pdata(dst, 0, dst.size)

    /**
     * Get the next [length] amount of bytes in inverse order,
     * as [g1s_altA] and put them into [dst] starting from
     * [position].
     */
    fun pdataLE_altA(dst: ByteArray, position: Int, length: Int) {
        for (i in position + length - 1 downTo position) {
            dst[i] = g1s_altA.toByte()
        }
    }

    companion object {

        /**
         * Create a [ReadOnlyPacket] with [data] as its backing array.
         */
        fun from(data: ByteArray): ReadOnlyPacket = ReadOnlyPacket(data)
    }
}
