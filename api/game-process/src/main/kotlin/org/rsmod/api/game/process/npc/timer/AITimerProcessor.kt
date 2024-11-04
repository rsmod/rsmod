package org.rsmod.api.game.process.npc.timer

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcAIEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.npc.UnpackedNpcType

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
        publishEvent()
    }

    private fun Npc.publishEvent(type: UnpackedNpcType = currentType) {
        val typeTrigger = eventBus.keyed[NpcAIEvents.Type::class.java, type.id.toLong()]
        if (typeTrigger != null) {
            typeTrigger.invoke(NpcAIEvents.Type(this))
            return
        }

        if (type.contentGroup != -1) {
            val contentTrigger =
                eventBus.keyed[NpcAIEvents.Content::class.java, type.contentGroup.toLong()]
            if (contentTrigger != null) {
                contentTrigger.invoke(NpcAIEvents.Content(this, type.contentGroup))
                return
            }
        }

        eventBus.publish(NpcAIEvents.Default(this))
    }
}
