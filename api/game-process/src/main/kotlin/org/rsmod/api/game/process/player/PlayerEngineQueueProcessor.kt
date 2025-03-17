package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.events.PlayerQueueEvents
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.queue.EngineQueueList

public class PlayerEngineQueueProcessor
@Inject
constructor(private val eventBus: EventBus, private val protectedAccess: ProtectedAccessLauncher) {
    public fun process(player: Player) {
        player.publishQueues()
    }

    // TODO: Should engine queues have the same iteration logic as normal queues?
    private fun Player.publishQueues() {
        while (engineQueueList.isNotEmpty) {
            var processedNone = true

            val iterator = engineQueueList.iterator() ?: break
            while (iterator.hasNext()) {
                val queue = iterator.next()
                if (!isAccessProtected) {
                    processedNone = false
                    iterator.remove()
                    publish(queue)
                }
            }
            iterator.cleanUp()

            if (processedNone) {
                break
            }
        }
    }

    private fun Player.publish(queue: EngineQueueList.Queue) {
        val event = PlayerQueueEvents.Engine(queue.args, queue.id)
        protectedAccess.launch(this) { eventBus.publish(this, event) }
    }
}
