package org.rsmod.plugins.api.protocol.structure.server

import org.rsmod.util.security.Xtea
import org.rsmod.game.message.PacketLength
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.item.Item
import org.rsmod.game.model.map.MapSquare
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.server.IfCloseSub
import org.rsmod.plugins.api.protocol.packet.server.IfOpenSub
import org.rsmod.plugins.api.protocol.packet.server.IfOpenTop
import org.rsmod.plugins.api.protocol.packet.server.IfSetEvents
import org.rsmod.plugins.api.protocol.packet.server.PlayerInfo
import org.rsmod.plugins.api.protocol.packet.server.RebuildNormal
import org.rsmod.plugins.api.protocol.packet.server.RunClientScript
import org.rsmod.plugins.api.protocol.packet.server.SmallVarpPacket
import org.rsmod.plugins.api.protocol.packet.server.UpdateInvFull
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.writeByteAdd
import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeIntME
import io.guthix.buffer.writeShortAdd
import io.guthix.buffer.writeShortAddLE
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlin.math.min
import org.rsmod.plugins.api.protocol.packet.server.LargeVarpPacket

val structures: DevicePacketStructureMap by inject()
val packets = structures.server(Device.Desktop)

packets.register<UpdateInvFull> {
    opcode = 3
    length = PacketLength.Short
    write {
        it.writeInt(component)
        it.writeShort(key)
        it.writeShort(items.size)
        it.writeItemContainer(items)
    }
}

packets.register<RebuildNormal> {
    opcode = 65
    length = PacketLength.Short
    write {
        val xteas = xteasBuffer(viewport, xteas)
        val buf = gpi?.write(it) ?: it
        buf.writeShort(playerZone.x)
        buf.writeShortAdd(playerZone.y)
        buf.writeBytes(xteas)
    }
}

packets.register<IfOpenTop> {
    opcode = 39
    write {
        it.writeShortAddLE(interfaceId)
    }
}

packets.register<IfOpenSub> {
    opcode = 69
    write {
        it.writeIntLE(targetComponent)
        it.writeByteAdd(clickMode)
        it.writeShortLE(interfaceId)
    }
}

packets.register<IfCloseSub> {
    opcode = 21
    write {
        it.writeInt(component)
    }
}

packets.register<IfSetEvents> {
    opcode = 79
    write {
        it.writeInt(component)
        it.writeShortLE(dynamic.last)
        it.writeInt(event)
        it.writeShortAdd(dynamic.first)
    }
}

packets.register<RunClientScript> {
    opcode = 50
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

packets.register<PlayerInfo> {
    opcode = 75
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<SmallVarpPacket> {
    opcode = 59
    write {
        it.writeByteAdd(value)
        it.writeShortLE(id)
    }
}

packets.register<LargeVarpPacket> {
    opcode = 31
    write {
        it.writeIntME(value)
        it.writeShort(id)
    }
}

fun xteasBuffer(viewport: List<MapSquare>, xteasRepository: XteaRepository): ByteBuf {
    val buf = Unpooled.buffer(Short.SIZE_BYTES + (Int.SIZE_BYTES * 4 * 4))
    buf.writeShort(viewport.size)
    viewport.forEach { mapSquare ->
        val xteas = xteasRepository[mapSquare.id] ?: Xtea.EMPTY_KEY_SET
        xteas.forEach { buf.writeInt(it) }
    }
    return buf
}

fun ByteBuf.writeItemContainer(items: List<Item?>) {
    items.forEach { item ->
        val id = (item?.id ?: -1) + 1
        val amount = (item?.amount ?: 0)
        writeShortAdd(id)
        writeByteNeg(min(255, amount))
        if (amount >= 255) {
            writeIntLE(item?.amount ?: 0)
        }
    }
}
