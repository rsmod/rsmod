package gg.rsmod.plugins.core.protocol.structure.server

import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.packet.server.SmallVarpPacket
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.writeByteSub

val structures: DevicePacketStructureMap by inject()
val desktop = structures.server(Device.Desktop)

desktop.register<SmallVarpPacket> {
    opcode = 24
    write {
        it.writeByteSub(value)
        it.writeShort(id)
    }
}
