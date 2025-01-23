package org.rsmod.api.game.process.controller

import jakarta.inject.Inject
import org.rsmod.api.controller.events.ControllerAIEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Controller

public class AIConTimerProcessor @Inject constructor(private val eventBus: EventBus) {
    public fun process(controller: Controller) {
        if (controller.aiTimer <= 0) {
            return
        }
        controller.processTimer()
    }

    private fun Controller.processTimer() {
        aiTimer--

        if (aiTimer > 0) {
            return
        }

        aiTimer = aiTimerStart
        publishEvent()
    }

    private fun Controller.publishEvent() {
        val trigger = eventBus.keyed[ControllerAIEvents.Timer::class.java, id.toLong()]
        trigger?.invoke(ControllerAIEvents.Timer(this))
    }
}
