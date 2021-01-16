package org.rsmod.plugins.api.protocol.structure.client

import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.client.ClientCheat
import org.rsmod.plugins.api.protocol.packet.client.ClientCheatHandler
import org.rsmod.plugins.api.protocol.packet.client.EventAppletFocus
import org.rsmod.plugins.api.protocol.packet.client.EventKeyboard
import org.rsmod.plugins.api.protocol.packet.client.EventMouseClick
import org.rsmod.plugins.api.protocol.packet.client.EventMouseIdle
import org.rsmod.plugins.api.protocol.packet.client.EventMouseMove
import org.rsmod.plugins.api.protocol.packet.client.GameClickHandler
import org.rsmod.plugins.api.protocol.packet.client.MapBuildComplete
import org.rsmod.plugins.api.protocol.packet.client.MoveGameClick
import org.rsmod.plugins.api.protocol.packet.client.NoTimeout
import org.rsmod.plugins.api.protocol.packet.client.OperateObjectOne
import org.rsmod.plugins.api.protocol.packet.client.OperateObjectOneHandler
import org.rsmod.plugins.api.protocol.packet.client.ReflectionCheckReply
import org.rsmod.plugins.api.protocol.packet.client.WindowStatus
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.readUnsignedShortAddLE

val structures: DevicePacketStructureMap by inject()
val packets = structures.client(Device.Desktop)

packets.register<EventMouseMove> {
    opcode = 63
    length = -1
}

packets.register<EventMouseClick> {
    opcode = 3
    length = 6
}

packets.register<EventMouseIdle> {
    opcode = 76
    length = 0
}

packets.register<EventAppletFocus> {
    opcode = 7
    length = 1
}

packets.register<EventKeyboard> {
    opcode = 1
    length = -2
}

packets.register<ClientCheat> {
    opcode = 102
    length = -1
    handler = ClientCheatHandler::class
    read {
        val input = readStringCP1252()
        ClientCheat(input)
    }
}

packets.register<MapBuildComplete> {
    opcode = 8
    length = 0
}

packets.register<MoveGameClick> {
    opcode = 5
    length = -1
    handler = GameClickHandler::class
    read {
        val x = readUnsignedShortLE()
        val type = readUnsignedByte().toInt()
        val y = readUnsignedShortAddLE()
        MoveGameClick(x, y, type)
    }
}

packets.register<OperateObjectOne> {
    opcode = 66
    length = 7
    handler = OperateObjectOneHandler::class
    read {
        val x = readUnsignedShortAddLE()
        val mode = readUnsignedByte().toInt()
        val y = readUnsignedShort()
        val id = readUnsignedShortAddLE()
        OperateObjectOne(id, x, y, mode)
    }
}

packets.register<ReflectionCheckReply> {
    opcode = 100
    length = -1
}

packets.register<NoTimeout> {
    opcode = 95
    length = 0
}

packets.register<WindowStatus> {
    opcode = 101
    length = 5
}
