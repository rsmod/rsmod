package org.rsmod.api.game.process.controller

import jakarta.inject.Inject
import org.rsmod.api.controller.ControllerTimerEvents
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Controller
import org.rsmod.game.timer.NpcTimerMap

public class ControllerTimerProcessor
@Inject
constructor(private val mapClock: MapClock, private val eventBus: EventBus) {
    public fun process(controller: Controller) {
        if (controller.timerMap.isEmpty) {
            return
        }
        controller.processTimers()
    }

    private fun Controller.processTimers() {
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

    private fun Controller.publishEvent(timer: Int) {
        val packedType = (id.toLong() shl 32) or timer.toLong()
        val typeTrigger = eventBus.keyed[ControllerTimerEvents.Type::class.java, packedType]
        if (typeTrigger != null) {
            typeTrigger.invoke(ControllerTimerEvents.Type(this, timer))
            return
        }
        eventBus.publish(ControllerTimerEvents.Default(this, timer))
    }
}
