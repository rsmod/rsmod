package gg.rsmod.plugins.core.protocol.structure.server

import gg.rsmod.game.message.PacketLength
import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.packet.server.IfCloseSub
import gg.rsmod.plugins.core.protocol.packet.server.IfOpenSub
import gg.rsmod.plugins.core.protocol.packet.server.IfOpenTop
import gg.rsmod.plugins.core.protocol.packet.server.IfSetEvents
import gg.rsmod.plugins.core.protocol.packet.server.RunClientScript
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeIntIME
import io.guthix.buffer.writeIntME
import io.guthix.buffer.writeShortAdd
import io.guthix.buffer.writeStringCP1252

val structures: DevicePacketStructureMap by inject()
val desktop = structures.server(Device.Desktop)

desktop.register<IfOpenTop> {
    opcode = 48
    write {
        it.writeShort(interfaceId)
    }
}

desktop.register<IfOpenSub> {
    opcode = 65
    write {
        it.writeIntME(targetComponent)
        it.writeShort(interfaceId)
        it.writeByteNeg(clickMode)
    }
}

desktop.register<IfCloseSub> {
    opcode = 55
    write {
        it.writeInt(component)
    }
}

desktop.register<IfSetEvents> {
    opcode = 54
    write {
        it.writeInt(event)
        it.writeShort(dynamic.last)
        it.writeShortLE(dynamic.first)
        it.writeInt(component)
    }
}

desktop.register<RunClientScript> {
    opcode = 80
    length = PacketLength.Short
    write {
        val types = CharArray(args.size) { i -> if (args[i] is String) 's' else 'i' }
        it.writeStringCP1252(String(types))
        for (i in args.size - 1 downTo 0) {
            val arg = args[i]
            if (arg is String) {
                it.writeStringCP1252(arg)
            } else if (arg is Number) {
                it.writeInt(arg.toInt())
            }
        }
        it.writeInt(id)
    }
}
