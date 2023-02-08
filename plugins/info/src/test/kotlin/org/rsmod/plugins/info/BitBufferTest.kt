package org.rsmod.plugins.info

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.ByteBuffer

/* removed testExpand + testCapacity */
/* replaced testReader/WriterIndex with testPosition */
class BitBufferTest {

    @Test
    fun testClear() {
        ByteBuffer.allocate(1024).let { buf ->
            buf.putInt(1234567890)

            BitBuffer(buf).use { bitBuf ->
                assertEquals(32, bitBuf.position())
                bitBuf.clear()
                assertEquals(0, bitBuf.position())
            }

            assertEquals(0, buf.position())
        }
    }

    @Test
    fun testReadAlignment() {
        ByteBuffer.allocate(1024).let { buf ->
            buf.put(0xFF.toByte())
            buf.flip()

            BitBuffer(buf).use { bitBuf ->
                assertEquals(0x7F, bitBuf.getBits(7))
                assertEquals(7, bitBuf.position())
            }

            assertEquals(1, buf.position())
        }
    }

    @Test
    fun testWriteAlignment() {
        ByteBuffer.allocate(1024).let { buf ->
            buf.put(0, 0xFF.toByte())

            BitBuffer(buf).use { bitBuf ->
                assertEquals(0, bitBuf.position())
                bitBuf.putBits(7, 0x7F)
                assertEquals(7, bitBuf.position())
            }

            assertEquals(1, buf.position())
            assertEquals(0xFE, buf.get(0).toInt() and 0xFF)
        }
    }

    @Test
    fun testSkipBits() {
        ByteBuffer.allocate(1024).let { buf ->
            buf.putInt(1234567890)
            buf.flip()

            BitBuffer(buf).use { bitBuf ->
                assertThrows<IllegalArgumentException> {
                    bitBuf.skipBits(-1)
                }

                assertEquals(0, bitBuf.position())
                assertEquals(0, buf.position())

                bitBuf.skipBits(0)

                assertEquals(0, bitBuf.position())
                assertEquals(0, buf.position())

                bitBuf.skipBits(7)

                assertEquals(7, bitBuf.position())
                assertEquals(0, buf.position())

                bitBuf.skipBits(2)

                assertEquals(9, bitBuf.position())
                assertEquals(1, buf.position())

                bitBuf.skipBits(23)

                assertEquals(32, bitBuf.position())
                assertEquals(4, buf.position())

                bitBuf.skipBits(0)

                assertEquals(32, bitBuf.position())
                assertEquals(4, buf.position())

                assertThrows<IndexOutOfBoundsException> {
                    bitBuf.skipBits(1)
                }
            }

            assertEquals(4, buf.position())
        }
    }

    @Test
    fun testGetBits() {
        ByteBuffer.allocate(4).let { buf ->
            buf.putInt(1234567890)

            BitBuffer(buf).use { bitBuf ->
                assertEquals(1234567890, bitBuf.setBits(0, 32))

                assertEquals(0b0100100110010110, bitBuf.setBits(0, 16))
                assertEquals(0b0000001011010010, bitBuf.setBits(16, 16))

                assertEquals(0b01001001, bitBuf.setBits(0, 8))
                assertEquals(0b10010110, bitBuf.setBits(8, 8))
                assertEquals(0b00000010, bitBuf.setBits(16, 8))
                assertEquals(0b11010010, bitBuf.setBits(24, 8))

                assertEquals(0b10011001, bitBuf.setBits(4, 8))

                assertEquals(0b100110010110, bitBuf.setBits(4, 12))

                assertEquals(0, bitBuf.setBits(0, 1))
                assertEquals(0, bitBuf.setBit(0))
                assertEquals(false, bitBuf.setBoolean(0))

                assertEquals(1, bitBuf.setBits(1, 1))
                assertEquals(1, bitBuf.setBit(1))
                assertEquals(true, bitBuf.setBoolean(1))

                assertThrows<IndexOutOfBoundsException> {
                    bitBuf.setBits(-1, 1)
                }

                assertThrows<IndexOutOfBoundsException> {
                    bitBuf.setBits(32, 1)
                }

                assertThrows<IllegalArgumentException> {
                    bitBuf.setBits(0, 0)
                }

                assertThrows<IllegalArgumentException> {
                    bitBuf.putBits(0, 33)
                }
            }
        }
    }

    @Test
    fun testSetBits() {
        ByteBuffer.allocate(4).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                bitBuf.setBits(0, 32, 1234567890)
            }

