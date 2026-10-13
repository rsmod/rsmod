package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.misc.user.ClientCheat
import org.runerealm.game.cheat.CheatCommandMap
import org.runerealm.game.entity.Player

class ClientCheatHandler @Inject constructor(private val commands: CheatCommandMap) :
    MessageHandler<ClientCheat> {
    override fun handle(player: Player, message: ClientCheat) {
        val text = message.command.lowercase()
        val split = text.split(" ")
        val command = split[0]
        val args = if (split.size > 1) split.subList(1, split.size) else emptyList()
        commands.execute(player, command, args)
    }
}
