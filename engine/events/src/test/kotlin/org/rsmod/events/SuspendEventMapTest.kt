package org.rsmod.events

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class SuspendEventMapTest {
    @Test
    fun `ensure suspend execution`() = runTest {
        val locFunc0: suspend Unit.(LocOp) -> Unit = { throw IllegalStateException() }
        val locFunc1: suspend Unit.(LocOp) -> Unit = { /* no-op */ }
        val events = eventMap {
            this[LocOp::class.java, 0] = locFunc0
            this[LocOp::class.java, 1] = locFunc1
        }
        val op = LocOp()
        val event0 = checkNotNull(events[LocOp::class.java, 0])
        val event1 = checkNotNull(events[LocOp::class.java, 1])
        assertThrows<IllegalStateException> { event0.invoke(Unit, op) }
        assertDoesNotThrow { event1.invoke(Unit, op) }
    }

    @Test
    fun `get correct suspend lambda given key`() {
        val locFunc0: suspend Unit.(LocOp) -> Unit = { /* no-op */ }
        val locFunc1: suspend Unit.(LocOp) -> Unit = { /* no-op */ }
        val locFunc2: suspend Unit.(LocOp) -> Unit = { /* no-op */ }
        val events = eventMap {
            this[LocOp::class.java, 0] = locFunc0
            this[LocOp::class.java, 1] = locFunc1
            this[LocOp::class.java, 2] = locFunc2
        }
        assertSame(locFunc0, events[LocOp::class.java, 0])
        assertNotSame(locFunc1, events[LocOp::class.java, 0])
        assertNotSame(locFunc2, events[LocOp::class.java, 0])

        assertSame(locFunc1, events[LocOp::class.java, 1])
        assertNotSame(locFunc0, events[LocOp::class.java, 1])
        assertNotSame(locFunc2, events[LocOp::class.java, 1])

        assertSame(locFunc2, events[LocOp::class.java, 2])
        assertNotSame(locFunc0, events[LocOp::class.java, 2])
        assertNotSame(locFunc1, events[LocOp::class.java, 2])
    }

    @Test
    fun `contains correct suspendable type and key`() {
        val events = eventMap {
            this[LocOp::class.java, 0] = { /* no-op */ }
            this[ObjOp::class.java, 1] = { /* no-op */ }
        }
        assertTrue(events.contains(LocOp::class.java, 0L))
        assertFalse(events.contains(LocOp::class.java, 1L))
        assertFalse(events.contains(ObjOp::class.java, 0L))
        assertTrue(events.contains(ObjOp::class.java, 1L))
        assertFalse(events.contains(PlayerOp::class.java, 0L))
        assertFalse(events.contains(PlayerOp::class.java, 1L))
        assertFalse(events.contains(SuspendEvent::class.java, 0L))
        assertFalse(events.contains(SuspendEvent::class.java, 1L))
    }

    private fun eventMap(init: SuspendEventMap.() -> Unit): SuspendEventMap =
        SuspendEventMap().apply(init)

    private data class LocOp(val loc: Int = 0, val shape: Int = 10, val angle: Int = 0) :
        SuspendEvent<Unit> {
        override val id: Long = loc.toLong()
    }

    private data class ObjOp(val obj: Int) : SuspendEvent<Unit> {
        override val id: Long = obj.toLong()
    }

    private data class PlayerOp(val pid: Int) : SuspendEvent<Unit> {
        override val id: Long = pid.toLong()
    }

    private operator fun <K, T : SuspendEvent<K>> SuspendEventMap.set(
        type: Class<T>,
        key: Long,
        action: suspend K.(T) -> Unit,
    ) {
        putIfAbsent(type, key, action)
    }
}
