package org.rsmod.game.queue

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WorldQueueListTest {
    @Test
    fun `add single`() {
        val list = WorldQueueList().apply { add(10) {} }
        assertEquals(1, list.size)
        assertNotNull(list.iterator().next())
        assertEquals(10, list.iterator().next().remainingCycles)
    }

    @Test
    fun `add multiple`() {
        val list =
            WorldQueueList().apply {
                add(10) {}
                add(20) {}
            }
        assertEquals(2, list.size)

        val iterator = list.iterator()
        assertEquals(10, iterator.next().remainingCycles)
        assertTrue(iterator.hasNext())
        assertEquals(20, iterator.next().remainingCycles)
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `return null iterator from empty list`() {
        val list = WorldQueueList()
        assertFalse(list.iterator().hasNext())
        assertEquals(0, list.size)
        assertTrue(list.isEmpty)
    }

    @Test
    fun `remove from iterator`() {
        val list = WorldQueueList().apply { add(10) {} }

        val iterator = list.iterator()
        assertEquals(10, iterator.next().remainingCycles)
        iterator.remove()

        assertEquals(0, list.size)
    }

    @Test
    fun `calling remove without next should throw exception`() {
        val list = WorldQueueList().apply { add(10) {} }
        val iterator = list.iterator()
        assertThrows<IllegalStateException> { iterator.remove() }
    }

    @Test
    fun `remove head`() {
        val list =
            WorldQueueList().apply {
                add(10) {}
                add(20) {}
            }

        val iterator = list.iterator()
        iterator.next()
        iterator.remove()

        assertEquals(1, list.size)
        assertEquals(20, list.iterator().next().remainingCycles)
    }

    @Test
    fun `remove tail`() {
        val list =
            WorldQueueList().apply {
                add(10) {}
                add(20) {}
            }

        val iterator = list.iterator()
        iterator.next()
        iterator.next()
        iterator.remove()

        assertEquals(1, list.size)
        assertEquals(10, list.iterator().next().remainingCycles)
    }

    @Test
    fun `add large number of queues`() {
        val list = WorldQueueList()
        val count = 100_000

        repeat(count) { list.add(it) {} }
        assertEquals(count, list.size)

        val iterator = list.iterator()
        var iterated = 0
        while (iterator.hasNext()) {
            iterator.next()
            iterated++
        }
        assertEquals(count, iterated)
    }

    @Test
    fun `traverse iterator`() {
        val list =
            WorldQueueList().apply {
                add(10) {}
                add(20) {}
                add(30) {}
            }

        val iterator = list.iterator()
        assertEquals(10, iterator.next().remainingCycles)
        assertEquals(20, iterator.next().remainingCycles)
        assertEquals(30, iterator.next().remainingCycles)
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `reset iterator`() {
        val list =
            WorldQueueList().apply {
                add(10) {}
                add(20) {}
            }

        val iterator = list.iterator()
        assertTrue(iterator.hasNext())
        iterator.cleanUp()
        assertFalse(iterator.hasNext())
        assertThrows<NoSuchElementException> { iterator.next() }
        assertThrows<IllegalStateException> { iterator.remove() }
    }
}
