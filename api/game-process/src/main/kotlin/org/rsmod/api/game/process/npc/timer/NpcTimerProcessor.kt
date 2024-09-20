package org.rsmod.api.game.process.npc.timer

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcTimerEvent
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.timer.NpcTimerMap

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

            val event = NpcTimerEvent(this, timerType.toInt())
            eventBus.publish(event)
        }
    }

    private fun NpcTimerMap.toExpiredList(): List<Map.Entry<Short, Int>> = filter {
        mapClock >= it.value
    }
}
