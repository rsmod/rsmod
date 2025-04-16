package org.rsmod.events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UnboundEventMapTest {
    @Test
    fun `add event`() {
        val map = UnboundEventMap()
        check(map.isEmpty())

        val func0: (BootUp) -> Unit = { /* no-op */ }
        val func1: (ShutDown) -> Unit = { /* no-op */ }
        val func2: (Cycle) -> Unit = { /* no-op */ }

        map += func0
        assertEquals(1, map.size())
        assertEquals(1, map[BootUp::class.java]?.size)

        map += func1
        assertEquals(2, map.size())
        assertEquals(1, map[ShutDown::class.java]?.size)
        // Previous event type list should stay the same.
        assertEquals(1, map[BootUp::class.java]?.size)

        map += func2
        assertEquals(3, map.size())
        assertEquals(1, map[Cycle::class.java]?.size)
        // Previous event type list should stay the same.
        assertEquals(1, map[BootUp::class.java]?.size)
        assertEquals(1, map[ShutDown::class.java]?.size)
    }

    @Test
    fun `check contains`() {
        val map = UnboundEventMap()
        val func0: (BootUp) -> Unit = {}
        val func1: (CycleEnd) -> Unit = {}

        assertFalse(BootUp::class.java in map)
        assertFalse(Cycle::class.java in map)
        assertFalse(CycleEnd::class.java in map)

        map += func0
        assertTrue(BootUp::class.java in map)
        assertFalse(Cycle::class.java in map)
        assertFalse(CycleEnd::class.java in map)

        map += func1
        assertTrue(BootUp::class.java in map)
        assertTrue(CycleEnd::class.java in map)
        assertFalse(Cycle::class.java in map)
    }

    @Test
    fun `get events`() {
        val map = UnboundEventMap()
        check(map.isEmpty())

        val func0: (BootUp) -> Unit = { /* no-op */ }
        val func1: (ShutDown) -> Unit = { /* no-op */ }
        val func2: (Cycle) -> Unit = { /* no-op */ }

        assertNull(map[BootUp::class.java])
        assertNull(map[ShutDown::class.java])
        assertNull(map[Cycle::class.java])

        map += func0
        assertEquals(1, map[BootUp::class.java]?.size)
        assertNull(map[ShutDown::class.java])
        assertNull(map[Cycle::class.java])
        assertEquals(1, map.size())

        map += func1
        assertEquals(1, map[ShutDown::class.java]?.size)
        assertEquals(1, map[BootUp::class.java]?.size)
        assertNull(map[Cycle::class.java])
        assertEquals(2, map.size())

        map += func2
        assertEquals(1, map[Cycle::class.java]?.size)
        assertEquals(1, map[BootUp::class.java]?.size)
        assertEquals(1, map[ShutDown::class.java]?.size)
        assertEquals(3, map.size())
    }

    @Test
    fun `add multiple events`() {
        val events = UnboundEventMap()
        check(events.isEmpty())
        for (count in 0 until 32) {
            val func: (CycleStart) -> Unit = { /* no-op */ }
            events += func
            assertEquals(count + 1, events[CycleStart::class.java]?.size)
            // Same event type - event map size should stay the same while
            // list increases.
            assertEquals(1, events.size())
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
        val events = eventMap {
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
        val events = eventMap {
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

    private fun eventMap(init: UnboundEventMap.() -> Unit): UnboundEventMap {
        return UnboundEventMap().apply(init)
    }

    private class BootUp : UnboundEvent

    private class ShutDown : UnboundEvent

    private open class Cycle : UnboundEvent

    private class CycleStart : Cycle()

    private class CycleEnd : Cycle()

    private inline operator fun <reified T : UnboundEvent> UnboundEventMap.plusAssign(
        noinline event: T.() -> Unit
    ) {
        add(T::class.java, event)
    }

    private fun UnboundEventMap.size(): Int = events.size

    private fun UnboundEventMap.isEmpty(): Boolean = events.isEmpty()
}
