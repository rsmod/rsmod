package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.events.EventBus
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.api.net.upstream.ClientCheat
import javax.inject.Inject

public class ClientCheatHandler @Inject constructor(
    private val events: EventBus
) : UpstreamHandler<ClientCheat>(ClientCheat::class.java) {

    override fun handle(player: Player, packet: ClientCheat) {
        val text = packet.text
        val args = packet.args
        val event = UpstreamEvent.ClientCheat(player, text, args)
        events += event
    }
}
