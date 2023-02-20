package org.rsmod.game.events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameEventBusTest {

    @Test
    fun testUnboundEventAdd() {
        val events = listOf(
            TestGameEvent(),
            TestGameEventImpl(),
            TestGameEvent(),
            TestGameEventInstance,
            TestGameEventInstance
        )
        val bus = GameEventBus()
        events.forEach { bus.add(it::class.java) {} }
        assertTrue(bus.contains(TestGameEvent::class.java))
        assertTrue(bus.contains(TestGameEventImpl::class.java))
        assertTrue(bus.contains(TestGameEventInstance::class.java))
        assertEquals(2, bus.unboundEvents[TestGameEvent::class.java]?.size)
        assertEquals(1, bus.unboundEvents[TestGameEventImpl::class.java]?.size)
        assertEquals(2, bus.unboundEvents[TestGameEventInstance::class.java]?.size)
    }

    @Test
    fun testUnboundEventGet() {
        val event = TestGameEvent()
        val bus = GameEventBus()
        val action: (TestGameEvent) -> Unit = {}
        bus.add(event::class.java, action)
        assertNotNull(bus.getOrNull(event::class.java))
        assertTrue(action in bus.getOrNull(event::class.java)!!)
    }

    @Test
    fun testBoundEventAdd() {
        val events = listOf(
            TestKeyedGameEvent() to 0L,
            TestKeyedGameEventImpl() to 1L,
            TestKeyedGameEventInstance to 2L,
            TestKeyedGameEvent() to 3L,
            TestKeyedGameEvent() to 4L,
            TestKeyedGameEventImpl() to 5L,
            TestKeyedGameEventInstance to 6L,
            TestKeyedGameEventInstance to 7L,
            TestKeyedGameEventInstance to 8L
        )
        val bus = GameEventBus()
        events.forEach { bus.add(it.first::class.java, it.second) {} }

        assertTrue(bus.contains(TestKeyedGameEvent::class.java, 0L))
        assertTrue(bus.contains(TestKeyedGameEvent::class.java, 3L))
        assertTrue(bus.contains(TestKeyedGameEvent::class.java, 4L))

        assertTrue(bus.contains(TestKeyedGameEventImpl::class.java, 1L))
        assertTrue(bus.contains(TestKeyedGameEventImpl::class.java, 5L))

        assertTrue(bus.contains(TestKeyedGameEventInstance::class.java, 2L))
        assertTrue(bus.contains(TestKeyedGameEventInstance::class.java, 6L))
        assertTrue(bus.contains(TestKeyedGameEventInstance::class.java, 7L))
        assertTrue(bus.contains(TestKeyedGameEventInstance::class.java, 8L))

        assertEquals(3, bus.boundEvents[TestKeyedGameEvent::class.java]?.size)
        assertEquals(2, bus.boundEvents[TestKeyedGameEventImpl::class.java]?.size)
        assertEquals(4, bus.boundEvents[TestKeyedGameEventInstance::class.java]?.size)
    }

    @Test
    fun testBoundEventGet() {
        val event = TestKeyedGameEvent()
        val bus = GameEventBus()
        val action: (TestKeyedGameEvent) -> Unit = {}
        bus.add(event::class.java, 1L, action)
        assertNotNull(bus.getOrNull(event::class.java))
        assertEquals(1, bus.getOrNull(event::class.java)?.size)
        assertTrue(bus.getOrNull(event::class.java)?.containsKey(1L) ?: false)
        assertSame(bus.getOrNull(event::class.java)?.get(1L), action)
    }

    private open class TestGameEvent : GameEvent
    private class TestGameEventImpl : TestGameEvent()
    private object TestGameEventInstance : GameEvent

    private open class TestKeyedGameEvent : GameKeyedEvent
    private class TestKeyedGameEventImpl : TestKeyedGameEvent()
    private object TestKeyedGameEventInstance : TestKeyedGameEvent()
}
