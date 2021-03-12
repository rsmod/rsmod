package org.rsmod.game.queue

import kotlinx.coroutines.withContext
import org.rsmod.game.coroutine.GameCoroutineTask
import org.rsmod.game.event.Event
import java.util.LinkedList

private const val MAX_ACTIVE_QUEUES = 2

class GameQueueStack(
    private var currQueue: GameQueue? = null,
    private var currPriority: QueueType = QueueType.Weak,
    private val pendQueue: LinkedList<GameQueueBlock> = LinkedList()
) {

    val size: Int
        get() = pendQueue.size + (if (currQueue != null) 1 else 0)

    val idle: Boolean
        get() = currQueue == null

    internal fun queue(type: QueueType, block: suspend () -> Unit) {
        if (!overtakeQueues(type)) {
            return
        }
        if (size >= MAX_ACTIVE_QUEUES) {
            pendQueue.removeLast()
        }
        val queueBlock = GameQueueBlock(block)
        pendQueue.add(queueBlock)
    }

    internal fun clear() {
        currQueue = null
        currPriority = QueueType.Weak
        pendQueue.clear()
    }

    fun processCurrent() {
        val queue = currQueue ?: return
        queue.task.cycle()
        if (queue.task.idle) {
            discardCurrent()
        }
    }

    suspend fun pollPending() {
        if (currQueue != null) return
        val ctx = pendQueue.poll() ?: return
        val task = GameCoroutineTask()
        val block = suspend { withContext(task) { ctx.block() } }
        task.launch(block)
        currQueue = GameQueue(task)
    }

    fun <T : Event> submitEvent(value: T) {
        val queue = currQueue ?: return
        queue.task.submit(value)
    }

    fun discardCurrent() {
        currQueue = null
        /* only reset priority if no other queue is pending */
        if (pendQueue.isEmpty()) {
            currPriority = QueueType.Weak
        }
    }

    private fun overtakeQueues(priority: QueueType): Boolean {
        if (priority == currPriority) {
            return true
        }
        if (!priority.overtake(currPriority)) {
            return false
        }
        if (priority != currPriority) {
            clear()
            currPriority = priority
        }
        return true
    }

    private fun QueueType.overtake(other: QueueType): Boolean = when (this) {
        QueueType.Normal -> other == QueueType.Weak
        QueueType.Strong -> true
        else -> false
    }
}
