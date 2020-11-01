package org.rsmod.game.model.attribute

import java.util.stream.Stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.attribute.AttributeKey
import org.rsmod.game.attribute.AttributeMap

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AttributeMapTests {

    @Test
    fun `attribute map should not find a key when empty`() {
        val key1 = AttributeKey<Int>("test_key")
        val key2 = AttributeKey<Int>()

        val map = AttributeMap()
        Assertions.assertTrue(map.isEmpty())
        Assertions.assertNull(map[key1])
        Assertions.assertNull(map[key2])
    }

    @ParameterizedTest
    @ArgumentsSource(AttributeDoubleValueProvider::class)
    fun `attribute keys with same persistence key should overwrite`(value1: Int, value2: Int) {
        val key1 = AttributeKey<Int>("test_key")
        val key2 = AttributeKey<Int>("test_key")

        val map = AttributeMap()

        map[key1] = value1
        Assertions.assertNotNull(map[key1])
        Assertions.assertNotNull(map[key2])
        Assertions.assertEquals(map[key1], map[key2])
        Assertions.assertEquals(1, map.size)

        map[key2] = value2
        Assertions.assertEquals(value2, map[key1])
        Assertions.assertEquals(1, map.size)
    }

    @ParameterizedTest
    @ArgumentsSource(AttributeDoubleValueProvider::class)
    fun `attribute keys with different persistence key should not be equal`(value1: Int, value2: Int) {
        val key1 = AttributeKey<Int>("test_key")
        val key2 = AttributeKey<Int>("test_key2")

        val map = AttributeMap()

        map[key1] = value1
        Assertions.assertNotNull(map[key1])
        Assertions.assertNull(map[key2])
        Assertions.assertEquals(1, map.size)

        map[key2] = value2
        Assertions.assertNotNull(map[key2])
        Assertions.assertNotEquals(map[key1], map[key2])
        Assertions.assertEquals(2, map.size)
    }
}

private class AttributeDoubleValueProvider : ArgumentsProvider {

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(1, 2),
            Arguments.of(8, 16),
            Arguments.of(32, 64),
            Arguments.of(128, 256)
        )
    }
}
