package org.rsmod.game.model.vars

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class VarMapAttributeTests {

    private val vars = VarMap()

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
        Assertions.assertNull(vars[key1])
        Assertions.assertNull(vars[key2])
        vars[key1] = value
        Assertions.assertNotNull(vars[key1])
        Assertions.assertNotNull(vars[key2])
        Assertions.assertTrue(key1 == key2)
        Assertions.assertEquals(key1.hashCode(), key2.hashCode())
        Assertions.assertEquals(value, vars[key1])
        Assertions.assertEquals(value, vars[key2])
    }

    @Test
    fun mutuallyInclusiveKeys() {
        val key1 = AttributeKey.createPersistent<Any>("key1")
        val key2 = AttributeKey.createPersistent<Any>("key2")
        val value = 0
        Assertions.assertNull(vars[key1])
        Assertions.assertNull(vars[key2])
        vars[key1] = value
        Assertions.assertNotNull(vars[key1])
        Assertions.assertNull(vars[key2])
        Assertions.assertFalse(key1 == key2)
        Assertions.assertNotEquals(key1.hashCode(), key2.hashCode())
        Assertions.assertEquals(value, vars[key1])
        Assertions.assertNotEquals(value, vars[key2])
    }
}
