package org.rsmod.game.model.vars

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class VarMapVarpTests {

    private val vars = VarMap()

    @Test
    fun verifyKeyHashCodeAndEquals() {
        val key1 = VarpKey.create<Any>(1)
        val key2 = VarpKey.create<Any>(1)
        val key3 = VarpKey.create<Any>(2)
        Assertions.assertTrue(key1 == key2)
        Assertions.assertFalse(key1 == key3)
        Assertions.assertFalse(key2 == key3)
        Assertions.assertEquals(key1.hashCode(), key2.hashCode())
        Assertions.assertNotEquals(key1.hashCode(), key3.hashCode())
        Assertions.assertNotEquals(key2.hashCode(), key3.hashCode())
    }

    @Test
    fun overwriteEqualKeys() {
        val key1 = VarpKey.create<Any>(1)
        val key2 = VarpKey.create<Any>(1)
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
        val key1 = VarpKey.create<Any>(1)
        val key2 = VarpKey.create<Any>(2)
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
