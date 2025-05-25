package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.npc.events.AiQueueEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.queue.AiQueueType
import org.rsmod.game.type.npc.UnpackedNpcType

public class AiQueueProcessor @Inject constructor(private val eventBus: EventBus) {
    public fun process(npc: Npc) {
        if (npc.pendingAiCycle <= 0) {
            return
        }
        npc.processQueue()
    }

    private fun Npc.processQueue() {
        pendingAiCycle--
        if (pendingAiCycle > 0) {
            return
        }
        val aiQueue = checkNotNull(pendingAiQueue)
        clearPendingAiQueue()
        publishEvent(aiQueue)
    }

    private fun Npc.publishEvent(queue: AiQueueType, type: UnpackedNpcType = visType) {
        val typeTrigger = eventBus.keyed[AiQueueEvents.Type::class.java, type.id]
        if (typeTrigger != null) {
            typeTrigger.invoke(AiQueueEvents.Type(this, queue.id))
            return
        }

        if (type.contentGroup != -1) {
            val contentTrigger =
                eventBus.keyed[AiQueueEvents.Content::class.java, type.contentGroup]
            if (contentTrigger != null) {
                contentTrigger.invoke(AiQueueEvents.Content(this, queue.id, type.contentGroup))
                return
            }
        }

        eventBus.publish(AiQueueEvents.Default(this, queue.id))
    }
}
