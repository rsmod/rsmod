package gg.rsmod.game.queue

import gg.rsmod.game.coroutine.GameCoroutineTask
import gg.rsmod.game.event.Event
import java.util.LinkedList
import java.util.Queue
import kotlinx.coroutines.withContext

private const val MAX_ACTIVE_QUEUES = 2

sealed class QueueType {
    object Weak : QueueType()
    object Normal : QueueType()
    object Strong : QueueType()
}

inline class GameQueue(val task: GameCoroutineTask)

inline class GameQueueBlock(val block: suspend () -> Unit)

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
        currQueue?.task?.submit(value)
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

class GameQueueList internal constructor(
    private val queues: MutableList<GameQueue> = mutableListOf(),
    private val pending: Queue<GameQueueBlock> = LinkedList()
) : List<GameQueue> by queues {

    internal fun queue(block: suspend () -> Unit) {
        val queueBlock = GameQueueBlock(block)
        pending.add(queueBlock)
    }

    internal suspend fun cycle() {
        cycleQueues()
        addPending()
    }

    private fun cycleQueues() {
        queues.forEach { it.task.cycle() }
        queues.removeIf { it.task.idle }
    }

    private suspend fun addPending() {
        while (pending.isNotEmpty()) {
            val ctx = pending.poll() ?: break
            val task = GameCoroutineTask()
            val block = suspend { withContext(task) { ctx.block() } }
            val queue = GameQueue(task)
            task.launch(block)
            if (!queue.task.idle) {
                queues.add(queue)
            }
        }
    }
}
