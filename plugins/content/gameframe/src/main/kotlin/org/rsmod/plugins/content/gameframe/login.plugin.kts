package org.rsmod.plugins.content.gameframe

import org.rsmod.game.config.GameConfig
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.mob.player.MessageType
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.onLogin
import org.rsmod.plugins.api.protocol.packet.server.ResetAnims
import org.rsmod.plugins.api.protocol.packet.server.ResetClientVarCache

val config: GameConfig by inject()

onLogin { player.sendLogin() }

fun Player.sendLogin() {
    sendMessage("Welcome to ${config.name}.", MessageType.WELCOME)
    write(ResetClientVarCache)
    write(ResetAnims)
}
