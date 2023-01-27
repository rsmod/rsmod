package org.rsmod.plugins.net.rev.desktop

import org.openrs2.buffer.writeByteS
import org.openrs2.buffer.writeShortA
import org.rsmod.plugins.api.prot.downstream.IfOpenSub
import org.rsmod.plugins.api.prot.downstream.IfOpenTop
import org.rsmod.plugins.api.prot.downstream.RebuildNormal
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

packets.register<IfOpenSub> {
    opcode = 48
    length = 7
    encode { packet, buf ->
        buf.writeByteS(packet.clickMode)
        buf.writeIntLE(packet.targetComponent)
        buf.writeShortA(packet.interfaceId)
    }
}

packets.register<RebuildNormal> {
    opcode = 16
    length = variableShortLength
    encode { packet, buf ->
        buf.writeShortA(packet.zone.x)
        buf.writeShortLE(packet.zone.y)
        buf.writeShort(packet.xteaList.size / 4)
        packet.xteaList.forEach { key ->
            buf.writeInt(key)
        }
    }
}
