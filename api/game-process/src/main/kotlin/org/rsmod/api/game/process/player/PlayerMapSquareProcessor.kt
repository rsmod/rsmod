package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.events.MapChangeEvent
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.map.square.MapSquareKey

public class PlayerMapSquareProcessor @Inject constructor(private val eventBus: EventBus) {
    public fun process(player: Player) {
        player.processMapSquareChange()
    }

    private fun Player.processMapSquareChange() {
        val previousMapSquare = MapSquareKey.from(previousCoords)
        val currentMapSquare = MapSquareKey.from(coords)
        if (currentMapSquare != previousMapSquare) {
            enterMapSquare(currentMapSquare, previousMapSquare)
        }
    }

    private fun Player.enterMapSquare(current: MapSquareKey, previous: MapSquareKey) {
        val enter = MapChangeEvent.MapSquareEnter(this, current)
        val leave = MapChangeEvent.MapSquareLeave(this, previous)
        eventBus.publish(enter)
        eventBus.publish(leave)
    }
}
