package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.api.net.upstream.ClientCheat
import org.rsmod.plugins.api.publish

public class ClientCheatHandler : UpstreamHandler<ClientCheat>(ClientCheat::class.java) {

    override fun handle(player: Player, packet: ClientCheat) {
        val text = packet.text
        val args = packet.args
        val event = UpstreamEvent.ClientCheat(text, args)
        player.publish(event)
    }
}
