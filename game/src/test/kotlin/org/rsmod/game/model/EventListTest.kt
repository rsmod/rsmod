package org.rsmod.game.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.events.Event
import org.rsmod.game.events.KeyedEvent

private typealias EventType = Unit

class EventListTest {

    /*
     * It is necessary to allow duplicate entries for events, even if their
     * id => event-type match. This allows for things like component button
     * click events to stack up and be executed multiple times per game cycle.
     */
    @Test
    fun testAllowDuplicateKeyedEntries() {
        val list = EventList<EventType>()
        check(list.keyed.isEmpty())

        list.add(0, TestKeyedEvent1("test-event"))
        assertEquals(1, list.keyed.size)

        list.add(0, TestKeyedEvent1("test-event"))
        assertEquals(2, list.keyed.size)

        list.add(0, TestKeyedEvent2("test-event"))
        assertEquals(3, list.keyed.size)

        list.add(0, TestKeyedEvent2("test-event"))
        assertEquals(4, list.keyed.size)
    }

    @Test
    fun testMaintainInsertionOrder() {
        val list = EventList<EventType>()
        check(list.unbound.isEmpty())
        check(list.keyed.isEmpty())

        for (insertionIndex in 0 until 32) {
            TestEvent1("test-event").let { event ->
                list += event
                assertEquals(insertionIndex + 1, list.unbound.size)
                assertSame(list.unbound.last(), event)
            }
        }
        list.clear()

        for (insertionIndex in 0 until 32) {
            TestKeyedEvent2("test-event").let { event ->
                list.add(0, event)
                assertEquals(insertionIndex + 1, list.keyed.size)
                assertSame(list.keyed.last().event, event)
            }
        }
        list.clear()
    }

    @Test
    fun testClear() {
        val list = EventList<EventType>()
        check(list.unbound.isEmpty())
        check(list.keyed.isEmpty())

        list += TestEvent1("test-event")
        assertEquals(1, list.unbound.size)
        list.clear()
        assertTrue(list.unbound.isEmpty())

        list.add(0L, TestKeyedEvent1("test-event"))
        assertEquals(1, list.keyed.size)
        list.clear()
        assertTrue(list.keyed.isEmpty())
    }

    private abstract class TestEvent(val debugName: String) : Event<EventType> {

        override fun toString(): String {
            return "TestEvent(debugName=$debugName)"
        }
    }

    private abstract class TestKeyedEvent(val debugName: String) : KeyedEvent<EventType> {

        override fun toString(): String {
            return "TestKeyedEvent(debugName=$debugName)"
        }
    }

    private class TestEvent1(debugName: String) : TestEvent(debugName)
    private class TestKeyedEvent1(debugName: String) : TestKeyedEvent(debugName)
    private class TestKeyedEvent2(debugName: String) : TestKeyedEvent(debugName)
}
