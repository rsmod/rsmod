package gg.rsmod.cache.buf

import gg.rsmod.cache.util.BitConstants
import gg.rsmod.cache.util.CharacterUtil.toEncoded

/**
 * A write-only packet is a buffer containing an array of bytes and a pointer
 * of the last position in said array that it has written data to.
 *
 * This buffer should only be used to write data and not read from it.
 * If you need to read from a buffer, consider using [ReadOnlyPacket]
 * or [ReadWritePacket].
 */
internal class WriteOnlyPacket(private val buffer: ByteArray) {

    /**
     * The last position in [data] that was accessed.
     */
    var position = 0

    /**
     * The current bit position.
     */
    var bitPosition = DISABLED_BIT_POSITION

    /**
     * Create a [WriteOnlyPacket] with a backing-array the size of [capacity].
     */
    constructor(capacity: Int) : this(ByteArray(capacity))

    /**
     * Get the [Byte] located in position [index] on the backing array.
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
     * The amount of bytes that can be written to this packet taking the
     * current [position] into account.
     */
    val writableBytes: Int
        get() = buffer.size - position

    /**
     * Check if this packet has any more [writableBytes].
     */
    val isWritable: Boolean
        get() = writableBytes > 0

    /**
     * Reset the write position of this packet.
     */
    fun reset(): WriteOnlyPacket {
        position = 0
        return this
    }

    /**
     * Set [value] in our buffer and increment our [position] by one.
     */
    fun p1(value: Int) {
        buffer[position++] = value.toByte()
    }

    /**
     * Set [value] in our buffer as [value] plus 128, and increment
     * our [position] by one.
     */
    fun p1_altA(value: Int) {
        buffer[position++] = (value + 128).toByte()
    }

    /**
     * Set [value] in our buffer as negated [value], and increment
     * our [position] by one.
     */
    fun p1_altC(value: Int) {
        buffer[position++] = (0 - value).toByte()
    }

    /**
     * Set [value] in our buffer as 128 minus [value], and increment
     * our [position] by one.
     */
    fun p1_altS(value: Int) {
        buffer[position++] = (128 - value).toByte()
    }

    /**
     * Set [value] in our buffer as two bytes, and increment
     * our [position] by one.
     */
    fun p2(value: Int) {
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = value.toByte()
    }

    /**
     * Set [value] in our buffer as two bytes, the second byte being set
     * to [value] plus 128, and increment our [position] by two.
     */
    fun p2_altA(value: Int) {
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = (value + 128).toByte()
    }

    /**
     * Set [value] in our buffer as two bytes in little-endian order,
     * and increment our [position] by two.
     */
    fun p2LE(value: Int) {
        buffer[position++] = value.toByte()
        buffer[position++] = (value shr 8).toByte()
    }

    /**
     * Set [value] in our buffer as two bytes in little-endian order,
     * the second byte being set to [value] plus 128, and increment
     * our [position] by two.
     */
    fun p2LE_altA(value: Int) {
        buffer[position++] = (value + 128).toByte()
        buffer[position++] = (value shr 8).toByte()
    }

    /**
     * Set [value] in our buffer as three bytes, and increment
     * our [position] by three.
     */
    fun p3(value: Int) {
        buffer[position++] = (value shr 16).toByte()
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = value.toByte()
    }

    /**
     * Set [value] in our buffer as four bytes, and increment
     * our [position] by four.
     */
    fun p4(value: Int) {
        buffer[position++] = (value shr 24).toByte()
        buffer[position++] = (value shr 16).toByte()
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = value.toByte()
    }

    /**
     * Set [value] in our buffer as four bytes in little-endian order,
     * and increment our [position] by four.
     */
    fun p4_alt1(value: Int) {
        buffer[position++] = value.toByte()
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = (value shr 16).toByte()
        buffer[position++] = (value shr 24).toByte()
    }

    /**
     * Set [value] in our buffer as four bytes in "v1" or "middle"
     * order, and increment our [position] by four.
     */
    fun p4_alt2(value: Int) {
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = value.toByte()
        buffer[position++] = (value shr 24).toByte()
        buffer[position++] = (value shr 16).toByte()
    }

    /**
     * Set [value] in our buffer as four bytes in "v2" or "inverse middle"
     * order, and increment our [position] by four.
     */
    fun p4_alt3(value: Int) {
        buffer[position++] = (value shr 16).toByte()
        buffer[position++] = (value shr 24).toByte()
        buffer[position++] = value.toByte()
        buffer[position++] = (value shr 8).toByte()
    }

    /**
     * Set [value] in our buffer as five bytes, and increment
     * our [position] by five.
     */
    fun p5(value: Long) {
        buffer[position++] = (value shr 32).toByte()
        buffer[position++] = (value shr 24).toByte()
        buffer[position++] = (value shr 16).toByte()
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = value.toByte()
    }

