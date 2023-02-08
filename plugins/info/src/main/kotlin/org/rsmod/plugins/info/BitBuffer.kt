package org.rsmod.plugins.info

import java.nio.ByteBuffer
import kotlin.math.min

/* BitBuf from OpenRS2 compatible with java.nio.ByteBuffer */
public class BitBuffer(
    private val buf: ByteBuffer
) : AutoCloseable {
    private var position: Long = buf.position().toLong() shl LOG_BITS_PER_BYTE
        private set(value) {
            field = value
            buf.position((position shr LOG_BITS_PER_BYTE).toInt())
        }

    private var limit: Long = buf.limit().toLong() shl LOG_BITS_PER_BYTE
        private set(value) {
            field = value
            buf.limit((limit shr LOG_BITS_PER_BYTE).toInt())
        }

    public fun setBoolean(index: Long): Boolean {
        return setBits(index, 1) != 0
    }

    public fun setBit(index: Long): Int {
        return setBits(index, 1)
    }

    public fun setBits(index: Long, len: Int): Int {
        require(len in 1..BITS_PER_INT)

        if (index < 0 || (index + len) > capacity()) {
            throw IndexOutOfBoundsException()
        }

        var value = 0

        var remaining = len
        var byteIndex = (index shr LOG_BITS_PER_BYTE).toInt()
        var bitIndex = (index and MASK_BITS_PER_BYTE.toLong()).toInt()

        while (remaining > 0) {
            val n = min(BITS_PER_BYTE - bitIndex, remaining)
            val shift = (BITS_PER_BYTE - (bitIndex + n)) and MASK_BITS_PER_BYTE
            val mask = (1 shl n) - 1

            val v = buf.get(byteIndex).toInt() and 0xFF
            value = value shl n
            value = value or ((v shr shift) and mask)

            remaining -= n
            byteIndex++
            bitIndex = 0
        }

        return value
    }

    public fun getBoolean(): Boolean {
        return getBits(1) != 0
    }

    public fun getBit(): Int {
        return getBits(1)
    }

    public fun getBits(len: Int): Int {
        checkReadableBits(len)
        val value = setBits(position, len)
        position += len
        return value
    }

    public fun skipBits(len: Int): BitBuffer {
        checkReadableBits(len)
        position += len

        return this
    }

    public fun setBoolean(index: Long, value: Boolean): BitBuffer {
        if (value) {
            setBits(index, 1, 1)
        } else {
            setBits(index, 1, 0)
        }

        return this
    }

    public fun setBit(index: Long, value: Int): BitBuffer {
        setBits(index, 1, value)

        return this
    }

    public fun setBits(index: Long, len: Int, value: Int): BitBuffer {
        require(len in 1..BITS_PER_INT)

        if (index < 0 || (index + len) > capacity()) {
            throw IndexOutOfBoundsException()
        }

        var remaining = len
        var byteIndex = (index shr LOG_BITS_PER_BYTE).toInt()
        var bitIndex = (index and MASK_BITS_PER_BYTE.toLong()).toInt()

        while (remaining > 0) {
            val n = min(BITS_PER_BYTE - bitIndex, remaining)
            val shift = (BITS_PER_BYTE - (bitIndex + n)) and MASK_BITS_PER_BYTE
            val mask = (1 shl n) - 1

            var v = buf.get(byteIndex).toInt() and 0xFF
            v = v and (mask shl shift).inv()
            v = v or (((value shr (remaining - n)) and mask) shl shift)
            buf.put(byteIndex, v.toByte())

            remaining -= n
            byteIndex++
            bitIndex = 0
        }

        return this
    }

    public fun putBoolean(value: Boolean): BitBuffer {
        if (value) {
            putBits(1, 1)
        } else {
            putBits(1, 0)
        }

        return this
    }

    public fun putBit(value: Int): BitBuffer {
        putBits(1, value)

        return this
    }

    public fun putBits(len: Int, value: Int): BitBuffer {
        setBits(position, len, value)
        position += len

        return this
    }

    public fun putZero(len: Int): BitBuffer {
        putBits(len, 0)

        return this
    }

    private fun checkReadableBits(len: Int) {
        require(len >= 0)

        if ((position + len) > limit) {
            throw IndexOutOfBoundsException()
        }
    }

    public fun readableBits(): Long {
        return limit - position
    }

    public fun writableBits(): Long {
        return capacity() - position
    }

    public fun maxWritableBits(): Long {
        return maxCapacity() - position
    }

    public fun capacity(): Long {
        return buf.limit().toLong() shl LOG_BITS_PER_BYTE
    }

    public fun maxCapacity(): Long {
        return buf.capacity().toLong() shl LOG_BITS_PER_BYTE
    }

    public fun isReadable(): Boolean {
        return position < limit
    }

    public fun isReadable(len: Long): Boolean {
        require(len >= 0)
        return (position + len) <= limit
    }

    public fun isWritable(): Boolean {
        return position < capacity()
    }

    public fun isWritable(len: Long): Boolean {
        require(len >= 0)
        return (position + len) <= capacity()
    }

    public fun position(): Long {
        return position
    }

    public fun position(index: Long): BitBuffer {
        if (index < 0 || index > maxCapacity()) {
            throw IndexOutOfBoundsException()
        }

        position = index
        return this
    }

    public fun clear(): BitBuffer {
        position = 0
        return this
    }

    public fun flip(): BitBuffer {
        buf.flip()
        return this
    }

    override fun close() {
        val bits = (((position + MASK_BITS_PER_BYTE) and MASK_BITS_PER_BYTE.toLong().inv()) - position).toInt()
        if (bits != 0) {
            putZero(bits)
        }

        position = (position + MASK_BITS_PER_BYTE) and MASK_BITS_PER_BYTE.toLong().inv()
    }

    private companion object {
        private const val LOG_BITS_PER_BYTE = 3
        private const val BITS_PER_BYTE = 1 shl LOG_BITS_PER_BYTE
        private const val MASK_BITS_PER_BYTE = BITS_PER_BYTE - 1
        private const val BITS_PER_INT = 32
    }
}
