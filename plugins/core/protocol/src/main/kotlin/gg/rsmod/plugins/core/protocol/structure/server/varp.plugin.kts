package gg.rsmod.plugins.core.protocol.structure.server

import gg.rsmod.plugins.core.protocol.packet.server.SmallVarpPacket
import gg.rsmod.plugins.core.protocol.structure.DesktopPacketStructure
import io.guthix.buffer.writeByteSub

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.server

packets.register<SmallVarpPacket> {
    opcode = 24
    write {
        it.writeByteSub(value)
        it.writeShort(id)
    }
}
