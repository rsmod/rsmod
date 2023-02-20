package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.api.net.upstream.ClientCheat
import org.rsmod.plugins.api.publish
import javax.inject.Inject

public class ClientCheatHandler @Inject constructor(
    private val eventBus: GameEventBus
) : UpstreamHandler<ClientCheat>(ClientCheat::class.java) {

    override fun handle(player: Player, packet: ClientCheat) {
        val text = packet.text
        val args = packet.args
        val event = UpstreamEvent.ClientCheat(player, text, args)
        player.publish(event, eventBus)
    }
}
