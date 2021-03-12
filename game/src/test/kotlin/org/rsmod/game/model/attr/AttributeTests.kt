package org.rsmod.game.model.attr

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class AttributeTests {

    private val attr = AttributeMap()

    @Test
    fun verifyKeyHashCodeAndEquals() {
        val key1 = AttributeKey.createPersistent<Any>("same_key")
        val key2 = AttributeKey.createPersistent<Any>("same_key")
        val key3 = AttributeKey.createPersistent<Any>("diff_key")
        Assertions.assertTrue(key1 == key2)
        Assertions.assertFalse(key1 == key3)
        Assertions.assertFalse(key2 == key3)
        Assertions.assertEquals(key1.hashCode(), key2.hashCode())
        Assertions.assertNotEquals(key1.hashCode(), key3.hashCode())
        Assertions.assertNotEquals(key2.hashCode(), key3.hashCode())
    }

    @Test
    fun overwriteEqualKeys() {
        val key1 = AttributeKey.createPersistent<Any>("key")
        val key2 = AttributeKey.createPersistent<Any>("key")
        val value = 0
        Assertions.assertNull(attr[key1])
        Assertions.assertNull(attr[key2])
        attr[key1] = value
        Assertions.assertNotNull(attr[key1])
        Assertions.assertNotNull(attr[key2])
        Assertions.assertTrue(key1 == key2)
        Assertions.assertEquals(key1.hashCode(), key2.hashCode())
        Assertions.assertEquals(value, attr[key1])
        Assertions.assertEquals(value, attr[key2])
    }

    @Test
    fun mutuallyInclusiveKeys() {
        val key1 = AttributeKey.createPersistent<Any>("key1")
        val key2 = AttributeKey.createPersistent<Any>("key2")
        val value = 0
        Assertions.assertNull(attr[key1])
        Assertions.assertNull(attr[key2])
        attr[key1] = value
        Assertions.assertNotNull(attr[key1])
        Assertions.assertNull(attr[key2])
        Assertions.assertFalse(key1 == key2)
        Assertions.assertNotEquals(key1.hashCode(), key2.hashCode())
        Assertions.assertEquals(value, attr[key1])
        Assertions.assertNotEquals(value, attr[key2])
    }
}
