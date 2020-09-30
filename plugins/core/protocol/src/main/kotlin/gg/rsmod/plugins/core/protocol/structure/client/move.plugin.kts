package gg.rsmod.plugins.core.protocol.structure.client

import gg.rsmod.plugins.core.protocol.packet.client.GameClickHandler
import gg.rsmod.plugins.core.protocol.packet.client.MoveGameClick
import gg.rsmod.plugins.core.protocol.structure.DesktopPacketStructure
import io.guthix.buffer.readUnsignedShortAddLE

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.client

packets.register<MoveGameClick> {
    opcode = 98
    length = -1
    handler = GameClickHandler::class
    read {
        val type = readUnsignedByte().toInt()
        val x = readUnsignedShortAddLE()
        val y = readUnsignedShort()
        MoveGameClick(x, y, type)
    }
}
