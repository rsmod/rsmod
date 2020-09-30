package gg.rsmod.plugins.core.protocol.structure.server

import gg.rsmod.game.message.PacketLength
import gg.rsmod.plugins.core.protocol.packet.server.PlayerUpdate
import gg.rsmod.plugins.core.protocol.structure.DesktopPacketStructure

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.server

packets.register<PlayerUpdate> {
    opcode = 82
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}
