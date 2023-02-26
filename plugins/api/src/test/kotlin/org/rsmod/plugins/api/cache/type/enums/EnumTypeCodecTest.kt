package org.rsmod.plugins.api.cache.type.enums

import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.plugins.api.cache.type.enums.EnumTypeLoader.Companion.readType
import org.rsmod.plugins.api.cache.type.enums.EnumTypePacker.writeType
import org.rsmod.plugins.api.cache.type.literal.CacheTypeLiteral
import java.util.stream.Stream

class EnumTypeCodecTest {

    @ParameterizedTest
    @ArgumentsSource(EnumTypeProvider::class)
    fun testCodec(type: EnumType<Any, Any>) {
        val buf = Unpooled.buffer(32).apply { writeType(this, type, isJs5 = false) }
        val decoded = readType(buf, type.id)
        assertEquals(type, decoded)
    }

    @Test
    fun testJs5Codec() {
        val type = EnumTypeBuilder().apply {
            id = 5040
            name = "test_enum_5040"
            keyType = CacheTypeLiteral.Item.char
            valType = CacheTypeLiteral.Integer.char
            defaultInt = -1
            intValues[11802] = 1
            transmit = true
        }.build()
        val buf = Unpooled.buffer(32).apply { writeType(this, type, isJs5 = true) }
        val decoded = readType(buf, type.id)
        val copyNonJs5 = decoded.copy(name = type.name, transmit = type.transmit)
        assertNotEquals(type.name, decoded.name)
        assertNotEquals(type.transmit, decoded.transmit)
        assertEquals(type, copyNonJs5)
    }

    private object EnumTypeProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    EnumTypeBuilder().apply {
                        id = 10
                        keyType = CacheTypeLiteral.Item.char
                        valType = CacheTypeLiteral.Integer.char
                        defaultInt = 10
                        intValues[4151] = 1
                        transmit = true
                    }.build()
                ),
                Arguments.of(
                    EnumTypeBuilder().apply {
                        id = 512
                        keyType = CacheTypeLiteral.Object.char
                        valType = CacheTypeLiteral.Item.char
                        defaultInt = 1
                        intValues[1] = 2
                        intValues[40] = 4151
                        transmit = true
                    }.build()
                ),
                Arguments.of(
                    EnumTypeBuilder().apply {
                        id = 1000
                        keyType = CacheTypeLiteral.Integer.char
                        valType = CacheTypeLiteral.Boolean.char
                        defaultInt = 1
                        intValues[0] = 0
                        transmit = true
                    }.build()
                )
            )
        }
    }
}
