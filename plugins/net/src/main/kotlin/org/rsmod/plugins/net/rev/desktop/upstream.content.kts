package org.rsmod.plugins.net.rev.desktop

import org.rsmod.plugins.net.game.upstream.NoTimeout
import org.rsmod.plugins.net.game.upstream.WindowStatus
import org.rsmod.plugins.net.rev.platform.GamePlatformPacketMaps

val platforms: GamePlatformPacketMaps by inject()
val packets = platforms.desktopUpstream

packets.register<NoTimeout> {
    opcode = 0
    length = 0
    decode { NoTimeout() }
}

packets.register<WindowStatus> {
    opcode = 51
    length = 5
    decode { buf ->
        val mode = buf.readByte().toInt()
        val width = buf.readUnsignedShort()
        val height = buf.readUnsignedShort()
        return@decode WindowStatus(mode, width, height)
    }
}
