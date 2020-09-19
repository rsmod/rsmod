package gg.rsmod.plugins.core.gameframe

import gg.rsmod.game.event.impl.LoginEvent
import gg.rsmod.game.model.mob.Player
import gg.rsmod.plugins.api.onLogin
import gg.rsmod.plugins.protocol.packet.server.IfOpenSub
import gg.rsmod.plugins.protocol.packet.server.IfOpenTop

onLogin(LoginEvent.Stage.Priority) {
    player.sendGameframe()
}

fun Player.sendGameframe() {
    write(IfOpenTop(548))
    write(IfOpenSub(122, ((548 shl 16) or 18), 1))
    write(IfOpenSub(163, ((548 shl 16) or 20), 1))
    write(IfOpenSub(160, ((548 shl 16) or 11), 1))
    write(IfOpenSub(162, ((548 shl 16) or 25), 1))
    write(IfOpenSub(593, ((548 shl 16) or 67), 1))
    write(IfOpenSub(320, ((548 shl 16) or 68), 1))
    write(IfOpenSub(399, ((548 shl 16) or 69), 1))
    write(IfOpenSub(149, ((548 shl 16) or 70), 1))
    write(IfOpenSub(387, ((548 shl 16) or 71), 1))
    write(IfOpenSub(541, ((548 shl 16) or 72), 1))
    write(IfOpenSub(218, ((548 shl 16) or 73), 1))
    write(IfOpenSub(7, ((548 shl 16) or 74), 1))
    write(IfOpenSub(109, ((548 shl 16) or 75), 1))
    write(IfOpenSub(429, ((548 shl 16) or 76), 1))
    write(IfOpenSub(182, ((548 shl 16) or 77), 1))
    write(IfOpenSub(261, ((548 shl 16) or 78), 1))
    write(IfOpenSub(216, ((548 shl 16) or 79), 1))
    write(IfOpenSub(239, ((548 shl 16) or 80), 1))
    flush()
}
