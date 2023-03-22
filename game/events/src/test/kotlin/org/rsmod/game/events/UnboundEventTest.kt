package org.rsmod.game.events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UnboundEventTest {

    @Test
    fun testAddOpenEventType() {
        val bus = EventBus()
        check(bus.unboundEvents.isEmpty())
        bus.add(TestEventBase::class.java) {}
        assertEquals(1, bus.unboundEvents.size)
        bus[TestEventBase::class.java].let { mapped ->
            assertNotNull(mapped)
            assertEquals(1, mapped?.size)
        }
        /* add a `TestEvent` impl type event */
        bus.add(TestEventImpl1::class.java) {}
        assertEquals(2, bus.unboundEvents.size)
        assertEquals(1, bus[TestEventImpl1::class.java]?.size)
        /* base `TestEvent` type should not be affected by any of its impl types */
        assertEquals(1, bus[TestEventBase::class.java]?.size)
    }

    @Test
    fun testAddSameEventType() {
        val bus = EventBus()
        check(bus.unboundEvents.isEmpty())
        for (index in 0 until 32) {
            bus.add(TestEventImpl1::class.java) {}
            assertEquals(index + 1, bus[TestEventImpl1::class.java]?.size)
            /* same event type; outer unbound map size should stay as one */
            assertEquals(1, bus.unboundEvents.size)
        }
    }

    @Test
    fun testAdd() {
        val bus = EventBus()
        check(bus.unboundEvents.isEmpty())

        bus.add(TestEventBase::class.java) {}
        assertEquals(1, bus.unboundEvents.size)
        assertNotNull(bus[TestEventBase::class.java])
        assertEquals(1, bus[TestEventBase::class.java]?.size)

        bus.add(TestEventImpl1::class.java) {}
        assertEquals(2, bus.unboundEvents.size)
        assertNotNull(bus[TestEventImpl1::class.java])
        /* previous event types should stay the same */
        assertEquals(1, bus[TestEventImpl1::class.java]?.size)
        assertEquals(1, bus[TestEventBase::class.java]?.size)

        bus.add(TestEventImpl2::class.java) {}
        assertEquals(3, bus.unboundEvents.size)
        assertNotNull(bus[TestEventImpl2::class.java])
        assertEquals(1, bus[TestEventImpl2::class.java]?.size)
        /* previous event types should stay the same */
        assertEquals(1, bus[TestEventImpl1::class.java]?.size)
        assertEquals(1, bus[TestEventBase::class.java]?.size)
    }

    @Test
    fun testContains() {
        val bus = EventBus()
        check(bus.unboundEvents.isEmpty())
        val event1 = TestEventImpl1()
        assertFalse(event1::class.java in bus)
        bus.add(event1::class.java) {}
        assertTrue(event1::class.java in bus)
        assertFalse(TestEventBase::class.java in bus)
        assertFalse(TestEventImpl2::class.java in bus)
        val event2 = TestEventImpl2()
        assertFalse(event2::class.java in bus)
        bus.add(event2::class.java) {}
        assertTrue(event2::class.java in bus)
        assertTrue(event1::class.java in bus)
        assertFalse(TestEventBase::class.java in bus)
    }

    @Test
    fun testGet() {
        val bus = EventBus()
        check(bus.unboundEvents.isEmpty())

        assertNull(bus[TestEventBase::class.java])
        assertNull(bus[TestEventImpl1::class.java])
        assertNull(bus[TestEventImpl2::class.java])

        bus.add(TestEventBase::class.java) {}
        assertNotNull(bus[TestEventBase::class.java])
        assertEquals(1, bus[TestEventBase::class.java]?.size)
        assertNull(bus[TestEventImpl2::class.java])
        assertEquals(1, bus.unboundEvents.size)

        bus.add(TestEventImpl1::class.java) {}
        assertNotNull(bus[TestEventImpl1::class.java])
        assertEquals(1, bus[TestEventImpl1::class.java]?.size)
        assertNotNull(bus[TestEventBase::class.java])
        assertNull(bus[TestEventImpl2::class.java])
        assertEquals(2, bus.unboundEvents.size)

        bus.add(TestEventImpl2::class.java) {}
        assertNotNull(bus[TestEventImpl2::class.java])
        assertEquals(1, bus[TestEventImpl2::class.java]?.size)
        assertNotNull(bus[TestEventBase::class.java])
        assertNotNull(bus[TestEventImpl1::class.java])
        assertEquals(3, bus.unboundEvents.size)
    }
}
