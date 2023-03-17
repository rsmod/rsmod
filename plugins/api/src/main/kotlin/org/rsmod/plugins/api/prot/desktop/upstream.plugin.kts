package org.rsmod.plugins.api.prot.desktop

import io.netty.buffer.ByteBuf
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
import org.rsmod.plugins.api.net.upstream.IfButton
import org.rsmod.plugins.api.net.upstream.IfButton1
import org.rsmod.plugins.api.net.upstream.IfButton10
import org.rsmod.plugins.api.net.upstream.IfButton2
import org.rsmod.plugins.api.net.upstream.IfButton3
import org.rsmod.plugins.api.net.upstream.IfButton4
import org.rsmod.plugins.api.net.upstream.IfButton5
import org.rsmod.plugins.api.net.upstream.IfButton6
import org.rsmod.plugins.api.net.upstream.IfButton7
import org.rsmod.plugins.api.net.upstream.IfButton8
import org.rsmod.plugins.api.net.upstream.IfButton9
import org.rsmod.plugins.api.net.upstream.MapBuildComplete
import org.rsmod.plugins.api.net.upstream.MoveGameClick
import org.rsmod.plugins.api.net.upstream.MoveMinimapClick
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
        WindowStatus(mode, width, height)
    }
}

packets.register {
    opcode = 95
    length = variableByteLength
    decode { buf ->
        val z = buf.readUnsignedShortLEA()
        val mode = buf.readByteC().toInt()
        val x = buf.readUnsignedShortA()
        MoveGameClick(mode, x, z)
    }
}

packets.register {
    opcode = 13
    length = variableByteLength
    decode { buf ->
        val z = buf.readUnsignedShortLEA()
        val mode = buf.readByteC().toInt()
        val x = buf.readUnsignedShortA()
        val minimapPxOffX = buf.readByte().toInt()
        val minimapPxOffY = buf.readByte().toInt()
        val cameraAngle = buf.readUnsignedShort()
        buf.skipBytes(Byte.SIZE_BYTES * 4)
        val fineX = buf.readUnsignedShort()
        val fineY = buf.readUnsignedShort()
        buf.skipBytes(Byte.SIZE_BYTES)
        MoveMinimapClick(mode, x, z, fineX, fineY, minimapPxOffX, minimapPxOffY, cameraAngle)
    }
}

packets.register {
    opcode = 41
    length = variableByteLength
    decode { buf ->
        val text = buf.readString()
        ClientCheat(text)
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

packets.register {
    opcode = 16
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton1(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 37
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton2(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 12
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton3(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 19
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton4(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 20
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton5(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 52
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton6(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 77
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton7(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 23
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton8(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 22
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton9(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 6
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton10(component, dynamicChild, item)
    }
}

private val ByteBuf.readIfButton: IfButton get() {
    val component = readInt()
    val dynamicChild = readUnsignedShort()
    val item = readUnsignedShort()
    return IfButton(component, dynamicChild, item)
}
