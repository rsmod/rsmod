package gg.rsmod.plugins.core.protocol.packet.client

import com.google.inject.Inject
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.event.impl.CommandEvent
import gg.rsmod.game.message.ClientPacket
import gg.rsmod.game.message.ClientPacketHandler
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.mob.Player

data class ClientCheat(
    val input: String
) : ClientPacket

class ClientCheatHandler @Inject constructor(
    private val eventBus: EventBus
) : ClientPacketHandler<ClientCheat> {

    override fun handle(client: Client, player: Player, packet: ClientCheat) {
        val input = packet.input
        if (input.isBlank()) {
            return
        }
        val split = input.split(" ")
        val command = split[0]
        val args = if (split.size == 1) emptyList() else split.subList(1, split.size)
        val event = CommandEvent(player, command, args)
        eventBus.publish(event)
    }
}
