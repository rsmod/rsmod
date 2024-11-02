package org.rsmod.game.queue

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.type.queue.QueueType

class PlayerQueueListTest {
    @Test
    fun `trigger speed-up mechanic when new queue is added before last queue in iterator`() {
        val hit1 = QueueType(internalId = 1, internalName = "Hit1")
        val hit2 = QueueType(internalId = 2, internalName = "Hit2")
        val death = QueueType(internalId = 3, internalName = "Death")

        val list =
            PlayerQueueList().apply {
                add(hit1, QueueCategory.Strong, remainingCycles = 1)
                add(hit2, QueueCategory.Strong, remainingCycles = 1)
            }

        var deathProcessed = false
        fun processQueue(queue: PlayerQueueList.Queue) {
            if (queue.id == hit1.id) {
                list.add(death, QueueCategory.Strong, remainingCycles = 1)
            } else if (queue.id == death.id) {
                deathProcessed = true
            }
        }

        val iterator = checkNotNull(list.iterator())
        while (iterator.hasNext()) {
            val queue = iterator.next()
            processQueue(queue)
            iterator.remove()
        }
        assertTrue(deathProcessed)
        assertFalse(list.contains(hit1))
        assertFalse(list.contains(hit2))
        assertFalse(list.contains(death))
        assertTrue(list.isEmpty)
    }

    @Test
    fun `skip speed-up mechanic when new queue is added during last queue in iterator`() {
        val hit1 = QueueType(internalId = 1, internalName = "Hit1")
        val death = QueueType(internalId = 2, internalName = "Death")

        val list = PlayerQueueList().apply { add(hit1, QueueCategory.Strong, remainingCycles = 1) }

        var deathProcessed = false
        fun processQueue(queue: PlayerQueueList.Queue) {
            if (queue.id == hit1.id) {
                list.add(death, QueueCategory.Strong, remainingCycles = 1)
            } else if (queue.id == death.id) {
                deathProcessed = true
            }
        }

        val iterator1 = checkNotNull(list.iterator())
        while (iterator1.hasNext()) {
            val queue = iterator1.next()
            processQueue(queue)
            iterator1.remove()
        }
        assertFalse(deathProcessed)
        assertFalse(list.contains(hit1))
        assertTrue(list.contains(death))

        val iterator2 = checkNotNull(list.iterator())
        while (iterator2.hasNext()) {
            val queue = iterator2.next()
            processQueue(queue)
            iterator2.remove()
        }
        assertTrue(deathProcessed)
        assertFalse(list.contains(hit1))
        assertFalse(list.contains(death))
        assertTrue(list.isEmpty)
    }

    @Test
    fun `add single`() {
        val list = PlayerQueueList().apply { add(100) }
        assertEquals(1, list.size)
        assertNotNull(list.iterator()?.next())
        assertEquals(100, list.iterator()?.next()?.id)
    }

    @Test
    fun `add multiple`() {
        val list =
            PlayerQueueList().apply {
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
        val list = PlayerQueueList()
        assertNull(list.iterator())
        assertEquals(0, list.size)
        assertTrue(list.isEmpty)
    }

    @Test
    fun `remove from iterator`() {
        val list = PlayerQueueList().apply { add(100) }

        val iterator = checkNotNull(list.iterator())
        assertEquals(100, iterator.next().id)
        iterator.remove()

        assertEquals(0, list.size)
    }

    @Test
    fun `calling remove without next should throw exception`() {
        val list = PlayerQueueList().apply { add(100) }
        val iterator = checkNotNull(list.iterator())
        assertThrows<IllegalStateException> { iterator.remove() }
    }

    @Test
    fun `remove head`() {
        val list =
            PlayerQueueList().apply {
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
            PlayerQueueList().apply {
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
        val list = PlayerQueueList()
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
            PlayerQueueList().apply {
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
            PlayerQueueList().apply {
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

    private fun PlayerQueueList.add(id: Int = 0, category: QueueCategory = QueueCategory.Normal) {
        val type = QueueType(id, "TestQueue")
        add(type, category, remainingCycles = 1)
    }
}
