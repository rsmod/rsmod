package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcQueueEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.queue.NpcQueueList
import org.rsmod.game.type.npc.UnpackedNpcType

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
                    publishEvent(queue)
                }
            }
            iterator.cleanUp()

            if (processedNone) {
                break
            }
        }
    }

    private fun Npc.publishEvent(queue: NpcQueueList.Queue, type: UnpackedNpcType = currentType) {
        val packedType = (type.id.toLong() shl 32) or queue.id.toLong()
        val typeTrigger = eventBus.keyed[NpcQueueEvents.Type::class.java, packedType]
        if (typeTrigger != null) {
            typeTrigger.invoke(NpcQueueEvents.Type(this, queue.args, queue.id))
            return
        }

        if (type.contentGroup != -1) {
            val packedContentGroup = (type.contentGroup.toLong() shl 32) or queue.id.toLong()
            val contentTrigger =
                eventBus.keyed[NpcQueueEvents.Content::class.java, packedContentGroup]
            if (contentTrigger != null) {
                contentTrigger.invoke(
                    NpcQueueEvents.Content(this, queue.args, type.contentGroup, queue.id)
                )
                return
            }
        }

        eventBus.publish(NpcQueueEvents.Default(this, queue.args, queue.id))
    }
}
