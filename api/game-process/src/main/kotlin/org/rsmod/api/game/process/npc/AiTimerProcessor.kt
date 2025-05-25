package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.npc.events.AiTimerEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.npc.UnpackedNpcType

public class AiTimerProcessor @Inject constructor(private val eventBus: EventBus) {
    public fun process(npc: Npc) {
        if (npc.aiTimer <= 0) {
            return
        }
        npc.processTimer()
    }

    private fun Npc.processTimer() {
        aiTimer--

        if (aiTimer > 0) {
            return
        }

        aiTimer = aiTimerStart
        publishEvent()
    }

    private fun Npc.publishEvent(type: UnpackedNpcType = visType) {
        val typeTrigger = eventBus.keyed[AiTimerEvents.Type::class.java, type.id]
        if (typeTrigger != null) {
            typeTrigger.invoke(AiTimerEvents.Type(this))
            return
        }

        if (type.contentGroup != -1) {
            val contentTrigger =
                eventBus.keyed[AiTimerEvents.Content::class.java, type.contentGroup]
            if (contentTrigger != null) {
                contentTrigger.invoke(AiTimerEvents.Content(this, type.contentGroup))
                return
            }
        }

        eventBus.publish(AiTimerEvents.Default(this))
    }
}
