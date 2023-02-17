package org.rsmod.plugins.api.prot.desktop

import org.openrs2.buffer.readByteC
import org.openrs2.buffer.readString
import org.openrs2.buffer.readUnsignedShortA
import org.openrs2.buffer.readUnsignedShortLEA
import org.rsmod.plugins.api.net.platform.GamePlatformPacketMaps
import org.rsmod.plugins.api.net.upstream.ClientCheat
import org.rsmod.plugins.api.net.upstream.EventAppletFocus
import org.rsmod.plugins.api.net.upstream.EventCameraPosition
import org.rsmod.plugins.api.net.upstream.EventKeyboard
import org.rsmod.plugins.api.net.upstream.EventMouseClick
import org.rsmod.plugins.api.net.upstream.EventMouseIdle
import org.rsmod.plugins.api.net.upstream.EventMouseMove
import org.rsmod.plugins.api.net.upstream.MapBuildComplete
import org.rsmod.plugins.api.net.upstream.MoveGameClick
import org.rsmod.plugins.api.net.upstream.NoTimeout
import org.rsmod.plugins.api.net.upstream.ReflectionCheckReply
import org.rsmod.plugins.api.net.upstream.WindowStatus

private val platforms: GamePlatformPacketMaps by inject()
private val packets = platforms.desktopUpstream

packets.register {
    opcode = 51
    length = 5
    decode { buf ->
        val mode = buf.readByte().toInt()
        val width = buf.readUnsignedShort()
        val height = buf.readUnsignedShort()
        return@decode WindowStatus(mode, width, height)
    }
}

packets.register {
    opcode = 95
    length = variableByteLength
    decode { buf ->
        val y = buf.readUnsignedShortLEA()
        val mode = buf.readByteC().toInt()
        val x = buf.readUnsignedShortA()
        return@decode MoveGameClick(mode, x, y)
    }
}

packets.register {
    opcode = 41
    length = variableByteLength
    decode { buf ->
        val text = buf.readString()
        return@decode ClientCheat(text)
    }
}

packets.register {
    opcode = 0
    length = 0
    decode { NoTimeout }
}

packets.register {
    opcode = 97
    length = 0
    decode { MapBuildComplete }
}

packets.register {
    opcode = 29
    length = variableByteLength
    decode { ReflectionCheckReply }
}

packets.register {
    opcode = 39
    length = 1
    decode { EventAppletFocus }
}

packets.register {
    opcode = 53
    length = 4
    decode { EventCameraPosition }
}

packets.register {
    opcode = 88
    length = 0
    decode { EventMouseIdle }
}

packets.register {
    opcode = 60
    length = variableByteLength
    decode { EventMouseMove }
}

packets.register {
    opcode = 55
    length = 6
    decode { EventMouseClick }
}

packets.register {
    opcode = 8
    length = variableShortLength
    decode { EventKeyboard }
}
