package org.rsmod.plugins.api.protocol.packet.client

import javax.inject.Inject
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.cmd.CommandArgs
import org.rsmod.game.cmd.CommandBlock
import org.rsmod.game.cmd.CommandMap
import org.rsmod.game.model.mob.Player
import org.rsmod.game.privilege.Privilege

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
        val cmd = commands[input] ?: return
        if (player.hasAnyPrivilege(cmd.privileges)) {
            val cmdArgs = CommandArgs(args)
            val block = CommandBlock(player, cmdArgs)
            cmd.execute(block)
        }
    }

    private fun Player.hasAnyPrivilege(other: Collection<Privilege>): Boolean {
        if (other.isEmpty()) return true
        return privileges.intersect(other).isNotEmpty()
    }
}
