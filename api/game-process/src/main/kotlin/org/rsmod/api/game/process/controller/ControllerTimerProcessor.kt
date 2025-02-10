package org.rsmod.api.game.process.controller

import jakarta.inject.Inject
import org.rsmod.api.controller.access.StandardConAccessLauncher
import org.rsmod.api.controller.events.ControllerTimerEvents
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Controller
import org.rsmod.game.timer.NpcTimerMap

public class ControllerTimerProcessor
@Inject
constructor(
    private val mapClock: MapClock,
    private val eventBus: EventBus,
    private val accessLauncher: StandardConAccessLauncher,
) {
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
        val typeTrigger = eventBus.suspend[ControllerTimerEvents.Type::class.java, packedType]
        if (typeTrigger != null) {
            val event = ControllerTimerEvents.Type(this, timer)
            accessLauncher.launch(this) { typeTrigger(event) }
            return
        }
        val event = ControllerTimerEvents.Default(this, timer)
        accessLauncher.launch(this) { eventBus.publish(this, event) }
    }
}
