package org.runerealm.api.game.process.npc

import jakarta.inject.Inject
import org.runerealm.api.npc.events.AiQueueEvents
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Npc
import org.runerealm.game.queue.AiQueueType
import org.runerealm.game.type.npc.UnpackedNpcType

public class AiQueueProcessor @Inject constructor(private val eventBus: EventBus) {
    public fun process(npc: Npc) {
        if (npc.pendingAiQueueCycle <= 0) {
            return
        }
        npc.processQueue()
    }

    private fun Npc.processQueue() {
        pendingAiQueueCycle--
        if (pendingAiQueueCycle > 0) {
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
