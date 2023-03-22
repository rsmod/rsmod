package org.rsmod.game.events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KeyedEventTest {

    @Test
    fun testSet() {
        val bus = EventBus()
        check(bus.keyedEvents.isEmpty())

        bus.set(0, TestKeyedEventImpl1::class.java) {}
        assertEquals(1, bus.keyedEvents.size)
        assertNotNull(bus[TestKeyedEventImpl1::class.java])
        assertEquals(1, bus[TestKeyedEventImpl1::class.java]?.size)
        assertNotNull(bus[TestKeyedEventImpl1::class.java]?.get(0L))

        /* add same type with different id key */
        bus.set(1, TestKeyedEventImpl1::class.java) {}
        /* outer map should stay same size as event type is same */
        assertEquals(1, bus.keyedEvents.size)
        assertNotNull(bus[TestKeyedEventImpl1::class.java])
        assertEquals(2, bus[TestKeyedEventImpl1::class.java]?.size)
        assertNotNull(bus[TestKeyedEventImpl1::class.java]?.get(1L))
        /* previously-registered event type should still be valid */
        assertNotNull(bus[TestKeyedEventImpl1::class.java]?.get(0L))

        /* add new event type */
        bus.set(0, TestKeyedEventImpl2::class.java) {}
        assertEquals(2, bus.keyedEvents.size)
        assertNotNull(bus[TestKeyedEventImpl2::class.java])
        assertEquals(1, bus[TestKeyedEventImpl2::class.java]?.size)
        assertNotNull(bus[TestKeyedEventImpl2::class.java]?.get(0L))

        /* previous impl event map should stay the same */
        assertNotNull(bus[TestKeyedEventImpl1::class.java])
        assertEquals(2, bus[TestKeyedEventImpl1::class.java]?.size)
        assertNotNull(bus[TestKeyedEventImpl1::class.java]?.get(0L))
        assertNotNull(bus[TestKeyedEventImpl1::class.java]?.get(1L))
    }

    @Test
    fun testContains() {
        val bus = EventBus()
        check(bus.keyedEvents.isEmpty())
        for (i in 0L until 32L) {
            bus.set(i, TestKeyedEventBase::class.java) {}
            assertTrue(bus.contains(i, TestKeyedEventBase::class.java))
            assertFalse(bus.contains(i, TestKeyedEventImpl1::class.java))
            assertFalse(bus.contains(i, TestKeyedEventImpl2::class.java))
            for (prev in 0L until i) {
                assertTrue(bus.contains(prev, TestKeyedEventBase::class.java))
            }
        }
        for (i in 0L until 32L) {
            bus.set(i, TestKeyedEventImpl1::class.java) {}
            assertTrue(bus.contains(i, TestKeyedEventImpl1::class.java))
            assertFalse(bus.contains(i, TestKeyedEventImpl2::class.java))
            for (prev in 0L until i) {
                assertTrue(bus.contains(prev, TestKeyedEventImpl1::class.java))
            }
        }
        for (i in 0L until 32L) {
            bus.set(i, TestKeyedEventImpl2::class.java) {}
            assertTrue(bus.contains(i, TestKeyedEventImpl2::class.java))
            for (prev in 0L until i) {
                assertTrue(bus.contains(prev, TestKeyedEventImpl2::class.java))
            }
        }
    }

    @Test
    fun testGet() {
        val bus = EventBus()
        check(bus.keyedEvents.isEmpty())

        assertNull(bus[TestKeyedEventBase::class.java])
        assertNull(bus[TestKeyedEventImpl1::class.java])
        assertNull(bus[TestKeyedEventImpl2::class.java])

        bus.set(0L, TestKeyedEventBase::class.java) {}
        assertNotNull(bus[TestKeyedEventBase::class.java])
        assertEquals(1, bus[TestKeyedEventBase::class.java]?.size)
        assertNull(bus[TestKeyedEventImpl1::class.java])
        assertNull(bus[TestKeyedEventImpl2::class.java])
        assertEquals(1, bus.keyedEvents.size)

        bus.set(0L, TestKeyedEventImpl1::class.java) {}
        assertNotNull(bus[TestKeyedEventImpl1::class.java])
        assertNotNull(bus[TestKeyedEventImpl1::class.java]?.get(0L))
        assertEquals(1, bus[TestKeyedEventImpl1::class.java]?.size)
        assertNotNull(bus[TestKeyedEventBase::class.java])
        assertNull(bus[TestKeyedEventImpl2::class.java])
        assertEquals(2, bus.keyedEvents.size)

        bus.set(0L, TestKeyedEventImpl2::class.java) {}
        assertNotNull(bus[TestKeyedEventImpl2::class.java])
        assertNotNull(bus[TestKeyedEventImpl2::class.java]?.get(0L))
        assertEquals(1, bus[TestKeyedEventImpl2::class.java]?.size)
        assertNotNull(bus[TestKeyedEventBase::class.java])
        assertNotNull(bus[TestKeyedEventImpl1::class.java])
        assertEquals(3, bus.keyedEvents.size)
    }
}
