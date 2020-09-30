package gg.rsmod.plugins.core.protocol.structure.client

import gg.rsmod.plugins.core.protocol.packet.client.MapBuildComplete
import gg.rsmod.plugins.core.protocol.structure.DesktopPacketStructure

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.client

packets.register<MapBuildComplete> {
    opcode = 72
    length = 0
}
