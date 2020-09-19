package gg.rsmod.plugins.protocol.structure.server

import gg.rsmod.plugins.protocol.DesktopPacketStructure
import gg.rsmod.plugins.protocol.packet.server.IfOpenSub
import gg.rsmod.plugins.protocol.packet.server.IfOpenTop
import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeIntIME

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.server

packets.register<IfOpenTop> {
    opcode = 48
    write {
        it.writeShortLE(interfaceId)
    }
}

packets.register<IfOpenSub> {
    opcode = 16
    write {
        it.writeByteNeg(type)
        it.writeIntIME(targetComponent)
        it.writeShortLE(interfaceId)
    }
}
