package org.rsmod.game.queue

import org.rsmod.game.type.queue.QueueType

public class NpcQueueList {
    public var size: Int = 0
        private set

    private var first: Node? = null
    private var last: Node? = null
    private var iterator: QueueIterator? = null

    public val isEmpty: Boolean
        get() = size == 0

    public val isNotEmpty: Boolean
        get() = size > 0

    public fun add(type: QueueType, remainingCycles: Int) {
        val queue = Queue(type.id, remainingCycles)
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

    internal data class Node(val queue: Queue, var prev: Node?, var next: Node? = null)

    public data class Queue(public val id: Int, public var remainingCycles: Int)

    public fun iterator(): QueueIterator? {
        if (isEmpty) {
            return null
        }
        if (iterator == null) {
            iterator = QueueIterator()
        }
        iterator?.reset(first)
        return iterator
    }

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
