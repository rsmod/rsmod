package org.rsmod.plugins.api.cache.type.enums

import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.plugins.api.cache.type.enums.EnumTypeLoader.Companion.readType
import org.rsmod.plugins.api.cache.type.enums.EnumTypePacker.writeType
import org.rsmod.plugins.api.cache.type.literal.CacheTypeLiteral
import java.util.stream.Stream

class EnumTypeBufferTest {

    @ParameterizedTest
    @ArgumentsSource(EnumTypeProvider::class)
    fun testCodec(isJs5: Boolean, type: EnumType<Any, Any>) {
        val buf = Unpooled.buffer(128).apply { writeType(this, type, isJs5) }
        val decoded = readType(buf, type.id)
        assertEquals(type.id, decoded.id)
        assertEquals(type.keyType, decoded.keyType)
        assertEquals(type.valType, decoded.valType)
        assertEquals(type.default, decoded.default)
        assertEquals(type.properties, decoded.properties)
        if (!isJs5) {
            assertEquals(type.transmit, decoded.transmit)
        } else {
            assertFalse(decoded.transmit)
        }
    }

    private companion object {

        private const val ITEM_4151 = 4151
        private const val ITEM_2 = 2
        private const val OBJECT_1 = 1
        private const val OBJECT_40 = 40
    }

    private object EnumTypeProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    true,
                    EnumTypeBuilder().apply {
                        id = 10
                        keyType = CacheTypeLiteral.Item.char
                        valType = CacheTypeLiteral.Integer.char
                        defaultInt = 10
                        intValues[ITEM_4151] = 1
                        transmit = true
                    }.build()
                ),
                Arguments.of(
                    false,
                    EnumTypeBuilder().apply {
                        id = 10
                        keyType = CacheTypeLiteral.Item.char
                        valType = CacheTypeLiteral.Integer.char
                        defaultInt = 10
                        intValues[ITEM_4151] = 1
                        transmit = true
                    }.build()
                ),
                Arguments.of(
                    false,
                    EnumTypeBuilder().apply {
                        id = 512
                        keyType = CacheTypeLiteral.Object.char
                        valType = CacheTypeLiteral.Item.char
                        defaultInt = OBJECT_1
                        intValues[OBJECT_1] = ITEM_2
                        intValues[OBJECT_40] = ITEM_4151
                        transmit = true
                    }.build()
                ),
                Arguments.of(
                    false,
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
