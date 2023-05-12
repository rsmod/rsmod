package org.rsmod.plugins.cache.config.enums

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.plugins.cache.literal.CacheTypeLiteral
import org.rsmod.plugins.types.NamedAnimation
import org.rsmod.plugins.types.NamedGraphic
import org.rsmod.plugins.types.NamedItem
import org.rsmod.plugins.types.NamedObject
import java.util.stream.Stream

class EnumTypeTest {

    @Test
    fun testGetReturnsNullOnUndefinedKey() {
        val intEnum = EnumTypeBuilder().apply {
            keyType = CacheTypeLiteral.Integer.char
            valType = CacheTypeLiteral.NamedItem.char
            defaultInt = -1
            intValues[0] = ITEM_ID_4151
            intValues[1] = ITEM_ID_11802
        }.build()

        assertEquals(NamedItem(ITEM_ID_4151), intEnum[0])
        assertEquals(NamedItem(ITEM_ID_11802), intEnum[1])
        assertNull(intEnum[2])
    }

    @Test
    fun testGetReturnsDefaultOnUndefinedKey() {
        val intEnum = EnumTypeBuilder().apply {
            keyType = CacheTypeLiteral.Integer.char
            valType = CacheTypeLiteral.NamedItem.char
            defaultInt = ITEM_ID_4151
            intValues[1] = ITEM_ID_11802
        }.build()

        assertEquals(NamedItem(ITEM_ID_4151), intEnum.default)
        assertEquals(NamedItem(ITEM_ID_11802), intEnum[1])
        assertFalse(intEnum.containsKey(ITEM_ID_4151))
        assertEquals(NamedItem(ITEM_ID_4151), intEnum[400])
    }

    @ParameterizedTest
    @ArgumentsSource(MatchTypeProvider::class)
    fun testGetReturnsCorrectTypedValue(
        keyType: CacheTypeLiteral,
        valType: CacheTypeLiteral,
        defaultInt: Int,
        defaultStr: String,
        keyClass: Class<Any>,
        key: Any,
        keyAsInt: Int,
        valueClass: Class<Any>,
        value: Any,
        valueAsInt: Int?,
        valueAsStr: String?
    ) {
        /* make sure one of these values is passed as an argument */
        require(valueAsInt != null || valueAsStr != null)
        require(key == keyAsInt || keyClass == key.javaClass) {
            "Expect key class and key object class to match."
        }
        require(value == valueAsInt || value == valueAsStr || valueClass == value.javaClass) {
            "Expect value class and value object class to match."
        }

        val enum = EnumTypeBuilder().apply {
            this.keyType = keyType.char
            this.valType = valType.char
            this.defaultInt = defaultInt
            this.defaultStr = defaultStr

            if (valueAsInt != null) {
                intValues[keyAsInt] = valueAsInt
            } else if (valueAsStr != null) {
                strValues[keyAsInt] = valueAsStr
            }
        }.build()

        assertTrue(enum.containsKey(key))
        assertEquals(value, enum[key])
    }

    private companion object {

        private const val ITEM_ID_4151 = 4151
        private const val ITEM_ID_11802 = 11802
        private const val OBJ_ID_2100 = 2100
        private const val ANIM_ID_1000 = 1000
        private const val GFX_ID_500 = 500

        private object MatchTypeProvider : ArgumentsProvider {

            override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
                return Stream.of(
                    Arguments.of(
                        CacheTypeLiteral.Integer,
                        CacheTypeLiteral.Integer,
                        // default args
                        -1, "",
                        // key args
                        Int::class.java, 1, 1,
                        // value args
                        Int::class.java, 3, 3, null
                    ),
                    Arguments.of(
                        CacheTypeLiteral.Integer,
                        CacheTypeLiteral.String,
                        // default args
                        -1, "",
                        // key args
                        Int::class.java, 1, 1,
                        // value args
                        String::class.java, "test", null, "test"
                    ),
                    Arguments.of(
                        CacheTypeLiteral.NamedItem,
                        CacheTypeLiteral.NamedItem,
                        // default args
                        -1, "",
                        // key args
                        NamedItem::class.java, NamedItem(ITEM_ID_4151), ITEM_ID_4151,
                        // value args
                        NamedItem::class.java, NamedItem(ITEM_ID_11802), ITEM_ID_11802, null
                    ),
                    Arguments.of(
                        CacheTypeLiteral.Object,
                        CacheTypeLiteral.NamedItem,
                        // default args
                        -1, "",
                        // key args
                        NamedObject::class.java, NamedObject(OBJ_ID_2100), OBJ_ID_2100,
                        // value args
                        NamedItem::class.java, NamedItem(ITEM_ID_4151), ITEM_ID_4151, null
                    ),
                    Arguments.of(
                        CacheTypeLiteral.Animation,
                        CacheTypeLiteral.Graphic,
                        // default args
                        -1, "",
                        // key args
                        NamedAnimation::class.java, NamedAnimation(ANIM_ID_1000), ANIM_ID_1000,
                        // value args
                        NamedGraphic::class.java, NamedGraphic(GFX_ID_500), GFX_ID_500, null
                    )
                )
            }
        }
    }
}
