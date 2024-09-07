package org.rsmod.events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UnboundEventBusTest {
    @Test
    fun `add event`() {
        val bus = UnboundEventBus()
        check(bus.events.isEmpty())

        val func0: (BootUp) -> Unit = { /* no-op */ }
        val func1: (ShutDown) -> Unit = { /* no-op */ }
        val func2: (Cycle) -> Unit = { /* no-op */ }

        bus += func0
        assertEquals(1, bus.events.size)
        assertEquals(1, bus[BootUp::class.java]?.size)

        bus += func1
        assertEquals(2, bus.events.size)
        assertEquals(1, bus[ShutDown::class.java]?.size)
        // Previous event type list should stay the same.
        assertEquals(1, bus[BootUp::class.java]?.size)

        bus += func2
        assertEquals(3, bus.events.size)
        assertEquals(1, bus[Cycle::class.java]?.size)
        // Previous event type list should stay the same.
        assertEquals(1, bus[BootUp::class.java]?.size)
        assertEquals(1, bus[ShutDown::class.java]?.size)
    }

    @Test
    fun `check contains`() {
        val bus = UnboundEventBus()
        val func0: (BootUp) -> Unit = {}
        val func1: (CycleEnd) -> Unit = {}

        assertFalse(BootUp::class.java in bus)
        assertFalse(Cycle::class.java in bus)
        assertFalse(CycleEnd::class.java in bus)

        bus += func0
        assertTrue(BootUp::class.java in bus)
        assertFalse(Cycle::class.java in bus)
        assertFalse(CycleEnd::class.java in bus)

        bus += func1
        assertTrue(BootUp::class.java in bus)
        assertTrue(CycleEnd::class.java in bus)
        assertFalse(Cycle::class.java in bus)
    }

    @Test
    fun `get events`() {
        val bus = UnboundEventBus()
        check(bus.events.isEmpty())

        val func0: (BootUp) -> Unit = { /* no-op */ }
        val func1: (ShutDown) -> Unit = { /* no-op */ }
        val func2: (Cycle) -> Unit = { /* no-op */ }

        assertNull(bus[BootUp::class.java])
        assertNull(bus[ShutDown::class.java])
        assertNull(bus[Cycle::class.java])

        bus += func0
        assertEquals(1, bus[BootUp::class.java]?.size)
        assertNull(bus[ShutDown::class.java])
        assertNull(bus[Cycle::class.java])
        assertEquals(1, bus.events.size)

        bus += func1
        assertEquals(1, bus[ShutDown::class.java]?.size)
        assertEquals(1, bus[BootUp::class.java]?.size)
        assertNull(bus[Cycle::class.java])
        assertEquals(2, bus.events.size)

        bus += func2
        assertEquals(1, bus[Cycle::class.java]?.size)
        assertEquals(1, bus[BootUp::class.java]?.size)
        assertEquals(1, bus[ShutDown::class.java]?.size)
        assertEquals(3, bus.events.size)
    }

    @Test
    fun `add multiple events`() {
        val events = UnboundEventBus()
        check(events.events.isEmpty())
        for (count in 0 until 32) {
            val func: (CycleStart) -> Unit = { /* no-op */ }
            events += func
            assertEquals(count + 1, events[CycleStart::class.java]?.size)
            // Same event type - event map size should stay the same while
            // list increases.
            assertEquals(1, events.events.size)
        }
    }

    @Test
    fun `add and execute open event type`() {
        var baseExecutionCount = 0
        var childExecutionCount0 = 0
        var childExecutionCount1 = 0
        val baseFunc: (Cycle) -> Unit = { baseExecutionCount++ }
        val childFunc0: (CycleStart) -> Unit = { childExecutionCount0++ }
        val childFunc1: (CycleEnd) -> Unit = { childExecutionCount1++ }
        val events = eventBus {
            this += baseFunc
            this += childFunc0
            this += childFunc1
        }
        val baseFuncList = checkNotNull(events[Cycle::class.java])
        val childFuncList0 = checkNotNull(events[CycleStart::class.java])
        val childFuncList1 = checkNotNull(events[CycleEnd::class.java])
        assertEquals(1, baseFuncList.size)
        assertEquals(1, childFuncList0.size)
        assertEquals(1, childFuncList1.size)

        baseFuncList.forEach { it.invoke(Cycle()) }
        assertEquals(1, baseExecutionCount)
        assertEquals(0, childExecutionCount0)
        assertEquals(0, childExecutionCount1)

        childFuncList0.forEach { it.invoke(CycleStart()) }
        assertEquals(1, baseExecutionCount)
        assertEquals(1, childExecutionCount0)
        assertEquals(0, childExecutionCount1)

        childFuncList1.forEach { it.invoke(CycleEnd()) }
        assertEquals(1, baseExecutionCount)
        assertEquals(1, childExecutionCount0)
        assertEquals(1, childExecutionCount1)
    }

    @Test
    fun `add and execute all events of type`() {
        var executionCount = 0
        val bootFunc0: BootUp.() -> Unit = { executionCount++ }
        val bootFunc1: BootUp.() -> Unit = { executionCount++ }
        val bootFunc2: BootUp.() -> Unit = { executionCount++ }
        val otherFunc: ShutDown.() -> Unit = { executionCount++ }
        val events = eventBus {
            this += bootFunc0
            this += bootFunc1
            this += bootFunc2
            this += otherFunc
        }
        val event = BootUp()
        val funcList = checkNotNull(events[BootUp::class.java])
        assertEquals(3, funcList.size)
        assertEquals(0, executionCount)
        funcList.forEach { it.invoke(event) }
        assertEquals(3, executionCount)
    }

    private fun eventBus(init: UnboundEventBus.() -> Unit): UnboundEventBus {
        return UnboundEventBus().apply(init)
    }

    private class BootUp : UnboundEvent

    private class ShutDown : UnboundEvent

    private open class Cycle : UnboundEvent

    private class CycleStart : Cycle()

    private class CycleEnd : Cycle()
}
