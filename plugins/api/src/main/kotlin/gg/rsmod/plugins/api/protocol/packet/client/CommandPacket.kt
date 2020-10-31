package gg.rsmod.plugins.api.protocol.packet.client

import com.google.inject.Inject
import gg.rsmod.game.message.ClientPacket
import gg.rsmod.game.message.ClientPacketHandler
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.cmd.CommandArgs
import gg.rsmod.game.cmd.CommandMap
import gg.rsmod.game.model.mob.Player

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
