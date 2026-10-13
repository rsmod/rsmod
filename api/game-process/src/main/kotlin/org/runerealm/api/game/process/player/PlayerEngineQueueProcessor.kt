package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.runerealm.api.player.events.EngineQueueEvents
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.queue.EngineQueueCache
import org.runerealm.game.queue.EngineQueueList

public class PlayerEngineQueueProcessor
@Inject
constructor(
    private val eventBus: EventBus,
    private val queueCache: EngineQueueCache,
    private val protectedAccess: ProtectedAccessLauncher,
) {
    public fun process(player: Player) {
        player.publishQueues()
    }

    private fun Player.publishQueues() {
        val iterator = engineQueueList.iterator() ?: return
        while (iterator.hasNext()) {
            val queue = iterator.next()
            if (!isAccessProtected) {
                iterator.remove()
                publish(queue)
            }
        }
        iterator.cleanUp()
    }

    private fun Player.publish(queue: EngineQueueList.Queue) {
        if (queueCache.hasLabelScript(queue)) {
            val packedLabel = EventBus.composeLongKey(queue.label, queue.type)
            val labelled = eventBus.suspend[EngineQueueEvents.Labelled::class.java, packedLabel]
            if (labelled != null) {
                val event = EngineQueueEvents.Labelled(packedLabel)
                protectedAccess.launch(this) { eventBus.publish(this, event) }
                return
            }
        }

        if (queueCache.hasDefaultScript(queue)) {
            val default = eventBus.suspend[EngineQueueEvents.Default::class.java, queue.type]
            if (default != null) {
                val event = EngineQueueEvents.Default(queue.args, queue.type)
                protectedAccess.launch(this) { eventBus.publish(this, event) }
            }
        }
    }
}
