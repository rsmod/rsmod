package org.rsmod.game.model.attribute

import org.rsmod.game.attribute.AttributeKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AttributeKeyTests {

    @Test
    fun `key with no persistence key and key with persistence key should not match`() {
        val key1 = AttributeKey<Int>()
        val key2 = AttributeKey<Int>("test_key")

        Assertions.assertNotEquals(key2.hashCode(), key1.hashCode())
        Assertions.assertFalse(key1 == key2)
    }

    @Test
    fun `keys with different persistence key should not match`() {
        val key1 = AttributeKey<Int>("test_key2")
        val key2 = AttributeKey<Int>("test_key")

        Assertions.assertNotEquals(key2.hashCode(), key1.hashCode())
        Assertions.assertFalse(key1 == key2)
    }

    @Test
    fun `keys with same persistence key should match`() {
        val key1 = AttributeKey<Int>("test_key")
        val key2 = AttributeKey<Int>("test_key")

        Assertions.assertEquals(key2.hashCode(), key1.hashCode())
        Assertions.assertTrue(key1 == key2)
    }
}
