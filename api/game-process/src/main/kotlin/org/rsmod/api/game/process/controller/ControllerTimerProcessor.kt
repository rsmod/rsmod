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
        if (controller.timerMap.isNotEmpty) {
            controller.processTimers()
        }
    }

    private fun Controller.processTimers() {
        val expired = timerMap.incrementCountersAndGetExpiredKeys()
        for (timerType in expired) {
            publishEvent(timerType.toInt())
        }
    }

    private fun NpcTimerMap.incrementCountersAndGetExpiredKeys(): Set<Short> {
        expiredKeysBuffer.clear()
        for (entry in this) {
            // Note: Counter is incremented _before_ being checked against its interval.
            var counter = extractClockCounter(entry.longValue) + 1
            val interval = extractInterval(entry.longValue)
            if (counter >= interval) {
                expiredKeysBuffer.add(entry.shortKey)
                counter = 0
            }
            val packed = packValues(clockCounter = counter, interval = interval)
            entry.setValue(packed)
        }
        return expiredKeysBuffer
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
