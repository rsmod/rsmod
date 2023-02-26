package org.rsmod.plugins.api.cache.type.varbit

import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeLoader.Companion.readType
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypePacker.writeType
import java.util.stream.Stream

class VarbitTypeCodecTest {

    @ParameterizedTest
    @ArgumentsSource(VarbitTypeProvider::class)
    fun testCodec(type: VarbitType) {
        val buf = Unpooled.buffer(32).apply { writeType(this, type, isJs5 = false) }
        val decoded = readType(buf, type.id)
        assertEquals(type, decoded)
    }

    @Test
    fun testJs5Codec() {
        val type = VarbitTypeBuilder().apply {
            id = 1000
            name = "test_varbit_1000"
            varp = 200
            lsb = 0
            msb = 1
            transmit = true
        }.build()
        val buf = Unpooled.buffer(32).apply { writeType(this, type, isJs5 = true) }
        val decoded = readType(buf, type.id)
        val copyNonJs5 = decoded.copy(name = type.name, transmit = type.transmit)
        assertNotEquals(type.name, decoded.name)
        assertNotEquals(type.transmit, decoded.transmit)
        assertEquals(type, copyNonJs5)
    }

    private object VarbitTypeProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    VarbitTypeBuilder().apply {
                        id = 1000
                        name = "test_varbit_1000"
                        varp = 200
                        lsb = 0
                        msb = 1
                    }.build()
                ),
                Arguments.of(
                    VarbitTypeBuilder().apply {
                        id = 2500
                        name = "test_varbit_2500"
                        varp = 150
                        lsb = 1
                        msb = 5
                    }.build()
                ),
                Arguments.of(
                    VarbitTypeBuilder().apply {
                        id = 500
                        name = "test_varbit_500"
                        varp = 25
                        lsb = 5
                        msb = 10
                    }.build()
                )
            )
        }
    }
}
