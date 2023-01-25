package org.rsmod.plugins.net.rev.desktop

import org.openrs2.buffer.writeShortA
import org.rsmod.plugins.net.game.downstream.IfOpenTop
import org.rsmod.plugins.net.rev.platform.GamePlatformPacketMaps

val platforms: GamePlatformPacketMaps by inject()
val packets = platforms.desktopDownstream

packets.register<IfOpenTop> {
    opcode = 10
    length = 2
    encode { packet, buf ->
        buf.writeShortA(packet.interfaceId)
    }
}
