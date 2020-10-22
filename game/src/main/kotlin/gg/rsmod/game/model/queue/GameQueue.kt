package gg.rsmod.game.model.queue

import gg.rsmod.game.coroutine.GameCoroutineContext
import gg.rsmod.game.coroutine.launchCoroutine
import java.util.LinkedList
import java.util.Queue
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.suspendCancellableCoroutine

private const val MAX_ACTIVE_QUEUES = 2

internal sealed class QueueType {
    object Weak : QueueType()
    object Normal : QueueType()
    object Strong : QueueType()
}

class GameQueue internal constructor(
    internal var launched: Boolean = false,
    private var coroutineContext: GameCoroutineContext? = null,
    private var resumeCondition: GameQueueCondition? = null
) {

    val idle: Boolean
        get() = resumeCondition == null

    suspend fun delay(ticks: Int = 1): Unit = suspendCoroutine {
        check(ticks > 0) { "Delay ticks must be greater than 0." }
        it.suspend(condition = WaitCycleCondition(ticks))
    }

    suspend fun delay(predicate: () -> Boolean): Unit = suspendCoroutine {
        it.suspend(condition = PredicateCondition(predicate))
    }

    suspend fun cancel(): Nothing = suspendCancellableCoroutine {
        resumeCondition = null
        coroutineContext = null
        it.cancel()
    }

    internal fun cycle() {
        val condition = resumeCondition ?: return
        if (condition.resume()) {
            resumeCondition = null
            coroutineContext?.resume()
        }
    }

    private fun Continuation<Unit>.suspend(condition: GameQueueCondition) {
        val coroutine = GameCoroutineContext(this)
        resumeCondition = condition
        coroutineContext = coroutine
    }
}

class GameQueueStack internal constructor(
    private var currentQueue: GameQueue? = null,
    private var currPriority: QueueType = QueueType.Weak,
    private val pendingQueue: LinkedList<GameQueueContext> = LinkedList()
) {

    val size: Int
        get() = pendingQueue.size + (if (currentQueue != null) 1 else 0)

    internal fun queue(type: QueueType, block: suspend GameQueue.() -> Unit) {
        if (!overtakeQueues(type)) {
            return
        }
        if (size >= MAX_ACTIVE_QUEUES) {
            pendingQueue.removeLast()
        }
        val ctx = GameQueueContext(block)
        pendingQueue.add(ctx)
    }

    internal fun clear() {
        currentQueue = null
        currPriority = QueueType.Weak
        pendingQueue.clear()
    }

    internal suspend fun cycle() {
        pollPending()
        cycleCurrent()
    }

    private suspend fun pollPending() {
        if (currentQueue != null) {
            /* don't override the current queue */
            return
        }
        if (pendingQueue.isEmpty()) {
            /* make sure there's a pending queue to begin with */
            return
        }
        currentQueue = launchPending()
    }

    private fun cycleCurrent() {
        val queue = currentQueue ?: return
        /* don't resume queue on same tick that its logic-block is invoked */
        if (queue.launched) {
            queue.cycle()
        }
        queue.launched = true
        if (queue.idle) {
            discardCurrent()
        }
    }

    private fun discardCurrent() {
        currentQueue = null
        /* only reset priority if no other queue is pending */
        if (pendingQueue.isEmpty()) {
            currPriority = QueueType.Weak
        }
    }

    private suspend fun launchPending(): GameQueue {
        val ctx = pendingQueue.poll()
        val queue = GameQueue()
        val block = suspend { ctx.block(queue) }
        block.launchCoroutine()
        return queue
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
    private val pending: Queue<GameQueueContext> = LinkedList()
) : List<GameQueue> by queues {

    internal fun queue(block: suspend GameQueue.() -> Unit) {
        val context = GameQueueContext(block)
        pending.add(context)
    }

    internal suspend fun cycle() {
        addPending()
        cycleQueues()
    }

    private fun cycleQueues() {
        queues.forEach { it.cycle() }
        queues.removeIf { it.idle }
    }

    private suspend fun addPending() {
        while (pending.isNotEmpty()) {
            val ctx = pending.poll() ?: break
            val queue = queue(ctx)
            if (!queue.idle) {
                queues.add(queue)
            }
        }
    }

    private suspend fun queue(ctx: GameQueueContext): GameQueue {
        val queue = GameQueue()
        val block = suspend { ctx.block(queue) }
        block.launchCoroutine()
        return queue
    }
}

internal data class GameQueueContext(
    val block: suspend GameQueue.() -> Unit
)

internal interface GameQueueCondition {

    fun resume(): Boolean
}

internal class WaitCycleCondition(private var delay: Int) : GameQueueCondition {

    override fun resume(): Boolean {
        return --delay <= 0
    }
}

internal class PredicateCondition(private val predicate: () -> Boolean) : GameQueueCondition {

    override fun resume(): Boolean {
        return predicate()
    }
}
