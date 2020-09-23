package gg.rsmod.plugins.protocol.structure.client

import gg.rsmod.plugins.protocol.packet.client.MapBuildComplete
import gg.rsmod.plugins.protocol.structure.DesktopPacketStructure

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.client

packets.register<MapBuildComplete> {
    opcode = 72
    length = 0
    suppress = true
}
