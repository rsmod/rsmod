package org.rsmod.api.player.events

import org.rsmod.events.KeyedEvent
import org.rsmod.game.entity.Player
import org.rsmod.map.square.MapSquareKey

public sealed class MapChangeEvent : KeyedEvent {
    public data class MapSquareEnter(
        public val player: Player,
        public val mapSquare: MapSquareKey,
    ) : MapChangeEvent() {
        override val id: Long = mapSquare.id.toLong()
    }

    public data class MapSquareLeave(
        public val player: Player,
        public val mapSquare: MapSquareKey,
    ) : MapChangeEvent() {
        override val id: Long = mapSquare.id.toLong()
    }
}
