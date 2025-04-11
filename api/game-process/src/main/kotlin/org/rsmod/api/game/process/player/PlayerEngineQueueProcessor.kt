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
        val event = PlayerQueueEvents.Engine(queue.args, queue.id)
        protectedAccess.launch(this) { eventBus.publish(this, event) }
    }
}
