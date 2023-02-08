package org.rsmod.plugins.api.model.event

import org.rsmod.game.events.Event
import org.rsmod.game.model.mob.Player

public sealed class UpstreamEvent : Event {

    public data class MoveGameClick(
        val player: Player,
        val mode: Int,
        val x: Int,
        val y: Int
    ) : UpstreamEvent()
}
