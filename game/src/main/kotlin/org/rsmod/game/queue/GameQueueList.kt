package org.rsmod.game.queue

import kotlinx.coroutines.withContext
import org.rsmod.game.coroutine.GameCoroutineTask
import java.util.LinkedList
import java.util.Queue

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
