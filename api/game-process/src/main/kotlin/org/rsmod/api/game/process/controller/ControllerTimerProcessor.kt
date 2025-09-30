package org.rsmod.api.game.process.controller

import jakarta.inject.Inject
import org.rsmod.api.controller.access.StandardConAccessLauncher
import org.rsmod.api.controller.events.ControllerTimerEvents
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Controller

public class ControllerTimerProcessor
@Inject
constructor(
    private val mapClock: MapClock,
    private val eventBus: EventBus,
    private val accessLauncher: StandardConAccessLauncher,
) {
    public fun process(controller: Controller) {
        if (controller.timerMap.isNotEmpty) {
            controller.processTimers()
        }
    }

    private fun Controller.processTimers() {
        for (entry in timerMap) {
            if (isDelayed) {
                break
            }
            val timerType = entry.shortKey
            // Note: Counter is incremented _before_ being checked against its interval.
            var counter = timerMap.extractClockCounter(entry.longValue) + 1
            val interval = timerMap.extractInterval(entry.longValue)
            val publish = counter >= interval
            if (publish) {
                counter = 0
            }
            val packed = timerMap.packValues(clockCounter = counter, interval = interval)
            entry.setValue(packed)
            if (publish) {
                publishEvent(timerType.toInt())
            }
        }
    }

    private fun Controller.publishEvent(timer: Int) {
        val packedType = EventBus.composeLongKey(id, timer)
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
