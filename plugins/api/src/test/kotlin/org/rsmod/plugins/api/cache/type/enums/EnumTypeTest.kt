package org.rsmod.plugins.api.cache.type.enums

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.types.NamedAnimation
import org.rsmod.game.types.NamedGraphic
import org.rsmod.game.types.NamedItem
import org.rsmod.game.types.NamedObject
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class EnumTypeTest {

    @Test
    fun `test enum get returns null on undefined key when default int value is -1`() {
        val intEnum = EnumTypeBuilder().apply {
            keyType = EnumTypeIdentifier.Integer.char
            valType = EnumTypeIdentifier.NamedItem.char
            defaultInt = -1
            intValues[0] = ITEM_ID_4151
            intValues[1] = ITEM_ID_11802
        }.build()

        Assertions.assertEquals(NamedItem(ITEM_ID_4151), intEnum[0])
        Assertions.assertEquals(NamedItem(ITEM_ID_11802), intEnum[1])
        Assertions.assertNull(intEnum[2])
    }

    @Test
    fun `test enum get returns default value on undefined key`() {
        val intEnum = EnumTypeBuilder().apply {
            keyType = EnumTypeIdentifier.Integer.char
            valType = EnumTypeIdentifier.NamedItem.char
            defaultInt = ITEM_ID_4151
            intValues[1] = ITEM_ID_11802
        }.build()

        Assertions.assertEquals(NamedItem(ITEM_ID_4151), intEnum.default)
        Assertions.assertEquals(NamedItem(ITEM_ID_11802), intEnum[1])
        Assertions.assertFalse(intEnum.containsKey(ITEM_ID_4151))
        Assertions.assertEquals(NamedItem(ITEM_ID_4151), intEnum[400])
    }

    @ParameterizedTest
    @ArgumentsSource(MatchTypeProvider::class)
    fun `test enum returns correct typed-values`(
        keyId: EnumTypeIdentifier,
        valId: EnumTypeIdentifier,
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
            this.keyType = keyId.char
            this.valType = valId.char
            this.defaultInt = defaultInt
            this.defaultStr = defaultStr

            if (valueAsInt != null) intValues[keyAsInt] = valueAsInt
            else if (valueAsStr != null) strValues[keyAsInt] = valueAsStr
        }.build()

        Assertions.assertTrue(enum.containsKey(key))
        Assertions.assertEquals(value, enum[key])
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
                        EnumTypeIdentifier.Integer,
                        EnumTypeIdentifier.Integer,
                        // default args
                        -1, "",
                        // key args
                        Int::class.java, 1, 1,
                        // value args
                        Int::class.java, 3, 3, null
                    ),
                    Arguments.of(
                        EnumTypeIdentifier.Integer,
                        EnumTypeIdentifier.String,
                        // default args
                        -1, "",
                        // key args
                        Int::class.java, 1, 1,
                        // value args
                        String::class.java, "test", null, "test"
                    ),
                    Arguments.of(
                        EnumTypeIdentifier.NamedItem,
                        EnumTypeIdentifier.NamedItem,
                        // default args
                        -1, "",
                        // key args
                        NamedItem::class.java, NamedItem(ITEM_ID_4151), ITEM_ID_4151,
                        // value args
                        NamedItem::class.java, NamedItem(ITEM_ID_11802), ITEM_ID_11802, null
                    ),
                    Arguments.of(
                        EnumTypeIdentifier.Object,
                        EnumTypeIdentifier.NamedItem,
                        // default args
                        -1, "",
                        // key args
                        NamedObject::class.java, NamedObject(OBJ_ID_2100), OBJ_ID_2100,
                        // value args
                        NamedItem::class.java, NamedItem(ITEM_ID_4151), ITEM_ID_4151, null
                    ),
                    Arguments.of(
                        EnumTypeIdentifier.Animation,
                        EnumTypeIdentifier.Graphic,
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
