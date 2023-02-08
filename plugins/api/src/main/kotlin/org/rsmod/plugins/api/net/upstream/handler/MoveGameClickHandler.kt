package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.events.EventBus
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.api.net.upstream.MoveGameClick
import javax.inject.Inject

public class MoveGameClickHandler @Inject constructor(
    private val events: EventBus
) : UpstreamHandler<MoveGameClick>(MoveGameClick::class.java) {

    override fun handle(player: Player, packet: MoveGameClick) {
        val (mode, x, y) = packet
        val event = UpstreamEvent.MoveGameClick(
            player = player,
            mode = mode,
            x = x,
            y = y
        )
        events += event
    }
}
