package org.rsmod.plugins.info.player.model.bitcode

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.plugins.info.player.buffer.BitBuffer
import org.rsmod.plugins.info.player.model.bitcode.I26BitCode.Companion.VALUE_BITS
import java.nio.ByteBuffer
import java.util.stream.Stream

class I26BitCodeTest {

    @Test
    fun testBitBufferTransmission() {
        ByteBuffer.allocate(3).let { buf ->
            BitBuffer(buf).use { bitBuf ->
                bitBuf.putBits(len = 8, value = 250)
                bitBuf.putBoolean(true)
                bitBuf.putBits(len = 5, value = 1)
                bitBuf.putBits(len = 10, value = 1023)

                val bitCount = bitBuf.position()
                val value = bitBuf.flip().getBits(bitCount)
                val code = I26BitCode(value, bitCount)
                check(code.value == value)
                check(code.bitCount == bitCount)

                bitBuf.setBits(index = 0, len = code.bitCount, value = code.value)
                bitBuf.flip()

                Assertions.assertEquals(250, bitBuf.getBits(8))
                Assertions.assertTrue(bitBuf.getBoolean())
                Assertions.assertEquals(1, bitBuf.getBits(5))
                Assertions.assertEquals(1023, bitBuf.getBits(10))
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BitCodeProvider::class)
    fun testConstructBitCode(value: Int, bitCount: Int) {
        val bitCode = I26BitCode(value, bitCount)
        require(bitCount <= VALUE_BITS)
        require(value.countOneBits() <= VALUE_BITS)
        Assertions.assertEquals(value, bitCode.value)
        Assertions.assertEquals(bitCount, bitCode.bitCount)
    }

    private object BitCodeProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(0, 0),
                Arguments.of((1 shl 8) - 1, 8),
                Arguments.of((1 shl 16) - 1, 16),
                Arguments.of((1 shl 20) - 1, 20),
                Arguments.of((1 shl 23) - 1, 23),
                Arguments.of((1 shl 24) - 1, 24),
                Arguments.of((1 shl 26) - 1, 26)
            )
        }
    }
}
