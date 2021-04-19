package org.rsmod.plugins.api.protocol.structure.client

import io.guthix.buffer.readByteNeg
import io.guthix.buffer.readIntIME
import io.guthix.buffer.readIntME
import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.readUnsignedByteAdd
import io.guthix.buffer.readUnsignedByteNeg
import io.guthix.buffer.readUnsignedByteSub
import io.guthix.buffer.readUnsignedShortAdd
import io.guthix.buffer.readUnsignedShortAddLE
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.client.ClientCheat
import org.rsmod.plugins.api.protocol.packet.client.ClientCheatHandler
import org.rsmod.plugins.api.protocol.packet.client.EventAppletFocus
import org.rsmod.plugins.api.protocol.packet.client.EventCameraPosition
import org.rsmod.plugins.api.protocol.packet.client.EventKeyboard
import org.rsmod.plugins.api.protocol.packet.client.EventMouseClick
import org.rsmod.plugins.api.protocol.packet.client.EventMouseIdle
import org.rsmod.plugins.api.protocol.packet.client.EventMouseMove
import org.rsmod.plugins.api.protocol.packet.client.GameClickHandler
import org.rsmod.plugins.api.protocol.packet.client.IfButton
import org.rsmod.plugins.api.protocol.packet.client.IfButtonHandler
import org.rsmod.plugins.api.protocol.packet.client.LoginTimings
import org.rsmod.plugins.api.protocol.packet.client.MapBuildComplete
import org.rsmod.plugins.api.protocol.packet.client.MinimapClickHandler
import org.rsmod.plugins.api.protocol.packet.client.MoveGameClick
import org.rsmod.plugins.api.protocol.packet.client.MoveMinimapClick
import org.rsmod.plugins.api.protocol.packet.client.NoTimeout
import org.rsmod.plugins.api.protocol.packet.client.OpHeld1
import org.rsmod.plugins.api.protocol.packet.client.OpHeld1Handler
import org.rsmod.plugins.api.protocol.packet.client.OpHeld2
import org.rsmod.plugins.api.protocol.packet.client.OpHeld2Handler
import org.rsmod.plugins.api.protocol.packet.client.OpHeld3
import org.rsmod.plugins.api.protocol.packet.client.OpHeld3Handler
import org.rsmod.plugins.api.protocol.packet.client.OpHeld4
import org.rsmod.plugins.api.protocol.packet.client.OpHeld4Handler
import org.rsmod.plugins.api.protocol.packet.client.OpHeld5
import org.rsmod.plugins.api.protocol.packet.client.OpHeld5Handler
import org.rsmod.plugins.api.protocol.packet.client.OpHeld6
import org.rsmod.plugins.api.protocol.packet.client.OpHeld6Handler
import org.rsmod.plugins.api.protocol.packet.client.OpLoc1
import org.rsmod.plugins.api.protocol.packet.client.OpLoc1Handler
import org.rsmod.plugins.api.protocol.packet.client.OpLoc2
import org.rsmod.plugins.api.protocol.packet.client.OpLoc2Handler
import org.rsmod.plugins.api.protocol.packet.client.OpLoc3
import org.rsmod.plugins.api.protocol.packet.client.OpLoc3Handler
import org.rsmod.plugins.api.protocol.packet.client.OpLoc4
import org.rsmod.plugins.api.protocol.packet.client.OpLoc4Handler
import org.rsmod.plugins.api.protocol.packet.client.OpLoc5
import org.rsmod.plugins.api.protocol.packet.client.OpLoc5Handler
import org.rsmod.plugins.api.protocol.packet.client.OpLoc6
import org.rsmod.plugins.api.protocol.packet.client.OpLoc6Handler
import org.rsmod.plugins.api.protocol.packet.client.OpNpc1
import org.rsmod.plugins.api.protocol.packet.client.OpNpc1Handler
import org.rsmod.plugins.api.protocol.packet.client.OpNpc2
import org.rsmod.plugins.api.protocol.packet.client.OpNpc2Handler
import org.rsmod.plugins.api.protocol.packet.client.OpNpc3
import org.rsmod.plugins.api.protocol.packet.client.OpNpc3Handler
import org.rsmod.plugins.api.protocol.packet.client.OpNpc4
import org.rsmod.plugins.api.protocol.packet.client.OpNpc4Handler
import org.rsmod.plugins.api.protocol.packet.client.OpNpc5
import org.rsmod.plugins.api.protocol.packet.client.OpNpc5Handler
import org.rsmod.plugins.api.protocol.packet.client.OpNpc6
import org.rsmod.plugins.api.protocol.packet.client.OpNpc6Handler
import org.rsmod.plugins.api.protocol.packet.client.ReflectionCheckReply
import org.rsmod.plugins.api.protocol.packet.client.ResumePCountDialog
import org.rsmod.plugins.api.protocol.packet.client.ResumePCountDialogHandler
import org.rsmod.plugins.api.protocol.packet.client.ResumePObjDialog
import org.rsmod.plugins.api.protocol.packet.client.ResumePObjDialogHandler
import org.rsmod.plugins.api.protocol.packet.client.ResumePauseButton
import org.rsmod.plugins.api.protocol.packet.client.ResumePauseButtonHandler
import org.rsmod.plugins.api.protocol.packet.client.WindowStatus
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap

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
        val type = readByteNeg().toInt()
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
        val type = readByteNeg().toInt()
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

