package org.rsmod.plugins.content.gameframe

import org.rsmod.game.config.GameConfig
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.mob.player.MessageType
import org.rsmod.plugins.api.model.mob.player.runClientScript
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.model.mob.player.sendRunEnergy
import org.rsmod.plugins.api.model.mob.player.setVarp
import org.rsmod.plugins.api.model.ui.gameframe.GameframeFixed
import org.rsmod.plugins.api.model.ui.gameframe.GameframeList
import org.rsmod.plugins.api.model.ui.openGameframe
import org.rsmod.plugins.api.onEarlyLogin
import org.rsmod.plugins.api.onLogin
import org.rsmod.plugins.api.protocol.packet.server.ResetAnims
import org.rsmod.plugins.api.protocol.packet.server.ResetClientVarCache

val config: GameConfig by inject()
val frames: GameframeList by inject()

val varp1 = varp("varp_1055")
val varp2 = varp("varp_1737")

onLogin {
    player.sendLogin()
}

onEarlyLogin {
    val fixedFrame = frames.getValue(GameframeFixed)
    player.openGameframe(fixedFrame)
    player.sendEarlyLogin()
}

fun Player.sendLogin() {
    sendMessage("Welcome to ${config.name}.", MessageType.WELCOME)
    write(ResetClientVarCache)
    write(ResetAnims)
}

fun Player.sendEarlyLogin() {
    setVarp(varp1, 0)
    setVarp(varp2, -1)
    runClientScript(1105, 1)
    sendRunEnergy()
}
