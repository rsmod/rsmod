package gg.rsmod.plugins.core.protocol.structure.client

import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.packet.client.NoTimeout
import gg.rsmod.plugins.core.protocol.packet.client.ReflectionCheckReply
import gg.rsmod.plugins.core.protocol.packet.client.WindowStatus
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap

val structures: DevicePacketStructureMap by inject()
val desktop = structures.client(Device.Desktop)

desktop.register<ReflectionCheckReply> {
    opcode = 94
    length = -1
}

desktop.register<NoTimeout> {
    opcode = 16
    length = 0
}

desktop.register<WindowStatus> {
    opcode = 41
    length = 5
}
