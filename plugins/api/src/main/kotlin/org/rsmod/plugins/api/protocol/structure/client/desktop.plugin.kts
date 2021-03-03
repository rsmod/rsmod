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
import org.rsmod.plugins.api.protocol.packet.client.OpLoc1
import org.rsmod.plugins.api.protocol.packet.client.OpLoc1Handler
import org.rsmod.plugins.api.protocol.packet.client.ReflectionCheckReply
import org.rsmod.plugins.api.protocol.packet.client.WindowStatus
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.readUnsignedByteNeg
import io.guthix.buffer.readUnsignedShortAdd
import io.guthix.buffer.readUnsignedShortAddLE
import org.rsmod.plugins.api.protocol.packet.client.EventCameraPosition
import org.rsmod.plugins.api.protocol.packet.client.IfButton
import org.rsmod.plugins.api.protocol.packet.client.IfButtonHandler
import org.rsmod.plugins.api.protocol.packet.client.LoginTimings
import org.rsmod.plugins.api.protocol.packet.client.MinimapClickHandler
import org.rsmod.plugins.api.protocol.packet.client.MoveMinimapClick

val structures: DevicePacketStructureMap by inject()
val packets = structures.client(Device.Desktop)

packets.register<EventMouseMove> {
    opcode = 72
    length = -1
}

packets.register<EventMouseClick> {
    opcode = 47
    length = 6
}

packets.register<EventMouseIdle> {
    opcode = 69
    length = 0
}

packets.register<EventAppletFocus> {
    opcode = 5
    length = 1
}

packets.register<EventKeyboard> {
    opcode = 70
    length = -2
}

packets.register<EventCameraPosition> {
    opcode = 37
    length = 4
}

packets.register<ClientCheat> {
    opcode = 32
    length = -1
    handler = ClientCheatHandler::class
    read {
        val input = readStringCP1252()
        ClientCheat(input)
    }
}

packets.register<MapBuildComplete> {
    opcode = 48
    length = 0
}

packets.register<MoveGameClick> {
    opcode = 34
    length = -1
    handler = GameClickHandler::class
    read {
        val type = readUnsignedByteNeg().toInt()
        val y = readUnsignedShortAddLE()
        val x = readUnsignedShort()
        MoveGameClick(x, y, type)
    }
}

packets.register<MoveMinimapClick> {
    opcode = 45
    length = -1
    handler = MinimapClickHandler::class
    read {
        val type = readUnsignedByteNeg().toInt()
        val y = readUnsignedShortAddLE()
        val x = readUnsignedShort()
        MoveMinimapClick(x, y, type)
    }
}

packets.register<OpLoc1> {
    opcode = 94
    length = 7
    handler = OpLoc1Handler::class
    read {
        val y = readUnsignedShortAdd()
        val x = readUnsignedShortAdd()
        val mode = readUnsignedByte().toInt()
        val id = readUnsignedShort()
        OpLoc1(id, x, y, mode)
    }
}

packets.register<ReflectionCheckReply> {
    opcode = 86
    length = -1
}

packets.register<NoTimeout> {
    opcode = 76
    length = 0
}

packets.register<WindowStatus> {
    opcode = 61
    length = 5
}

packets.register<IfButton> {
    val typeOpcodes = listOf(91, 12, 97, 19, 25, 21, 29, 59, 24, 71)
    addOpcodes(typeOpcodes)
    length = 8
    handler = IfButtonHandler::class
    read { opcode ->
        val type = typeOpcodes.indexOf(opcode) + IfButton.TYPE_INDEX_OFFSET
        val component = readInt()
        val slot = readShort().toInt()
        val item = readShort().toInt()
        IfButton(type, component, slot, item)
    }
}

packets.register<LoginTimings> {
    opcode = 99
    length = -1
}
