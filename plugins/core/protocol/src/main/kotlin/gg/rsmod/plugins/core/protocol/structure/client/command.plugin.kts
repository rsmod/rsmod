package gg.rsmod.plugins.core.protocol.structure.client

import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.packet.client.ClientCheat
import gg.rsmod.plugins.core.protocol.packet.client.ClientCheatHandler
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.readStringCP1252

val structures: DevicePacketStructureMap by inject()
val desktop = structures.client(Device.Desktop)

desktop.register<ClientCheat> {
    opcode = 57
    length = -1
    handler = ClientCheatHandler::class
    read {
        val input = readStringCP1252()
        ClientCheat(input)
    }
}
