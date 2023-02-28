package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.api.net.upstream.MoveMinimapClick
import org.rsmod.plugins.api.publish
import javax.inject.Inject

public class MoveMinimapClickHandler @Inject constructor(
    private val eventBus: GameEventBus
) : UpstreamHandler<MoveMinimapClick>(MoveMinimapClick::class.java) {

    override fun handle(player: Player, packet: MoveMinimapClick) {
        val (mode, x, y) = packet
        val event = UpstreamEvent.MoveGameClick(player, mode, Coordinates(x, y))
        player.publish(event, eventBus)
    }
}
