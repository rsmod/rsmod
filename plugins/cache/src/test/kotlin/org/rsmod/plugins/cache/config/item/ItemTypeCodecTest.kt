package org.rsmod.plugins.cache.config.item

import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.plugins.cache.config.item.ItemTypeLoader.readType
import org.rsmod.plugins.cache.config.item.ItemTypePacker.writeType
import org.rsmod.plugins.cache.config.param.ParamMap
import org.rsmod.plugins.cache.config.param.ParamType
import org.rsmod.plugins.cache.config.param.ParamTypeBuilder
import org.rsmod.plugins.cache.config.param.ParamTypeList
import org.rsmod.plugins.cache.literal.CacheTypeLiteral
import org.rsmod.plugins.types.NamedItem
import java.util.stream.Stream

class ItemTypeCodecTest {

    @ParameterizedTest
    @ArgumentsSource(ItemTypeProvider::class)
    fun testCodec(type: ItemType, params: ParamTypeList) {
        val buf = Unpooled.buffer(32).apply { writeType(this, type, params, isJs5 = false) }
        val decoded = readType(buf, type.id, params)
        assertEquals(type, decoded)
    }

    @Test
    fun testJs5Codec() {
        val params = emptyMap<Int, ParamType<*>>()
        val type = ItemTypeBuilder().apply {
            id = 4151
            internalName = "test_name_4151"
        }.build()
        val buf = Unpooled.buffer(32).apply { writeType(this, type, params, isJs5 = true) }
        val decoded = readType(buf, type.id, params)
        val copyNonJs5 = decoded.copy(internalName = type.internalName)
        assertNotEquals(type.internalName, decoded.internalName)
        assertEquals(type, copyNonJs5)
    }

    private companion object {

        private const val ITEM_PARAM = 10
        private const val ITEM_415 = 415
        private const val ITEM_4151 = 4151

        private val params = listOf(
            ParamTypeBuilder().apply {
                id = ITEM_PARAM
                typeChar = CacheTypeLiteral.Item.char
            }.build()
        ).associateBy { it.id }

        private fun mockParamTypeList(): ParamTypeList {
            return params
        }
    }

    private object ItemTypeProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            val params = mockParamTypeList()
            return Stream.of(
                Arguments.of(
                    ItemTypeBuilder().apply {
                        id = ITEM_415
                        wearPos1 = 3
                        this.params = ParamMap(mapOf(ITEM_PARAM to NamedItem(ITEM_4151)))
                    }.build(),
                    params
                ),
                Arguments.of(
                    ItemTypeBuilder().apply {
                        id = ITEM_4151
                        model = 20
                        stacks = true
                        exchangeable = true
                    }.build(),
                    params
                ),
                Arguments.of(
                    ItemTypeBuilder().apply {
                        id = ITEM_4151
                        cost = 200_000_000
                        team = 10
                    }.build(),
                    params
                )
            )
        }
    }
}
