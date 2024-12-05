package org.rsmod.game.queue

public class WorldQueueList {
    public var size: Int = 0
        private set

    private val iterator = QueueIterator()

    private var first: Node? = null
    private var last: Node? = null

    public val isEmpty: Boolean
        get() = size == 0

    public val isNotEmpty: Boolean
        get() = size > 0

    public fun add(remainingCycles: Int, action: () -> Unit) {
        val queue = Queue(action, remainingCycles)
        val node = Node(queue, prev = last)
        add(node)
    }

    private fun add(node: Node) {
        if (isEmpty) {
            check(first == null)
            check(last == null)
            first = node
        }
        last?.next = node
        last = node
        size++
    }

    private fun remove(node: Node) {
        val prev = node.prev
        val next = node.next

        // If `prev` is null that means `node` was the first node.
        if (prev == null) {
            first = next
        } else {
            prev.next = next
        }

        // If `next` is null that means `node` was the last node.
        if (next == null) {
            last = prev
        } else {
            next.prev = prev
        }

        check(size > 0)
        size--
    }

    public data class Node(val queue: Queue, var prev: Node?, var next: Node? = null)

    public data class Queue(val action: () -> Unit, var remainingCycles: Int)

    public fun iterator(): QueueIterator {
        iterator.reset(first)
        return iterator
    }

    // We are assuming that world queues have the same "speed-up" mechanic as player and npc
    // queues.
    public inner class QueueIterator {
        private var next: Node? = null
        private var curr: Node? = null

        public fun hasNext(): Boolean = next != null

        public fun next(): Queue {
            val node = next ?: throw NoSuchElementException()
            curr = node
            next = node.next
            return node.queue
        }

        public fun remove() {
            val node = curr ?: error("`next` must be called before any `remove`.")
            remove(node)
        }

        public fun cleanUp() {
            this.next = null
            this.curr = null
        }

        internal fun reset(first: Node?) {
            this.next = first
            this.curr = null
        }
    }
}
