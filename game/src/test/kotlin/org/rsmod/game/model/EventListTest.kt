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
        check(list.getKeyed().isEmpty())

        list.add(0, TestKeyedEvent1("test-event"))
        assertEquals(1, list.getKeyed().size)

        list.add(0, TestKeyedEvent1("test-event"))
        assertEquals(2, list.getKeyed().size)

        list.add(0, TestKeyedEvent2("test-event"))
        assertEquals(3, list.getKeyed().size)

        list.add(0, TestKeyedEvent2("test-event"))
        assertEquals(4, list.getKeyed().size)
    }

    @Test
    fun testMaintainInsertionOrder() {
        val list = EventList<EventType>()
        check(list.getUnbound().isEmpty())
        check(list.getKeyed().isEmpty())

        for (insertionIndex in 0 until 32) {
            TestEvent1("test-event").let { event ->
                list += event
                assertEquals(insertionIndex + 1, list.getUnbound().size)
                assertSame(list.getUnbound().last(), event)
            }
        }
        list.clear()

        for (insertionIndex in 0 until 32) {
            TestKeyedEvent2("test-event").let { event ->
                list.add(0, event)
                assertEquals(insertionIndex + 1, list.getKeyed().size)
                assertSame(list.getKeyed().last().event, event)
            }
        }
        list.clear()
    }

    @Test
    fun testClear() {
        val list = EventList<EventType>()
        check(list.getUnbound().isEmpty())
        check(list.getKeyed().isEmpty())

        list += TestEvent1("test-event")
        assertEquals(1, list.getUnbound().size)
        list.clear()
        assertTrue(list.getUnbound().isEmpty())

        list.add(0L, TestKeyedEvent1("test-event"))
        assertEquals(1, list.getKeyed().size)
        list.clear()
        assertTrue(list.getKeyed().isEmpty())
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
