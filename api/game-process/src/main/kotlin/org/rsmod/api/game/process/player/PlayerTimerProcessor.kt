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
        if (player.timerMap.isNotEmpty) {
            player.processTimers()
        }
        if (player.softTimerMap.isNotEmpty) {
            player.processSoftTimers()
        }
    }

    private fun Player.processTimers() {
        // Early return to avoid redundant expired key filtering.
        if (isAccessProtected) {
            return
        }

        val expired = timerMap.toExpiredKeys()
        for (timerType in expired) {
            if (isAccessProtected) {
                break
            }
            val event = PlayerTimerEvent.Normal(timerType.toInt())
            protectedAccess.launch(this) { eventBus.publish(this, event) }

            // `packedValue` holds the packed (expiry << 32 | interval) if the timer is still
            // present. If it's `null` it means the script cleared the timer.
            val packedValue = timerMap[timerType]
            if (packedValue != null) {
                val interval = timerMap.extractInterval(packedValue)
                timerMap.put(timerType, mapClock = mapClock.cycle, interval = interval)
            }
        }
    }

    private fun Player.processSoftTimers() {
        val expired = softTimerMap.toExpiredKeys()
        for (timerType in expired) {
            val event = PlayerTimerEvent.Soft(this, timerType.toInt())
            eventBus.publish(event)

            // `packedValue` holds the packed (expiry << 32 | interval) if the timer is still
            // present. If it's `null` it means the script cleared the timer.
            val packedValue = softTimerMap[timerType]
            if (packedValue != null) {
                val interval = softTimerMap.extractInterval(packedValue)
                softTimerMap.put(timerType, mapClock = mapClock.cycle, interval = interval)
            }
        }
    }

    private fun PlayerTimerMap.toExpiredKeys(): Set<Short> {
        expiredKeysBuffer.clear()
        for (entry in this) {
            val expiry = extractExpiry(entry.longValue)
            if (mapClock >= expiry) {
                expiredKeysBuffer.add(entry.shortKey)
            }
        }
        return expiredKeysBuffer
    }
}
