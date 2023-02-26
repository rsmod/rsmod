package org.rsmod.plugins.api.cache.type.enums

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.plugins.api.cache.type.literal.CacheTypeLiteral
import org.rsmod.plugins.types.NamedEnum
import org.rsmod.plugins.types.NamedGraphic
import org.rsmod.plugins.types.NamedItem
import org.rsmod.plugins.types.NamedObject
import java.util.stream.Stream

class EnumTypeListTest {

    @ParameterizedTest
    @ArgumentsSource(EnumCorrectGetProvider::class)
    fun testCorrectGetTypeChecks(enum: EnumType<out Any, out Any>, input: Class<out Any>, output: Class<out Any>) {
        val list = mockEnumTypeList()
        val get = assertDoesNotThrow { list.get(NamedEnum(enum.id), input, output) }
        assertEquals(get.keyType.out, input)
        assertEquals(get.valType.out, output)
    }

    @ParameterizedTest
    @ArgumentsSource(EnumIncorrectGetProvider::class)
    fun testIncorrectGetTypeChecks(enum: EnumType<out Any, out Any>, input: Class<out Any>, output: Class<out Any>) {
        val list = mockEnumTypeList()
        assertThrows<IllegalStateException> { list.get(NamedEnum(enum.id), input, output) }
    }

    private companion object {

        private val enums = listOf(
            EnumTypeBuilder().apply {
                id = ITEM_BOOL_ENUM
                keyType = CacheTypeLiteral.Item.char
                valType = CacheTypeLiteral.Boolean.char
            }.build(),
            EnumTypeBuilder().apply {
                id = INT_OBJECT_ENUM
                keyType = CacheTypeLiteral.Integer.char
                valType = CacheTypeLiteral.Object.char
            }.build(),
            EnumTypeBuilder().apply {
                id = GRAPHIC_STRING_ENUM
                keyType = CacheTypeLiteral.Graphic.char
                valType = CacheTypeLiteral.String.char
            }.build()
        ).associateBy { it.id }

        private const val ITEM_BOOL_ENUM = 10
        private const val INT_OBJECT_ENUM = 20
        private const val GRAPHIC_STRING_ENUM = 30

        private fun mockEnumTypeList(): EnumTypeList {
            return EnumTypeList(enums)
        }
    }

    private object EnumCorrectGetProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(enums[ITEM_BOOL_ENUM], NamedItem::class.java, Boolean::class.java),
                Arguments.of(enums[INT_OBJECT_ENUM], Int::class.java, NamedObject::class.java),
                Arguments.of(enums[GRAPHIC_STRING_ENUM], NamedGraphic::class.java, String::class.java)
            )
        }
    }

    private object EnumIncorrectGetProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(enums[ITEM_BOOL_ENUM], Int::class.java, Boolean::class.java),
                Arguments.of(enums[INT_OBJECT_ENUM], Boolean::class.java, String::class.java),
                Arguments.of(enums[GRAPHIC_STRING_ENUM], NamedGraphic::class.java, Int::class.java)
            )
        }
    }
}
