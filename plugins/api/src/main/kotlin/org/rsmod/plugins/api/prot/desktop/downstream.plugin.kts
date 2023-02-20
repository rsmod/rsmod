package org.rsmod.plugins.api.prot.desktop

import org.openrs2.buffer.BitBuf
import org.openrs2.buffer.writeByteS
import org.openrs2.buffer.writeShortA
import org.openrs2.buffer.writeShortLEA
import org.rsmod.plugins.api.net.downstream.IfOpenSub
import org.rsmod.plugins.api.net.downstream.IfOpenTop
import org.rsmod.plugins.api.net.downstream.PlayerInfoPacket
import org.rsmod.plugins.api.net.downstream.RebuildNormal
import org.rsmod.plugins.api.net.downstream.VarpLarge
import org.rsmod.plugins.api.net.downstream.VarpSmall
import org.rsmod.plugins.api.net.platform.GamePlatformPacketMaps

private val platforms: GamePlatformPacketMaps by inject()
private val packets = platforms.desktopDownstream

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
        /* log-in sends gpi initialization data along with REBUILD_NORMAL data */
        packet.gpiInitialization?.let { gpi ->
            BitBuf(buf).use { bitBuf -> gpi.encode(bitBuf) }
        }
        buf.writeShortA(packet.zone.x)
        buf.writeShortLE(packet.zone.y)
        buf.writeShort(packet.xteaList.size / 4)
        packet.xteaList.forEach { key ->
            buf.writeInt(key)
        }
    }
}

packets.register<PlayerInfoPacket> {
    opcode = 26
    length = variableShortLength
    encode { packet, buf ->
        buf.writeBytes(packet.data, 0, packet.length)
    }
}

packets.register<VarpSmall> {
    opcode = 31
    length = 3
    encode { packet, buf ->
        buf.writeByteS(packet.packed)
        buf.writeShortA(packet.varp)
    }
}

packets.register<VarpLarge> {
    opcode = 76
    length = 6
    encode { packet, buf ->
        buf.writeInt(packet.packed)
        buf.writeShortLEA(packet.varp)
    }
}
