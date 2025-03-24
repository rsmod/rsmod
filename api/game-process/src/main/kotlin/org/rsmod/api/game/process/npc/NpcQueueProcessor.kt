package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.npc.access.StandardNpcAccessLauncher
import org.rsmod.api.npc.events.NpcQueueEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.queue.NpcQueueList
import org.rsmod.game.type.npc.UnpackedNpcType

public class NpcQueueProcessor
@Inject
constructor(private val eventBus: EventBus, private val accessLauncher: StandardNpcAccessLauncher) {
    public fun process(npc: Npc) {
        npc.publishExpiredQueues()
    }

    private fun Npc.publishExpiredQueues() {
        while (queueList.isNotEmpty) {
            var processedNone = true

            val iterator = queueList.iterator() ?: break
            while (iterator.hasNext()) {
                val queue = iterator.next()

                if (queue.processedCycle != currentMapClock) {
                    queue.processedCycle = currentMapClock
                    queue.remainingCycles--
                }

                if (queue.remainingCycles > 0) {
                    continue
                }

                if (!isBusy) {
                    processedNone = false
                    iterator.remove()
                    publishEvent(queue)
                }
            }
            iterator.cleanUp()

            if (processedNone || queueList.size == 1) {
                break
            }
        }
    }

    private fun Npc.publishEvent(queue: NpcQueueList.Queue, type: UnpackedNpcType = visType) {
        val packedType = (type.id.toLong() shl 32) or queue.id.toLong()
        val typeTrigger = eventBus.suspend[NpcQueueEvents.Type::class.java, packedType]
        if (typeTrigger != null) {
            val event = NpcQueueEvents.Type(this, queue.args, queue.id)
            accessLauncher.launch(this) { typeTrigger(event) }
            return
        }

        if (type.contentGroup != -1) {
            val packedContentGroup = (type.contentGroup.toLong() shl 32) or queue.id.toLong()
            val contentTrigger =
                eventBus.suspend[NpcQueueEvents.Content::class.java, packedContentGroup]
            if (contentTrigger != null) {
                val event = NpcQueueEvents.Content(this, queue.args, type.contentGroup, queue.id)
                accessLauncher.launch(this) { contentTrigger(event) }
                return
            }
        }

        val event = NpcQueueEvents.Default(this, queue.args, queue.id)
        accessLauncher.launch(this) { eventBus.publish(this, event) }
    }
}