packets.register<OpLoc2> {
    opcode = 78
    length = 7
    handler = OpLoc2Handler::class
    read {
        val y = readUnsignedShortAdd()
        val mode = readUnsignedByteNeg().toInt()
        val id = readUnsignedShortLE()
        val x = readUnsignedShort()
        OpLoc2(id, x, y, mode)
    }
}

packets.register<OpLoc3> {
    opcode = 89
    length = 7
    handler = OpLoc3Handler::class
    read {
        val mode = readUnsignedByte().toInt()
        val id = readUnsignedShortLE()
        val x = readUnsignedShortLE()
        val y = readUnsignedShortAddLE()
        OpLoc3(id, x, y, mode)
    }
}

packets.register<OpLoc4> {
    opcode = 17
    length = 7
    handler = OpLoc4Handler::class
    read {
        val y = readUnsignedShortAddLE()
        val mode = readUnsignedByteSub().toInt()
        val x = readUnsignedShortAddLE()
        val id = readUnsignedShortAddLE()
        OpLoc4(id, x, y, mode)
    }
}

packets.register<OpLoc5> {
    opcode = 80
    length = 7
    handler = OpLoc5Handler::class
    read {
        val x = readUnsignedShortLE()
        val id = readUnsignedShortAddLE()
        val mode = readUnsignedByteNeg().toInt()
        val y = readUnsignedShortAdd()
        OpLoc5(id, x, y, mode)
    }
}

packets.register<OpLoc6> {
    opcode = 62
    length = 2
    handler = OpLoc6Handler::class
    read {
        val id = readUnsignedShortAddLE()
        OpLoc6(id)
    }
}

packets.register<OpNpc1> {
    opcode = 7
    length = 3
    handler = OpNpc1Handler::class
    read {
        val mode = readUnsignedByteAdd().toInt()
        val index = readUnsignedShortAdd()
        OpNpc1(index, mode)
    }
}

packets.register<OpNpc2> {
    opcode = 40
    length = 3
    handler = OpNpc2Handler::class
    read {
        val mode = readUnsignedByteNeg().toInt()
        val index = readUnsignedShort()
        OpNpc2(index, mode)
    }
}

packets.register<OpNpc3> {
    opcode = 31
    length = 3
    handler = OpNpc3Handler::class
    read {
        val index = readUnsignedShortAdd()
        val mode = readUnsignedByteAdd().toInt()
        OpNpc3(index, mode)
    }
}

packets.register<OpNpc4> {
    opcode = 2
    length = 3
    handler = OpNpc4Handler::class
    read {
        val mode = readUnsignedByteNeg().toInt()
        val index = readUnsignedShortAdd()
        OpNpc4(index, mode)
    }
}

packets.register<OpNpc5> {
    opcode = 73
    length = 3
    handler = OpNpc5Handler::class
    read {
        val index = readUnsignedShortAddLE()
        val mode = readUnsignedByteSub().toInt()
        OpNpc5(index, mode)
    }
}

packets.register<OpNpc6> {
    opcode = 42
    length = 2
    handler = OpNpc6Handler::class
    read {
        val id = readUnsignedShort()
        OpNpc6(id)
    }
}

packets.register<OpHeld1> {
    opcode = 57
    length = 8
    handler = OpHeld1Handler::class
    read {
        val component = readIntLE()
        val slot = readUnsignedShort()
        val item = readUnsignedShort()
        OpHeld1(item, component, slot)
    }
}

packets.register<OpHeld2> {
    opcode = 22
    length = 8
    handler = OpHeld2Handler::class
    read {
        val item = readUnsignedShortAddLE()
        val slot = readUnsignedShortAddLE()
        val component = readIntLE()
        OpHeld2(item, component, slot)
    }
}

packets.register<OpHeld3> {
    opcode = 87
    length = 8
    handler = OpHeld3Handler::class
    read {
        val slot = readUnsignedShortAddLE()
        val component = readInt()
        val item = readUnsignedShortLE()
        OpHeld3(item, component, slot)
    }
}

packets.register<OpHeld4> {
    opcode = 60
    length = 8
    handler = OpHeld4Handler::class
    read {
        val slot = readUnsignedShortAddLE()
        val item = readUnsignedShortAdd()
        val component = readIntIME()
        OpHeld4(item, component, slot)
    }
}

packets.register<OpHeld5> {
    opcode = 67
    length = 8
    handler = OpHeld5Handler::class
    read {
        val component = readIntME()
        val item = readUnsignedShort()
        val slot = readUnsignedShortAddLE()
        OpHeld5(item, component, slot)
    }
}

packets.register<OpHeld6> {
    opcode = 43
    length = 2
    handler = OpHeld6Handler::class
    read {
        val item = readUnsignedShortAdd()
        OpHeld6(item)
    }
}

packets.register<ResumePObjDialog> {
    opcode = 102
    length = 2
    handler = ResumePObjDialogHandler::class
    read {
        val item = readUnsignedShort()
        ResumePObjDialog(item)
    }
}

packets.register<ResumePCountDialog> {
    opcode = 56
    length = 4
    handler = ResumePCountDialogHandler::class
    read {
        val amount = readInt()
        ResumePCountDialog(amount)
    }
}

packets.register<ResumePauseButton> {
    opcode = 28
    length = 6
    handler = ResumePauseButtonHandler::class
    read {
        val component = readIntLE()
        val slot = readUnsignedShortAddLE()
        ResumePauseButton(component, slot)
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
