package gg.rsmod.game.model.queue

import gg.rsmod.game.coroutine.GameCoroutineContext
import gg.rsmod.game.coroutine.launchCoroutine
import java.util.LinkedList
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
    private val contextQueue: LinkedList<GameQueueContext> = LinkedList()
) {

    val size: Int
        get() = contextQueue.size + (if (currentQueue != null) 1 else 0)

    internal fun queue(type: QueueType, block: suspend GameQueue.() -> Unit) {
        if (!overtakeQueues(type)) {
            return
        }
        if (size >= MAX_ACTIVE_QUEUES) {
            /*
             * Can only stack up to two queues at a time. This can
             * be proven with strong queues, but not sure about other
             * priority types.
             */
            contextQueue.removeLast()
        }
        val ctx = GameQueueContext(type, block)
        contextQueue.add(ctx)
    }

    internal suspend fun cycle() {
        pollContext()
        pollQueue()
    }

    private suspend fun pollQueue() {
        val queue = currentQueue ?: return
        queue.cycle()
        if (queue.idle) {
            resetQueue()
            /* immediately poll next queue */
            pollContext()
            pollQueue()
        }
    }

    private suspend fun pollContext() {
        if (currentQueue != null) {
            /*
             * Wait until pending queue has finished before
             * attempting to launch a new one.
             */
            return
        }
        val ctx = contextQueue.poll() ?: return
        val queue = queue(ctx)
        currentQueue = queue
    }

    private fun resetQueue() {
        currentQueue = null
        currPriority = QueueType.Weak
    }

    internal fun clear() {
        resetQueue()
        contextQueue.clear()
    }

    private fun overtakeQueues(priority: QueueType): Boolean {
        if (priority == currPriority) {
            return true
        }
        if (!priority.overtake(currPriority)) {
            return false
        }
        if (priority != currPriority) {
            /*
             * If priority of current queue is lower than given priority,
             * clear the current queue as well as pending queues.
             */
            clear()
            currPriority = priority
        }
        return true
    }

    private suspend fun queue(ctx: GameQueueContext): GameQueue {
        val queue = GameQueue()
        val block = suspend { ctx.block(queue) }
        block.launchCoroutine()
        return queue
    }

    private fun QueueType.overtake(other: QueueType): Boolean = when (this) {
        QueueType.Normal -> other == QueueType.Weak
        QueueType.Strong -> true
        else -> false
    }
}

internal data class GameQueueContext(
    val priority: QueueType,
    val block: suspend GameQueue.() -> Unit
)

internal interface GameQueueCondition {

    fun resume(): Boolean
}

internal class WaitCycleCondition(private var delay: Int) : GameQueueCondition {

    override fun resume(): Boolean {
        return delay-- <= 0
    }
}

internal class PredicateCondition(private val predicate: () -> Boolean) : GameQueueCondition {

    override fun resume(): Boolean {
        return predicate()
    }
}
