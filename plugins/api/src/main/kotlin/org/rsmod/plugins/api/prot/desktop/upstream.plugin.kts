package org.rsmod.plugins.api.prot.desktop

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.rsmod.plugins.api.net.platform.game.GamePlatformPacketMaps
import org.rsmod.plugins.api.net.readUnsignedByteAlt1
import org.rsmod.plugins.api.net.readUnsignedShortAlt2
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
    opcode = 12
    length = 5
    decode { buf ->
        val mode = buf.readUnsignedByte().toInt()
        val width = buf.readUnsignedShort()
        val height = buf.readUnsignedShort()
        WindowStatus(mode, width, height)
    }
}

packets.register {
    opcode = 34
    length = variableByteLength
    decode { buf ->
        val z = buf.readUnsignedShortAlt2()
        val x = buf.readUnsignedShort()
        val mode = buf.readUnsignedByteAlt1().toInt()
        MoveGameClick(mode, x, z)
    }
}

packets.register {
    opcode = 37
    length = variableByteLength
    decode { buf ->
        val z = buf.readUnsignedShortAlt2()
        val x = buf.readUnsignedShort()
        val mode = buf.readUnsignedByteAlt1().toInt()
        val minimapPxOffX = buf.readUnsignedByte().toInt()
        val minimapPxOffY = buf.readUnsignedByte().toInt()
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
    opcode = 36
    length = 0
    decode { NoTimeout }
}

packets.register {
    opcode = 15
    length = 0
    decode { MapBuildComplete }
}

packets.register {
    opcode = 66
    length = variableByteLength
    decode { ReflectionCheckReply }
}

packets.register {
    opcode = 25
    length = 1
    decode { EventAppletFocus }
}

packets.register {
    opcode = 29
    length = 4
    decode { EventCameraPosition }
}

packets.register {
    opcode = 56
    length = 0
    decode { EventMouseIdle }
}

packets.register {
    opcode = 4
    length = variableByteLength
    decode { EventMouseMove }
}

packets.register {
    opcode = 83
    length = 6
    decode { EventMouseClick }
}

packets.register {
    opcode = 55
    length = variableShortLength
    decode { EventKeyboard }
}

packets.register {
    opcode = 58
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton1(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 2
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton2(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 75
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton3(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 50
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton4(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 60
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton5(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 23
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton6(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 17
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton7(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 21
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton8(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 24
    length = 8
    decode { buf ->
        val (component, dynamicChild, item) = buf.readIfButton
        IfButton9(component, dynamicChild, item)
    }
}

packets.register {
    opcode = 52
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
