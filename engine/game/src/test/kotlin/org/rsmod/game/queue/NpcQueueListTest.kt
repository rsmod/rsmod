package org.rsmod.game.queue

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.type.queue.QueueType

class NpcQueueListTest {
    @Test
    fun `add single`() {
        val list = NpcQueueList().apply { add(100) }
        assertEquals(1, list.size)
        assertNotNull(list.iterator()?.next())
        assertEquals(100, list.iterator()?.next()?.id)
    }

    @Test
    fun `add multiple`() {
        val list =
            NpcQueueList().apply {
                add(100)
                add(200)
            }
        assertEquals(2, list.size)

        val iterator = checkNotNull(list.iterator())
        assertEquals(100, iterator.next().id)
        assertTrue(iterator.hasNext())
        assertEquals(200, iterator.next().id)
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `return null iterator from empty list`() {
        val list = NpcQueueList()
        assertNull(list.iterator())
        assertEquals(0, list.size)
        assertTrue(list.isEmpty)
    }

    @Test
    fun `remove from iterator`() {
        val list = NpcQueueList().apply { add(100) }

        val iterator = checkNotNull(list.iterator())
        assertEquals(100, iterator.next().id)
        iterator.remove()

        assertEquals(0, list.size)
    }

    @Test
    fun `calling remove without next should throw exception`() {
        val list = NpcQueueList().apply { add(100) }
        val iterator = checkNotNull(list.iterator())
        assertThrows<IllegalStateException> { iterator.remove() }
    }

    @Test
    fun `remove head`() {
        val list =
            NpcQueueList().apply {
                add(100)
                add(200)
            }

        val iterator = checkNotNull(list.iterator())
        iterator.next()
        iterator.remove()

        assertEquals(1, list.size)
        assertEquals(200, list.iterator()?.next()?.id)
    }

    @Test
    fun `remove tail`() {
        val list =
            NpcQueueList().apply {
                add(100)
                add(200)
            }

        val iterator = checkNotNull(list.iterator())
        iterator.next()
        iterator.next()
        iterator.remove()

        assertEquals(1, list.size)
        assertEquals(100, list.iterator()?.next()?.id)
    }

    @Test
    fun `add large number of queues`() {
        val list = NpcQueueList()
        val count = 100_000

        repeat(count) { list.add() }
        assertEquals(count, list.size)

        val iterator = checkNotNull(list.iterator())
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
            NpcQueueList().apply {
                add(100)
                add(200)
                add(300)
            }

        val iterator = checkNotNull(list.iterator())
        assertEquals(100, iterator.next().id)
        assertEquals(200, iterator.next().id)
        assertEquals(300, iterator.next().id)
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `reset iterator`() {
        val list =
            NpcQueueList().apply {
                add(100)
                add(200)
            }

        val iterator = checkNotNull(list.iterator())
        assertTrue(iterator.hasNext())
        iterator.cleanUp()
        assertFalse(iterator.hasNext())
        assertThrows<NoSuchElementException> { iterator.next() }
        assertThrows<IllegalStateException> { iterator.remove() }
    }

    private fun NpcQueueList.add(id: Int = 0) {
        val type = QueueType(id, "TestQueue")
        add(type, remainingCycles = 1)
    }
}
