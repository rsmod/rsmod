package gg.rsmod.plugins.api.protocol.structure.client

import gg.rsmod.plugins.api.protocol.Device
import gg.rsmod.plugins.api.protocol.packet.client.GameClickHandler
import gg.rsmod.plugins.api.protocol.packet.client.MoveGameClick
import gg.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.readUnsignedShortAddLE

val structures: DevicePacketStructureMap by inject()
val desktop = structures.client(Device.Desktop)

desktop.register<MoveGameClick> {
    opcode = 29
    length = -1
    handler = GameClickHandler::class
    read {
        val x = readUnsignedShortAddLE()
        val type = readUnsignedByte().toInt()
        val y = readUnsignedShortLE()
        MoveGameClick(x, y, type)
    }
}
