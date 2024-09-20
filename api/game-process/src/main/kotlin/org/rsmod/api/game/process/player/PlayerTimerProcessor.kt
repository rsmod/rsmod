package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.events.PlayerTimerEvent
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Player
import org.rsmod.game.timer.PlayerTimerMap

public class PlayerTimerProcessor
@Inject
constructor(
    private val mapClock: MapClock,
    private val eventBus: EventBus,
    private val protectedAccess: ProtectedAccessLauncher,
) {
    public fun process(player: Player) {
        if (player.timerMap.isNotEmpty && !player.isAccessProtected) {
            player.processTimers()
        }
        if (player.softTimerMap.isNotEmpty) {
            player.processSoftTimers()
        }
    }

    private fun Player.processTimers() {
        val expired = timerMap.toExpiredList()
        for (entry in expired) {
            // Normal timers require protected access, so if player cannot grant it, we break early.
            if (isAccessProtected) {
                break
            }
            val timerType = entry.key
            timerMap -= timerType

            val event = PlayerTimerEvent.Normal(timerType.toInt())
            protectedAccess.launch(this) { eventBus.publish(this, event) }
        }
    }

    private fun Player.processSoftTimers() {
        val expired = softTimerMap.toExpiredList()
        for (entry in expired) {
            val timerType = entry.key
            softTimerMap -= timerType

            val event = PlayerTimerEvent.Soft(this, timerType.toInt())
            eventBus.publish(event)
        }
    }

    private fun PlayerTimerMap.toExpiredList(): List<Map.Entry<Short, Int>> = filter {
        mapClock >= it.value
    }
}
