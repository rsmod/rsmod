package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcTimerEvents
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.timer.NpcTimerMap
import org.rsmod.game.type.npc.UnpackedNpcType

public class NpcTimerProcessor
@Inject
constructor(private val mapClock: MapClock, private val eventBus: EventBus) {
    public fun process(npc: Npc) {
        if (npc.timerMap.isEmpty) {
            return
        }
        npc.processTimers()
    }

    private fun Npc.processTimers() {
        val expired = timerMap.toExpiredList()
        for (entry in expired) {
            val timerType = entry.key
            timerMap -= timerType
            publishEvent(timerType.toInt())
        }
    }

    private fun NpcTimerMap.toExpiredList(): List<Map.Entry<Short, Int>> = filter {
        mapClock >= it.value
    }

    private fun Npc.publishEvent(timer: Int, type: UnpackedNpcType = currentType) {
        val packedType = (type.id.toLong() shl 32) or timer.toLong()
        val typeTrigger = eventBus.keyed[NpcTimerEvents.Type::class.java, packedType]
        if (typeTrigger != null) {
            typeTrigger.invoke(NpcTimerEvents.Type(this, timer))
            return
        }

        if (type.contentGroup != -1) {
            val packedContentGroup = (type.contentGroup.toLong() shl 32) or timer.toLong()
            val contentTrigger =
                eventBus.keyed[NpcTimerEvents.Content::class.java, packedContentGroup]
            if (contentTrigger != null) {
                contentTrigger.invoke(NpcTimerEvents.Content(this, type.contentGroup, timer))
                return
            }
        }

        eventBus.publish(NpcTimerEvents.Default(this, timer))
    }
}
