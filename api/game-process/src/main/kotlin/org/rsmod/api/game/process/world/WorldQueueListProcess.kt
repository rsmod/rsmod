package org.rsmod.api.game.process.world

import jakarta.inject.Inject
import org.rsmod.game.queue.WorldQueueList

public class WorldQueueListProcess @Inject constructor(private val queues: WorldQueueList) {
    public fun process() {
        if (queues.isNotEmpty) {
            queues.process()
        }
    }

    private fun WorldQueueList.process() {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val queue = iterator.next()

            queue.remainingCycles--

            if (queue.remainingCycles <= 0) {
                iterator.remove()
                queue.action()
            }
        }
        iterator.cleanUp()
    }
}
