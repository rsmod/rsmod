package org.rsmod.api.game.process.npc.timer

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcAIEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc

public class AITimerProcessor @Inject constructor(private val eventBus: EventBus) {
    public fun process(npc: Npc) {
        if (npc.aiTimerCycles == -1) {
            return
        }
        npc.processTimer()
    }

    private fun Npc.processTimer() {
        aiTimerCycles--

        if (aiTimerCycles > 0) {
            return
        }

        // NOTE: This assumes npc type timer has a default value of -1 when not manually configured.
        aiTimerCycles = type.timer

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
