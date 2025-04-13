package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.events.MapChangeEvent
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.zone.ZoneKey

public class PlayerMapUpdateProcessor
@Inject
constructor(private val eventBus: EventBus, private val playerRegistry: PlayerRegistry) {
    public fun process(player: Player) {
        player.processMapSquareChange()
        player.processZoneChange()
    }

    private fun Player.processMapSquareChange() {
        val previousMapSquare = MapSquareKey.from(previousCoords)
        val currentMapSquare = MapSquareKey.from(coords)
        if (currentMapSquare != previousMapSquare) {
            enterMapSquare(previousMapSquare, currentMapSquare)
        }
    }

    private fun Player.enterMapSquare(previous: MapSquareKey, current: MapSquareKey) {
        val enter = MapChangeEvent.MapSquareEnter(this, current)
        val leave = MapChangeEvent.MapSquareLeave(this, previous)
        eventBus.publish(enter)
        eventBus.publish(leave)
    }

    private fun Player.processZoneChange() {
        val currZone = ZoneKey.from(coords)
        val prevZone = lastProcessedZone
        if (currZone != prevZone) {
            enterZone(prevZone, currZone)
        }
    }

    private fun Player.enterZone(previous: ZoneKey, current: ZoneKey) {
        playerRegistry.change(this, from = previous, to = current)
    }
}
