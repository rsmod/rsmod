package org.rsmod.plugins.api.protocol.packet.client

import com.google.inject.Inject
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.cmd.CommandArgs
import org.rsmod.game.cmd.CommandMap
import org.rsmod.game.model.mob.Player

data class ClientCheat(
    val input: String
) : ClientPacket

class ClientCheatHandler @Inject constructor(
    private val commands: CommandMap
) : ClientPacketHandler<ClientCheat> {

    override fun handle(client: Client, player: Player, packet: ClientCheat) {
        val message = packet.input
        if (message.isBlank()) {
            return
        }
        val split = message.split(" ")
        val input = split[0].toLowerCase()
        val args = if (split.size == 1) emptyList() else split.subList(1, split.size)
        val cmd = commands[input]
        if (cmd != null && player.eligibleRank(cmd.rank)) {
            val cmdArgs = CommandArgs(args)
            val invoke = cmd.execute
            invoke(player, cmdArgs)
        }
    }
}
