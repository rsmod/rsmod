package gg.rsmod.plugins.core.protocol.structure.client

import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.packet.client.NoTimeout
import gg.rsmod.plugins.core.protocol.packet.client.UnknownClientPacket
import gg.rsmod.plugins.core.protocol.packet.client.WindowStatus
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap

val structures: DevicePacketStructureMap by inject()
val desktop = structures.client(Device.Desktop)

desktop.register<UnknownClientPacket> {
    opcode = 41
    length = -1
}

desktop.register<NoTimeout> {
    opcode = 0
    length = 0
}

desktop.register<WindowStatus> {
    opcode = 10
    length = 5
}
