package org.rsmod.plugins.cache.config.param

import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.map.Coordinates
import org.rsmod.plugins.cache.config.param.ParamTypeLoader.readType
import org.rsmod.plugins.cache.config.param.ParamTypePacker.writeType
import org.rsmod.plugins.cache.literal.CacheTypeLiteral
import java.util.stream.Stream

class ParamTypeCodecTest {

    @ParameterizedTest
    @ArgumentsSource(ParamTypeProvider::class)
    fun testCodec(type: ParamType<*>) {
        val buf = Unpooled.buffer(32).apply { writeType(this, type, isJs5 = false) }
        val decoded = readType(buf, type.id)
        assertEquals(type, decoded)
    }

    @Test
    fun testJs5Codec() {
        val type = ParamTypeBuilder().apply {
            id = 5000
            name = "test_param_5000"
            typeChar = CacheTypeLiteral.Item.char
            defaultInt = 4151
            transmit = true
        }.build()
        val buf = Unpooled.buffer(32).apply { writeType(this, type, isJs5 = true) }
        val decoded = readType(buf, type.id)
        val copyNonJs5 = decoded.copy(name = type.name, transmit = type.transmit)
        assertNotEquals(type.name, decoded.name)
        assertNotEquals(type.transmit, decoded.transmit)
        assertEquals(type, copyNonJs5)
    }

    private object ParamTypeProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    ParamTypeBuilder().apply {
                        id = 10
                        name = "test_param_10"
                        typeChar = CacheTypeLiteral.Animation.char
                        defaultInt = 45
                    }.build()
                ),
                Arguments.of(
                    ParamTypeBuilder().apply {
                        id = 50
                        name = "test_param_50"
                        typeChar = CacheTypeLiteral.Graphic.char
                        defaultInt = 320
                    }.build()
                ),
                Arguments.of(
                    ParamTypeBuilder().apply {
                        id = 1220
                        name = "test_param_1220"
                        typeChar = CacheTypeLiteral.Coordinate.char
                        defaultInt = Coordinates(3200, 3200).packed
                    }.build()
                )
            )
        }
    }
}
