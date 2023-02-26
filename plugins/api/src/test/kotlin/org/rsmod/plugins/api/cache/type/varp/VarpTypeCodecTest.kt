package org.rsmod.plugins.api.cache.type.varp

import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.plugins.api.cache.type.varp.VarpTypeLoader.Companion.readType
import org.rsmod.plugins.api.cache.type.varp.VarpTypePacker.writeType
import java.util.stream.Stream

class VarpTypeCodecTest {

    @ParameterizedTest
    @ArgumentsSource(VarpTypeProvider::class)
    fun testCodec(type: VarpType) {
        val buf = Unpooled.buffer(32).apply { writeType(this, type, isJs5 = false) }
        val decoded = readType(buf, type.id)
        assertEquals(type, decoded)
    }

    @Test
    fun testJs5Codec() {
        val type = VarpTypeBuilder().apply {
            id = 1000
            name = "test_varp_1000"
            clientCode = 2
            transmit = true
        }.build()
        val buf = Unpooled.buffer(32).apply { writeType(this, type, isJs5 = true) }
        val decoded = readType(buf, type.id)
        val copyNonJs5 = decoded.copy(name = type.name, transmit = type.transmit)
        assertNotEquals(type.name, decoded.name)
        assertNotEquals(type.transmit, decoded.transmit)
        assertEquals(type, copyNonJs5)
    }

    private object VarpTypeProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    VarpTypeBuilder().apply {
                        id = 1000
                        name = "test_varp_1000"
                        clientCode = 2
                        transmit = true
                    }.build()
                ),
                Arguments.of(
                    VarpTypeBuilder().apply {
                        id = 400
                        name = "test_varp_400"
                        transmit = true
                    }.build()
                ),
                Arguments.of(
                    VarpTypeBuilder().apply {
                        id = 200
                        name = "test_varp_200"
                        transmit = true
                    }.build()
                )
            )
        }
    }
}
