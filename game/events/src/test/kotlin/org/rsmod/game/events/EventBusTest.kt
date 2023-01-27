package org.rsmod.game.events

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class EventBusTest {

    private val events = EventBus()

    @Test
    fun `add events of same type`() {
        Assertions.assertEquals(true, events.isEmpty())

        events.subscribe<TestEvent1> { /* empty */ }
        Assertions.assertNotNull(events[TestEvent1::class.java])
        Assertions.assertEquals(1, events.size)
        Assertions.assertEquals(1, events.getValue(TestEvent1::class.java).size)

        events.subscribe<TestEvent1> { /* empty */ }
        Assertions.assertEquals(1, events.size)
        Assertions.assertEquals(2, events.getValue(TestEvent1::class.java).size)
    }

    @Test
    fun `add events of different types`() {
        Assertions.assertEquals(true, events.isEmpty())

        events.subscribe<TestEvent1> { /* empty */ }
        Assertions.assertNotNull(events[TestEvent1::class.java])
        Assertions.assertEquals(1, events.size)
        Assertions.assertEquals(1, events.getValue(TestEvent1::class.java).size)

        events.subscribe<TestEvent2> { /* empty */ }
        Assertions.assertEquals(2, events.size)
        Assertions.assertEquals(1, events.getValue(TestEvent1::class.java).size)
        Assertions.assertEquals(1, events.getValue(TestEvent2::class.java).size)
    }

    private companion object {

        private object TestEvent1 : Event
        private object TestEvent2 : Event
    }
}
