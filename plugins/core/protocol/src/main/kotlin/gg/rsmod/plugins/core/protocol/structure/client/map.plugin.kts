package gg.rsmod.plugins.core.protocol.structure.client

import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.packet.client.MapBuildComplete
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap

val structures: DevicePacketStructureMap by inject()
val desktop = structures.client(Device.Desktop)

desktop.register<MapBuildComplete> {
    opcode = 72
    length = 0
}
