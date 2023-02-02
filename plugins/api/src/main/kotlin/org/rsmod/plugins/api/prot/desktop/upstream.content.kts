package org.rsmod.plugins.api.prot.desktop

import org.rsmod.plugins.api.net.platform.GamePlatformPacketMaps
import org.rsmod.plugins.api.net.upstream.NoTimeout
import org.rsmod.plugins.api.net.upstream.WindowStatus

private val platforms: GamePlatformPacketMaps by inject()
private val packets = platforms.desktopUpstream

packets.register<NoTimeout> {
    opcode = 0
    length = 0
    decode { NoTimeout }
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
