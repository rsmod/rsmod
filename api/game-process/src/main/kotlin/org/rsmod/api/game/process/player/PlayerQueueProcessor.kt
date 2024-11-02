package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.events.PlayerQueueEvent
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
            player.processQueues()
        }

        if (player.weakQueueList.isNotEmpty) {
            player.processWeakQueues()
        }
    }

    private fun Player.processQueues() {
        while (queueList.isNotEmpty) {
            var processedNone = true

            val iterator = queueList.iterator() ?: break
            while (iterator.hasNext()) {
                val queue = iterator.next()

                if (queue.shouldCloseModals()) {
                    ifClose(eventBus)
                }

                if (queue.remainingCycles > 0) {
                    queue.remainingCycles--
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
            val event = PlayerQueueEvent.Soft(this, queue.id)
            eventBus.publish(event)
            return
        }
        publishProtected(queue)
    }

    private fun Player.publishProtected(queue: PlayerQueueList.Queue) {
        val event = PlayerQueueEvent.Protected(queue.id)
        protectedAccess.launch(this) { eventBus.publish(this, event) }
    }

    private fun Player.processWeakQueues() {
        while (weakQueueList.isNotEmpty) {
            var processedNone = true

            val iterator = weakQueueList.iterator() ?: break
            while (iterator.hasNext()) {
                val queue = iterator.next()

                if (queue.remainingCycles > 0) {
                    queue.remainingCycles--
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
