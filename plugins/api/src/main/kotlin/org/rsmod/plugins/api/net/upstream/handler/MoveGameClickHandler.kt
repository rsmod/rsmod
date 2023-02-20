package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.api.net.upstream.MoveGameClick
import org.rsmod.plugins.api.publish
import javax.inject.Inject

public class MoveGameClickHandler @Inject constructor(
    private val eventBus: GameEventBus
) : UpstreamHandler<MoveGameClick>(MoveGameClick::class.java) {

    override fun handle(player: Player, packet: MoveGameClick) {
        val (mode, x, y) = packet
        val event = UpstreamEvent.MoveGameClick(player, mode, Coordinates(x, y))
        player.publish(event, eventBus)
    }
}
