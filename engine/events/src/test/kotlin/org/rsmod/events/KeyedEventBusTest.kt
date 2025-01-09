package org.rsmod.events

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.rsmod.events.KeyedEventBusTest.LocOp

class KeyedEventBusTest {
    @Test
    fun `ensure execution`() {
        val locFunc0: LocOp.() -> Unit = { throw IllegalStateException() }
        val locFunc1: LocOp.() -> Unit = { /* no-op */ }
        val events = eventBus {
            this[LocOp::class.java, 0L] = locFunc0
            this[LocOp::class.java, 1L] = locFunc1
        }
        val op = LocOp()
        val event0 = checkNotNull(events[LocOp::class.java, 0L])
        val event1 = checkNotNull(events[LocOp::class.java, 1L])
        assertThrows<IllegalStateException> { event0.invoke(op) }
        assertDoesNotThrow { event1.invoke(op) }
    }

    @Test
    fun `get correct lambda given key`() {
        val locFunc0: LocOp.() -> Unit = { /* no-op */ }
        val locFunc1: LocOp.() -> Unit = { /* no-op */ }
        val locFunc2: LocOp.() -> Unit = { /* no-op */ }
        val events = eventBus {
            this[LocOp::class.java, 0L] = locFunc0
            this[LocOp::class.java, 1L] = locFunc1
            this[LocOp::class.java, 2L] = locFunc2
        }
        assertSame(locFunc0, events[LocOp::class.java, 0L])
        assertNotSame(locFunc1, events[LocOp::class.java, 0L])
        assertNotSame(locFunc2, events[LocOp::class.java, 0L])

        assertSame(locFunc1, events[LocOp::class.java, 1L])
        assertNotSame(locFunc0, events[LocOp::class.java, 1L])
        assertNotSame(locFunc2, events[LocOp::class.java, 1L])

        assertSame(locFunc2, events[LocOp::class.java, 2L])
        assertNotSame(locFunc0, events[LocOp::class.java, 2L])
        assertNotSame(locFunc1, events[LocOp::class.java, 2L])
    }

    @Test
    fun `contains correct type and key`() {
        val events = eventBus {
            this[LocOp::class.java, 0L] = { /* no-op */ }
            this[ObjOp::class.java, 1L] = { /* no-op */ }
        }
        assertTrue(events.contains(LocOp::class.java, 0L))
        assertFalse(events.contains(LocOp::class.java, 1L))
        assertFalse(events.contains(ObjOp::class.java, 0L))
        assertTrue(events.contains(ObjOp::class.java, 1L))
        assertFalse(events.contains(PlayerOp::class.java, 0L))
        assertFalse(events.contains(PlayerOp::class.java, 1L))
        assertFalse(events.contains(KeyedEvent::class.java, 0L))
        assertFalse(events.contains(KeyedEvent::class.java, 1L))
    }

    private fun eventBus(init: KeyedEventBus.() -> Unit): KeyedEventBus {
        return KeyedEventBus().apply(init)
    }

    private data class LocOp(val loc: Int = 0, val shape: Int = 10, val angle: Int = 0) :
        KeyedEvent {
        override val id: Long = loc.toLong()
    }

    private data class ObjOp(val obj: Int) : KeyedEvent {
        override val id: Long = obj.toLong()
    }

    private data class PlayerOp(val pid: Int) : KeyedEvent {
        override val id: Long = pid.toLong()
    }

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    private operator fun <T : KeyedEvent> KeyedEventBus.get(
        type: Class<T>,
        key: Long,
    ): (T.() -> Unit)? {
        val map = this[type] ?: return null
        val action = map.getOrDefault(key, null) ?: return null
        return action
    }
}
