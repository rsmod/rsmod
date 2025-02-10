package org.rsmod.api.game.process.controller

import jakarta.inject.Inject
import org.rsmod.api.controller.access.StandardConAccessLauncher
import org.rsmod.api.controller.events.ControllerAIEvents
import org.rsmod.api.controller.events.ControllerQueueEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Controller
import org.rsmod.game.queue.NpcQueueList

public class ControllerQueueProcessor
@Inject
constructor(private val eventBus: EventBus, private val accessLauncher: StandardConAccessLauncher) {
    public fun process(controller: Controller) {
        controller.processAiQueue()
        controller.processQueueList()
    }

    private fun Controller.processAiQueue() {
        val queue = aiQueue ?: return
        queue.remainingCycles--
        if (queue.remainingCycles <= 0) {
            aiQueue = null
            publishAiEvent(queue)
        }
    }

    private fun Controller.publishAiEvent(queue: NpcQueueList.Queue) {
        val trigger = eventBus.keyed[ControllerAIEvents.Queue::class.java, id.toLong()]
        trigger?.invoke(ControllerAIEvents.Queue(this, queue.args))
    }

    private fun Controller.processQueueList() {
        if (queueList.isNotEmpty) {
            decrementQueueDelays()
            publishExpiredQueues()
        }
    }

    private fun Controller.decrementQueueDelays() {
        val iterator = queueList.iterator() ?: return
        while (iterator.hasNext()) {
            val queue = iterator.next()
            queue.remainingCycles--
        }
    }

    private fun Controller.publishExpiredQueues() {
        while (queueList.isNotEmpty) {
            var processedNone = true

            val iterator = queueList.iterator() ?: break
            while (iterator.hasNext()) {
                val queue = iterator.next()

                if (queue.remainingCycles > 0) {
                    continue
                }

                if (isNotDelayed) {
                    processedNone = false
                    iterator.remove()
                    publishEvent(queue)
                }
            }
            iterator.cleanUp()

            if (processedNone) {
                break
            }
        }
    }

    private fun Controller.publishEvent(queue: NpcQueueList.Queue) {
        val packedType = (id.toLong() shl 32) or queue.id.toLong()
        val typeTrigger = eventBus.suspend[ControllerQueueEvents.Type::class.java, packedType]
        if (typeTrigger != null) {
            val event = ControllerQueueEvents.Type(this, queue.args, queue.id)
            accessLauncher.launch(this) { typeTrigger(event) }
            return
        }
        val event = ControllerQueueEvents.Default(this, queue.args, queue.id)
        accessLauncher.launch(this) { eventBus.publish(this, event) }
    }
}
