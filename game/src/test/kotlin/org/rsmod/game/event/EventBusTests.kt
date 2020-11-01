package org.rsmod.game.event

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventBusTests {

    @Test
    fun `add multiple events with same type to bus`() {
        val bus = EventBus()

        bus.subscribe<TestEvent1>().then {}
        Assertions.assertEquals(1, bus.size)

        val actions = bus[TestEvent1::class]
        Assertions.assertNotNull(actions)
        Assertions.assertEquals(1, actions?.size)

        bus.subscribe<TestEvent1>().then {}
        Assertions.assertEquals(1, bus.size)
        Assertions.assertEquals(2, actions?.size)
    }

    @Test
    fun `add actions for different event types to bus`() {
        val bus = EventBus()

        bus.subscribe<TestEvent1>().then {}
        Assertions.assertEquals(1, bus.size)

        val actions1 = bus[TestEvent1::class]
        Assertions.assertNotNull(actions1)
        Assertions.assertEquals(1, actions1?.size)

        bus.subscribe<TestEvent2>().then {}
        Assertions.assertEquals(2, bus.size)

        val actions2 = bus[TestEvent1::class]
        Assertions.assertNotNull(actions2)
        Assertions.assertEquals(1, actions2?.size)
    }

    companion object {
        private object TestEvent1 : Event
        private object TestEvent2 : Event
    }
}
