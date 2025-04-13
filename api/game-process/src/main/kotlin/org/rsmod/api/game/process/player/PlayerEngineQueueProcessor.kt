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
        // A "label" provides a secondary identifier within an engine queue, allowing for
        // more specific bindings. For example, this lets a script bind to `[advancestat,attack]`
        // even if there is already a more general `[advancestat,_]` binding.
        val label = queue.label
        if (label != null) {
            val packed = (label.toLong() shl 32) or queue.type.toLong()
            val trigger = eventBus.suspend[PlayerQueueEvents.EngineLabelled::class.java, packed]
            if (trigger != null) {
                val event = PlayerQueueEvents.EngineLabelled(packed)
                protectedAccess.launch(this) { eventBus.publish(this, event) }
                return
            }
        }

        val trigger = eventBus.suspend[PlayerQueueEvents.EngineDefault::class.java, queue.type]
        if (trigger != null) {
            val event = PlayerQueueEvents.EngineDefault(queue.type)
            protectedAccess.launch(this) { eventBus.publish(this, event) }
        }
    }
}