    /**
     * Set [value] in our buffer as eight bytes, and increment
     * our [position] by eight.
     */
    fun p8(value: Long) {
        buffer[position++] = (value shr 56).toByte()
        buffer[position++] = (value shr 48).toByte()
        buffer[position++] = (value shr 40).toByte()
        buffer[position++] = (value shr 32).toByte()
        buffer[position++] = (value shr 24).toByte()
        buffer[position++] = (value shr 16).toByte()
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = value.toByte()
    }

    /**
     * Writes a byte for each character in the given string value in
     * the current position and terminates the string with a 0 written
     * as a byte, all while incrementing the position on each write by
     * one.
     */
    fun pjstr(string: String) {
        string.forEach { character ->
            p1(character.toEncoded().toInt())
        }
        p1(0)
    }

    /**
     * Set [value] in our buffer as one or two bytes depending on the
     * [value], and increment our [position] by one or two.
     */
    fun psmart1or2(value: Int) {
        if (value <= Byte.MAX_VALUE) {
            p1(value)
        } else {
            p2(value)
        }
    }

    /**
     * Set [value] in our buffer as two or four bytes depending on the
     * [value], and increment our [position] by two or four.
     */
    fun psmart2or4(value: Int) {
        if (value <= Short.MAX_VALUE) {
            p2(value)
        } else {
            p4(value)
        }
    }

    /**
     * Get the the next [length] amount of bytes from this packet and
     * write them on [dst] starting from [position].
     */
    fun pdata(dst: ByteArray, position: Int, length: Int) {
        for (i in 0 until length) {
            dst[position + i] = data[this.position++]
        }
    }

    /**
     * Get and put the next bytes from this packet onto [dst].
     * The amount of bytes being transferred is equal to the
     * size of [dst].
     */
    fun pdata(dst: ByteArray) = pdata(dst, 0, dst.size)

    /**
     * Get the next [length] amount of bytes in inverse order, as a
     * signed short altA and put them into [dst] starting from [position].
     */
    fun pdataLE_altA(dst: ByteArray, position: Int, length: Int) {
        for (i in position + length - 1 downTo position) {
            dst[i] = (data[this.position++] - 128).toByte()
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
            p1(src[srcOffset + i].toInt())
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
     * Set the upcoming [numBits] bits in our buffer as [value].
     * [setBitMode] must be enabled before calling this function.
     */
    fun pbits(value: Int, numBits: Int) {
        if(bitPosition == DISABLED_BIT_POSITION) {
            error("Bit mode must be enabled")
        } else if (numBits !in 1..Int.SIZE_BITS) {
            error("Number of bits must be between [1-32]")
        }

        var bytePosition = bitPosition shr 3
        var bitOffset = Byte.SIZE_BITS - (bitPosition and 0x7)

        var numberOfBits = numBits
        bitPosition += numberOfBits

        var spaceRequired = bytePosition - position + 1
        spaceRequired += (numberOfBits + 7) shr 3

        if(writableBytes < spaceRequired) {
            error("Not enough writable bytes to continue (required=$spaceRequired, left=$writableBytes)")
        }

        while (numberOfBits > bitOffset) {
            var tmp = data[bytePosition].toInt()
            tmp = tmp and BitConstants.BIT_MASK[bitOffset].inv()
            tmp = tmp or (value shr numberOfBits - bitOffset and BitConstants.BIT_MASK[bitOffset])
            data[bytePosition++] = tmp.toByte()
            numberOfBits -= bitOffset
            bitOffset = Byte.SIZE_BITS
        }

        var tmp = data[bytePosition].toInt()
        if (numberOfBits == bitOffset) {
            tmp = tmp and BitConstants.BIT_MASK[bitOffset].inv()
            tmp = tmp or (value and BitConstants.BIT_MASK[bitOffset])
        } else {
            tmp = tmp and (BitConstants.BIT_MASK[numberOfBits] shl bitOffset - numberOfBits).inv()
            tmp = tmp or (value and BitConstants.BIT_MASK[numberOfBits] shl bitOffset - numberOfBits)
        }
        data[bytePosition] = tmp.toByte()
    }

    /**
     * Toggle bit-access mode.
     */
    fun setBitMode(bitMode: Boolean): Unit = if (bitMode) {
        if (bitPosition != DISABLED_BIT_POSITION) { error("Bit mode is already enabled") }
        bitPosition = position * 8
    } else {
        if (bitPosition == DISABLED_BIT_POSITION) { error("Bit mode is already disabled") }
        position = (bitPosition + 7) / 8
        bitPosition = DISABLED_BIT_POSITION
    }

    companion object {

        private const val DISABLED_BIT_POSITION = -1

        /**
         * Create a [WriteOnlyPacket] with [data] as its backing array.
         */
        fun from(data: ByteArray): WriteOnlyPacket = WriteOnlyPacket(data)
    }
}
