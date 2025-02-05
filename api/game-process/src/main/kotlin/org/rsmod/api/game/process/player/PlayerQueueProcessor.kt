package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.events.PlayerQueueEvents
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifClose
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.queue.PlayerQueueList
import org.rsmod.game.queue.QueueCategory

public class PlayerQueueProcessor
@Inject
constructor(private val eventBus: EventBus, private val protectedAccess: ProtectedAccessLauncher) {
    public fun process(player: Player) {
        if (player.queueList.strongQueues > 0) {
            player.ifClose(eventBus)
        }

        if (player.queueList.isNotEmpty) {
            player.queueList.decrementDelays()
            player.publishExpiredQueues()
        }

        if (player.weakQueueList.isNotEmpty) {
            player.weakQueueList.decrementDelays()
            player.publishExpiredWeakQueues()
        }
    }

    private fun PlayerQueueList.decrementDelays() {
        val iterator = iterator() ?: return
        while (iterator.hasNext()) {
            val queue = iterator.next()
            queue.remainingCycles--
        }
    }

    private fun Player.publishExpiredQueues() {
        while (queueList.isNotEmpty) {
            var processedNone = true

            val iterator = queueList.iterator() ?: break
            while (iterator.hasNext()) {
                val queue = iterator.next()

                if (queue.shouldCloseModals()) {
                    ifClose(eventBus)
                }

                if (queue.remainingCycles > 0) {
                    continue
                }

                if (canLaunchQueue(queue)) {
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

    private fun PlayerQueueList.Queue.shouldCloseModals(): Boolean =
        category == QueueCategory.Strong.id || category == QueueCategory.Soft.id

    private fun Player.canLaunchQueue(queue: PlayerQueueList.Queue): Boolean =
        queue.category == QueueCategory.Soft.id || !isAccessProtected

    private fun Player.publish(queue: PlayerQueueList.Queue) {
        if (queue.category == QueueCategory.Soft.id) {
            val event = PlayerQueueEvents.Soft(this, queue.args, queue.id)
            eventBus.publish(event)
            return
        }
        publishProtected(queue)
    }

    private fun Player.publishProtected(queue: PlayerQueueList.Queue) {
        val event = PlayerQueueEvents.Protected(queue.args, queue.id)
        protectedAccess.launch(this) { eventBus.publish(this, event) }
    }

    private fun Player.publishExpiredWeakQueues() {
        while (weakQueueList.isNotEmpty) {
            var processedNone = true

            val iterator = weakQueueList.iterator() ?: break
            while (iterator.hasNext()) {
                val queue = iterator.next()

                if (queue.remainingCycles > 0) {
                    continue
                }

                if (!isAccessProtected) {
                    processedNone = false
                    iterator.remove()
                    publishProtected(queue)
                }
            }
            iterator.cleanUp()

            if (processedNone) {
                break
            }
        }
    }
}
