package org.rsmod.plugins.api.cache.type.param

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.map.Coordinates
import org.rsmod.plugins.api.cache.type.literal.CacheTypeLiteral
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedItem
import java.util.stream.Stream

class ParamMapTest {

    @ParameterizedTest
    @ArgumentsSource(ParamTypeProvider::class)
    fun testGetTypeChecks(map: ParamMap, type: ParamType<out Any>) {
        val get = assertDoesNotThrow { map.get(type) }
        assertNotNull(get)
    }

    private companion object {

        private const val COMPONENT_PARAM = 100
        private const val ITEM_PARAM = 200
        private const val COORDS_PARAM = 300

        private val params = listOf(
            ParamTypeBuilder().apply {
                id = COMPONENT_PARAM
                typeChar = CacheTypeLiteral.Component.char
            }.build(),
            ParamTypeBuilder().apply {
                id = ITEM_PARAM
                typeChar = CacheTypeLiteral.Item.char
            }.build(),
            ParamTypeBuilder().apply {
                id = COORDS_PARAM
                typeChar = CacheTypeLiteral.Coordinate.char
            }.build()
        ).associateBy { it.id }
    }

    private object ParamTypeProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    ParamMap(mapOf(COMPONENT_PARAM to NamedComponent(interfaceId = 200, child = 2))),
                    params.getValue(COMPONENT_PARAM)
                ),
                Arguments.of(
                    ParamMap(mapOf(ITEM_PARAM to NamedItem(4151))),
                    params.getValue(ITEM_PARAM)
                ),
                Arguments.of(
                    ParamMap(mapOf(COORDS_PARAM to Coordinates(3200, 3200))),
                    params.getValue(COORDS_PARAM)
                )
            )
        }
    }
}
