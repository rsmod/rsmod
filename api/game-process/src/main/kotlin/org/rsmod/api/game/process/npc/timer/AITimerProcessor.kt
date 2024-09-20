package org.rsmod.api.game.process.npc.timer

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcAIEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc

public class AITimerProcessor @Inject constructor(private val eventBus: EventBus) {
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

        val typeTrigger = eventBus.keyed[NpcAIEvents.Type::class.java, id.toLong()]
        if (typeTrigger != null) {
            typeTrigger.invoke(NpcAIEvents.Type(this))
            return
        }

        if (type.contentType != -1) {
            val contentTrigger =
                eventBus.keyed[NpcAIEvents.Content::class.java, type.contentType.toLong()]
            if (contentTrigger != null) {
                contentTrigger.invoke(NpcAIEvents.Content(this, type.contentType))
                return
            }
        }

        eventBus.publish(NpcAIEvents.Default(this))
    }
}
