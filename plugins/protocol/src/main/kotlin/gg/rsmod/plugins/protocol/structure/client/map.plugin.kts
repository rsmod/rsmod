package gg.rsmod.plugins.protocol.structure.client

import gg.rsmod.plugins.protocol.DesktopPacketStructure
import gg.rsmod.plugins.protocol.packet.client.MapBuildComplete

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.client

packets.register<MapBuildComplete> {
    opcode = 47
    length = 0
    suppress = true
}