            assertEquals(1234567890, buf.getInt(0))
        }

        ByteBuffer.allocate(4).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                bitBuf.setBits(0, 16, 0b0100100110010110)
                bitBuf.setBits(16, 16, 0b0000001011010010)
            }

            assertEquals(1234567890, buf.getInt(0))
        }

        ByteBuffer.allocate(4).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                bitBuf.setBits(0, 8, 0b01001001)
                bitBuf.setBits(8, 8, 0b10010110)
                bitBuf.setBits(16, 8, 0b00000010)
                bitBuf.setBits(24, 8, 0b11010010)
            }

            assertEquals(1234567890, buf.getInt(0))
        }

        ByteBuffer.allocate(4).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                bitBuf.setBits(0, 4, 0b0100)
                bitBuf.setBits(4, 8, 0b10011001)
                bitBuf.setBits(12, 4, 0b0110)

                bitBuf.setBits(16, 16, 0b0000001011000101)

                bitBuf.setBoolean(27, true)
                bitBuf.setBits(29, 1, 0)
                bitBuf.setBit(30, 1)
                bitBuf.setBoolean(31, false)
            }

            assertEquals(1234567890, buf.getInt(0))
        }

        ByteBuffer.allocate(4).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                assertThrows<IndexOutOfBoundsException> {
                    bitBuf.setBits(-1, 1, 0)
                }

                assertThrows<IndexOutOfBoundsException> {
                    bitBuf.setBits(32, 1, 0)
                }

                assertThrows<IllegalArgumentException> {
                    bitBuf.setBits(0, 0, 0)
                }

                assertThrows<IllegalArgumentException> {
                    bitBuf.setBits(0, 33, 0)
                }
            }
        }
    }

    @Test
    fun testRead() {
        ByteBuffer.allocate(4).let { buf ->
            buf.putInt(1234567890)
            buf.flip()

            BitBuffer(buf).use { bitBuf ->
                assertEquals(0, bitBuf.position())
                assertEquals(32, bitBuf.readableBits())

                assertTrue(bitBuf.isReadable())
                assertTrue(bitBuf.isReadable(0))
                assertTrue(bitBuf.isReadable(1))
                assertTrue(bitBuf.isReadable(31))
                assertTrue(bitBuf.isReadable(32))
                assertFalse(bitBuf.isReadable(33))

                assertEquals(1234567890, bitBuf.getBits(32))
                assertEquals(32, bitBuf.position())
                assertEquals(0, bitBuf.readableBits())

                assertFalse(bitBuf.isReadable())
                assertTrue(bitBuf.isReadable(0))
                assertFalse(bitBuf.isReadable(1))
            }

            assertEquals(4, buf.position())
        }

        ByteBuffer.allocate(4).let { buf ->
            buf.putInt(1234567890)
            buf.flip()

            BitBuffer(buf).use { bitBuf ->
                assertEquals(0, bitBuf.position())
                assertEquals(32, bitBuf.readableBits())

                assertEquals(0b0100100110010110, bitBuf.getBits(16))

                assertEquals(16, bitBuf.position())
                assertEquals(16, bitBuf.readableBits())

                assertEquals(0b0000001011010010, bitBuf.getBits(16))

                assertEquals(32, bitBuf.position())
                assertEquals(0, bitBuf.readableBits())
            }

            assertEquals(4, buf.position())
        }

        ByteBuffer.allocate(4).let { buf ->
            buf.putInt(1234567890)
            buf.flip()

            BitBuffer(buf).use { bitBuf ->
                assertEquals(0, bitBuf.position())
                assertEquals(32, bitBuf.readableBits())

                assertEquals(0b01001001, bitBuf.getBits(8))

                assertEquals(8, bitBuf.position())
                assertEquals(24, bitBuf.readableBits())

                assertEquals(0b10010110, bitBuf.getBits(8))

                assertEquals(16, bitBuf.position())
                assertEquals(16, bitBuf.readableBits())

                assertEquals(0b00000010, bitBuf.getBits(8))

                assertEquals(24, bitBuf.position())
                assertEquals(8, bitBuf.readableBits())

                assertEquals(0b11010010, bitBuf.getBits(8))

                assertEquals(32, bitBuf.position())
                assertEquals(0, bitBuf.readableBits())
            }

            assertEquals(4, buf.position())
        }

        ByteBuffer.allocate(1).let { buf ->
            buf.put(0b01010100)
            buf.flip()

            BitBuffer(buf).use { bitBuf ->
                assertEquals(0, bitBuf.position())
                assertEquals(8, bitBuf.readableBits())

                assertEquals(0, bitBuf.getBits(1))

                assertEquals(1, bitBuf.position())
                assertEquals(7, bitBuf.readableBits())

                assertEquals(1, bitBuf.getBits(1))

                assertEquals(2, bitBuf.position())
                assertEquals(6, bitBuf.readableBits())

                assertEquals(0, bitBuf.getBit())

                assertEquals(3, bitBuf.position())
                assertEquals(5, bitBuf.readableBits())

                assertEquals(1, bitBuf.getBit())

                assertEquals(4, bitBuf.position())
                assertEquals(4, bitBuf.readableBits())

                assertFalse(bitBuf.getBoolean())

                assertEquals(5, bitBuf.position())
                assertEquals(3, bitBuf.readableBits())

                assertTrue(bitBuf.getBoolean())

                assertEquals(6, bitBuf.position())
                assertEquals(2, bitBuf.readableBits())

                bitBuf.skipBits(2)

                assertEquals(8, bitBuf.position())
                assertEquals(0, bitBuf.readableBits())
            }

            assertEquals(1, buf.position())
        }

        ByteBuffer.allocate(5).let { buf ->
            buf.position(5)
            buf.rewind()

            BitBuffer(buf).use { bitBuf ->
                assertThrows<IllegalArgumentException> {
                    bitBuf.getBits(0)
                }

                assertThrows<IllegalArgumentException> {
                    bitBuf.getBits(33)
                }
            }
        }
    }

    @Test
    fun testWrite() {
        ByteBuffer.allocate(4).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                assertEquals(0, bitBuf.position())
                assertEquals(32, bitBuf.writableBits())

                assertTrue(bitBuf.isWritable())
                assertTrue(bitBuf.isWritable(0))
                assertTrue(bitBuf.isWritable(1))
                assertTrue(bitBuf.isWritable(31))
                assertTrue(bitBuf.isWritable(32))
                assertFalse(bitBuf.isWritable(33))

                bitBuf.putBits(32, 1234567890)

                assertEquals(32, bitBuf.position())
                assertEquals(0, bitBuf.writableBits())

                assertFalse(bitBuf.isWritable())
                assertTrue(bitBuf.isWritable(0))
                assertFalse(bitBuf.isWritable(1))
            }

            assertEquals(1234567890, buf.getInt(0))
            assertEquals(4, buf.position())
        }

        ByteBuffer.allocate(4).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                assertEquals(0, bitBuf.position())
                assertEquals(32, bitBuf.writableBits())

                bitBuf.putBits(16, 0b0100100110010110)

                assertEquals(16, bitBuf.position())
                assertEquals(16, bitBuf.writableBits())

                bitBuf.putBits(16, 0b0000001011010010)

                assertEquals(32, bitBuf.position())
                assertEquals(0, bitBuf.writableBits())
            }

            assertEquals(1234567890, buf.getInt(0))
            assertEquals(4, buf.position())
        }

        ByteBuffer.allocate(4).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                assertEquals(0, bitBuf.position())
                assertEquals(32, bitBuf.writableBits())

                bitBuf.putBits(8, 0b01001001)

                assertEquals(8, bitBuf.position())
                assertEquals(24, bitBuf.writableBits())

                bitBuf.putBits(8, 0b10010110)

                assertEquals(16, bitBuf.position())
                assertEquals(16, bitBuf.writableBits())

                bitBuf.putBits(8, 0b00000010)

                assertEquals(24, bitBuf.position())
                assertEquals(8, bitBuf.writableBits())

                bitBuf.putBits(8, 0b11010010)

                assertEquals(32, bitBuf.position())
                assertEquals(0, bitBuf.writableBits())
            }

            assertEquals(1234567890, buf.getInt(0))
            assertEquals(4, buf.position())
        }

        ByteBuffer.allocate(1).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                assertEquals(0, bitBuf.position())
                assertEquals(8, bitBuf.writableBits())

                bitBuf.putBits(1, 0)

                assertEquals(1, bitBuf.position())
                assertEquals(7, bitBuf.writableBits())

                bitBuf.putBits(1, 1)

                assertEquals(2, bitBuf.position())
                assertEquals(6, bitBuf.writableBits())

                bitBuf.putBit(0)

                assertEquals(3, bitBuf.position())
                assertEquals(5, bitBuf.writableBits())

                bitBuf.putBit(1)

                assertEquals(4, bitBuf.position())
                assertEquals(4, bitBuf.writableBits())

                bitBuf.putBoolean(false)

                assertEquals(5, bitBuf.position())
                assertEquals(3, bitBuf.writableBits())

                bitBuf.putBoolean(true)

                assertEquals(6, bitBuf.position())
                assertEquals(2, bitBuf.writableBits())

                bitBuf.putZero(2)

                assertEquals(8, bitBuf.position())
                assertEquals(0, bitBuf.writableBits())
            }

            assertEquals(0b01010100, buf.get(0).toInt() and 0xFF)
            assertEquals(1, buf.position())
        }

        ByteBuffer.allocate(5).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                assertThrows<IllegalArgumentException> {
                    bitBuf.putBits(0, 0)
                }

                assertThrows<IllegalArgumentException> {
                    bitBuf.putBits(33, 0)
                }

                assertThrows<IllegalArgumentException> {
                    bitBuf.isWritable(-1)
                }
            }
        }
    }

    @Test
    fun testPosition() {
        ByteBuffer.allocate(2).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                bitBuf.position(10)

                assertThrows<IndexOutOfBoundsException> {
                    bitBuf.position(-1)
                }

                bitBuf.position(1)
                assertEquals(1, bitBuf.position())

                bitBuf.position(8)
                assertEquals(8, bitBuf.position())

                bitBuf.position(9)
                assertEquals(9, bitBuf.position())

                bitBuf.position(10)
                assertEquals(10, bitBuf.position())

                assertThrows<IndexOutOfBoundsException> {
                    bitBuf.position(17)
                }
            }
        }
    }
}
