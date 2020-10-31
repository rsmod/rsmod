package gg.rsmod.plugins.api.protocol.structure.server

import gg.rsmod.game.message.PacketLength
import gg.rsmod.plugins.api.protocol.Device
import gg.rsmod.plugins.api.protocol.packet.server.PlayerInfo
import gg.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap

val structures: DevicePacketStructureMap by inject()
val desktop = structures.server(Device.Desktop)

desktop.register<PlayerInfo> {
    opcode = 40
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}
