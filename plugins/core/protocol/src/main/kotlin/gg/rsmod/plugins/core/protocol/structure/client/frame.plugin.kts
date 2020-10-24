package gg.rsmod.plugins.core.protocol.structure.client

import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.packet.client.EventAppletFocus
import gg.rsmod.plugins.core.protocol.packet.client.EventKeyboard
import gg.rsmod.plugins.core.protocol.packet.client.EventMouseClick
import gg.rsmod.plugins.core.protocol.packet.client.EventMouseIdle
import gg.rsmod.plugins.core.protocol.packet.client.EventMouseMove
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap

val structures: DevicePacketStructureMap by inject()
val desktop = structures.client(Device.Desktop)

desktop.register<EventMouseMove> {
    opcode = 83
    length = -1
}

desktop.register<EventMouseClick> {
    opcode = 82
    length = 6
}

desktop.register<EventMouseIdle> {
    opcode = 40
    length = 0
}

desktop.register<EventAppletFocus> {
    opcode = 68
    length = 1
}

desktop.register<EventKeyboard> {
    opcode = 53
    length = -2
}
