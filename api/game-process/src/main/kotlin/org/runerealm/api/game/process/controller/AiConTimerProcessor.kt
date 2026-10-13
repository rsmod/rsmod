package org.runerealm.api.game.process.controller

import jakarta.inject.Inject
import org.runerealm.api.controller.events.ControllerAIEvents
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Controller

public class AiConTimerProcessor @Inject constructor(private val eventBus: EventBus) {
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
        val trigger = eventBus.keyed[ControllerAIEvents.Timer::class.java, id]
        trigger?.invoke(ControllerAIEvents.Timer(this))
    }
}
