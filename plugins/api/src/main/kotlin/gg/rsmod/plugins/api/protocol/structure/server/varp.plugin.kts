package gg.rsmod.plugins.api.protocol.structure.server

import gg.rsmod.plugins.api.protocol.Device
import gg.rsmod.plugins.api.protocol.packet.server.SmallVarpPacket
import gg.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.writeByteAdd

val structures: DevicePacketStructureMap by inject()
val desktop = structures.server(Device.Desktop)

desktop.register<SmallVarpPacket> {
    opcode = 44
    write {
        it.writeShort(id)
        it.writeByteAdd(value)
    }
}
