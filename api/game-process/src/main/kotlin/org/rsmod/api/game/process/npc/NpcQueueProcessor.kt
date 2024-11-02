package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcQueueEvent
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.queue.NpcQueueList

public class NpcQueueProcessor @Inject constructor(private val eventBus: EventBus) {
    public fun process(npc: Npc) {
        if (npc.queueList.isNotEmpty) {
            npc.processQueues()
        }
    }

    private fun Npc.processQueues() {
        while (queueList.isNotEmpty) {
            var processedNone = true

            val iterator = queueList.iterator() ?: break
            while (iterator.hasNext()) {
                val queue = iterator.next()

                if (queue.remainingCycles > 0) {
                    queue.remainingCycles--
                    continue
                }

                if (!isBusy) {
                    processedNone = false
                    iterator.remove()
                    publish(queue)
                }
            }
            iterator.cleanUp()

            if (processedNone) {
                break
            }
        }
    }

    private fun Npc.publish(queue: NpcQueueList.Queue) {
        val event = NpcQueueEvent(this, queue.id)
        eventBus.publish(event)
    }
}
