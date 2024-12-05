package org.rsmod.game.queue

import org.rsmod.game.type.queue.QueueType

public class PlayerQueueList {
    public var size: Int = 0
        private set

    public var strongQueues: Int = 0
        private set

    private var iterator: QueueIterator? = null
    private var first: Node? = null
    private var last: Node? = null

    public val isEmpty: Boolean
        get() = size == 0

    public val isNotEmpty: Boolean
        get() = size > 0

    public fun add(
        type: QueueType,
        category: QueueCategory,
        remainingCycles: Int,
        args: Any? = null,
    ) {
        val queue = Queue(type.id, category.id, remainingCycles, args)
        val node = Node(queue, prev = last)
        add(node)

        if (category == QueueCategory.Strong) {
            strongQueues++
        }
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

        if (node.queue.category == QueueCategory.Strong.id) {
            check(strongQueues > 0)
            strongQueues--
        }
    }

    public fun removeAll(type: QueueType): Int {
        if (isEmpty) {
            return 0
        }

        val startSize = size
        var count = 0
        var current = first
        while (current != null) {
            if (current.queue.id == type.id) {
                remove(current)
                count++
            }
            current = current.next ?: break
        }

        check(startSize - count == size)
        return count
    }

    public fun count(type: QueueType): Int {
        if (isEmpty) {
            return 0
        }

        var count = 0
        var current = first
        while (current != null) {
            if (current.queue.id == type.id) {
                count++
            }
            current = current.next ?: break
        }
        return count
    }

    public operator fun contains(type: QueueType): Boolean {
        if (isEmpty) {
            return false
        }

        var current = first
        while (current != null) {
            if (current.queue.id == type.id) {
                return true
            }
            current = current.next ?: break
        }
        return false
    }

    public operator fun contains(category: QueueCategory): Boolean {
        if (isEmpty) {
            return false
        }

        var current = first
        while (current != null) {
            if (current.queue.category == category.id) {
                return true
            }
            current = current.next ?: break
        }
        return false
    }

    public fun clear() {
        iterator?.cleanUp()

        first = null
        last = null

        size = 0
        strongQueues = 0
    }

    internal data class Node(val queue: Queue, var prev: Node?, var next: Node? = null)

    public data class Queue(
        public val id: Int,
        public val category: Int,
        public var remainingCycles: Int,
        public val args: Any?,
    )

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
