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

        // TODO: Might be a good idea to check that `aiTimerCycles` isn't set to 0 at this point.
        // This could easily lead to perf degradation if npcs are (mistakenly) set to have a default
        // timer of 0, but never assigned an ai timer event that sets it to a greater value. This
        // would cause the aiTimerCycles to continue calling this logic every single cycle.
        // Have to find out if there's ever a case where an npc mechanic would require their ai
        // timer to be called every single cycle.
    }
}
