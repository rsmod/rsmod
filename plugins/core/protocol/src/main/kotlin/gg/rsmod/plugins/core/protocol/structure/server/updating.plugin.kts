package gg.rsmod.plugins.core.protocol.structure.server

import gg.rsmod.game.message.PacketLength
import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.packet.server.PlayerInfo
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap

val structures: DevicePacketStructureMap by inject()
val desktop = structures.server(Device.Desktop)

desktop.register<PlayerInfo> {
    opcode = 82
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}
