package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.api.net.upstream.MoveGameClick
import org.rsmod.plugins.api.publish

public class MoveGameClickHandler : UpstreamHandler<MoveGameClick>(MoveGameClick::class.java) {

    override fun handle(player: Player, packet: MoveGameClick) {
        val (mode, x, z) = packet
        val speed = UpstreamEvent.MoveGameClick.speedRequest(mode)
        val event = UpstreamEvent.MoveGameClick(speed, Coordinates(x, z))
        player.publish(event)
    }
}
