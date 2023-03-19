package org.rsmod.plugins.api.prot

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BufferExtensionsTest {

    @Test
    fun testByteAlt1() {
        val value = 132
        Unpooled.buffer(Byte.SIZE_BYTES).writeByteAlt1(value).apply {
            assertEquals(value, readByte() - 128)
            assertEquals(value, readByteAlt1().toInt())
            assertEquals(value, readUnsignedByteAlt1().toInt())
        }
    }

    @Test
    fun testByteAlt2() {
        val value = 24
        Unpooled.buffer(Byte.SIZE_BYTES).writeByteAlt2(value).apply {
            assertEquals(value, 0 - readByte())
            assertEquals(value, readByteAlt2().toInt())
            assertEquals(value, readUnsignedByteAlt2().toInt())
        }
    }

    @Test
    fun testByteAlt3() {
        val value = -126
        Unpooled.buffer(Byte.SIZE_BYTES).writeByteAlt3(value).apply {
            assertEquals(value, 128 - readByte())
            assertEquals(value, readByteAlt3().toInt())
            assertEquals(value, readUnsignedByteAlt3().toInt())
        }
    }

    @Test
    fun testShortAlt1() {
        val value = 15400
        Unpooled.buffer(Short.SIZE_BYTES).writeShortAlt1(value).apply {
            assertEquals(value, readByte().toInt() or (readByte().toInt() shl 8))
            assertEquals(value, readShortAlt1().toInt())
            assertEquals(value, readUnsignedShortAlt1())
        }
    }

    @Test
    fun testShortAlt2() {
        val value = 64435
        Unpooled.buffer(Short.SIZE_BYTES).writeShortAlt2(value).apply {
            assertEquals(value, (readByte().toInt() shl 8) or (readByte() - 128))
            assertEquals(value, readShortAlt2().toInt())
            assertEquals(value, readUnsignedShortAlt2())
        }
    }

    @Test
    fun testShortAlt3() {
        val value = -6352
        Unpooled.buffer(Short.SIZE_BYTES).writeShortAlt3(value).apply {
            assertEquals(value, (readByte() - 128) or (readByte().toInt() shl 8))
            assertEquals(value, readShortAlt3().toInt())
            assertEquals(value, readUnsignedShortAlt3())
        }
    }

    @Test
    fun testMediumAlt1() {
        val value = 10_777_216
        Unpooled.buffer(3).writeMediumAlt1(value).apply {
            assertEquals(value, readByte().toInt() or (readByte().toInt() shl 8) or (readByte().toInt() shl 16))
            assertEquals(value, readMediumAlt1())
            assertEquals(value, readUnsignedMediumAlt1())
        }
    }

    @Test
    fun testIntAlt1() {
        val value = 140_500_000
        Unpooled.buffer(Int.SIZE_BYTES).writeIntAlt1(value).apply {
            assertEquals(
                value,
                readByte().toInt() or
                    (readByte().toInt() shl 8) or
                    (readByte().toInt() shl 16) or
                    (readByte().toInt() shl 24)
            )
            assertEquals(value, readIntAlt1())
        }
    }

    @Test
    fun testIntAlt2() {
        val value = 2_140_500_000
        Unpooled.buffer(Int.SIZE_BYTES).writeIntAlt2(value).apply {
            assertEquals(
                value,
                (readByte().toInt() shl 8) or
                    readByte().toInt() or
                    (readByte().toInt() shl 24) or
                    (readByte().toInt() shl 16)
            )
            assertEquals(value, readIntAlt2())
        }
    }

    @Test
    fun testIntAlt3_() {
        val value = -500_000_000
        Unpooled.buffer(Int.SIZE_BYTES).writeIntAlt3_(value).apply {
            assertEquals(
                value,
                (readByte().toInt() shl 16) or
                    (readByte().toInt() shl 24) or
                    readByte().toInt() or
                    (readByte().toInt() shl 8)
            )
            assertEquals(value, readIntAlt3_())
        }
    }

    private fun ByteBuf.assertEquals(expected: Int, actual: Int) {
        Assertions.assertEquals(expected and 0xFF, actual and 0xFF)
        resetReaderIndex()
    }
}
