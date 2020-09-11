package gg.rsmod.plugins.protocol.structure.server

import gg.rsmod.plugins.protocol.DesktopPacketStructure
import gg.rsmod.plugins.protocol.packet.server.IfOpenSub
import gg.rsmod.plugins.protocol.packet.server.IfOpenTop
import io.guthix.buffer.writeIntIME

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.server

packets.register<IfOpenTop> {
    opcode = 46
    write {
        it.writeShort(interfaceId)
    }
}

packets.register<IfOpenSub> {
    opcode = 40
    write {
        it.writeShort(interfaceId)
        it.writeByte(type)
        it.writeIntIME(targetComponent)
    }
}
