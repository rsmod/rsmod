package gg.rsmod.plugins.api.protocol.structure.server

import gg.rsmod.cache.util.Xtea
import gg.rsmod.game.message.PacketLength
import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.game.model.item.Item
import gg.rsmod.game.model.map.MapSquare
import gg.rsmod.plugins.api.protocol.Device
import gg.rsmod.plugins.api.protocol.packet.server.IfCloseSub
import gg.rsmod.plugins.api.protocol.packet.server.IfOpenSub
import gg.rsmod.plugins.api.protocol.packet.server.IfOpenTop
import gg.rsmod.plugins.api.protocol.packet.server.IfSetEvents
import gg.rsmod.plugins.api.protocol.packet.server.PlayerInfo
import gg.rsmod.plugins.api.protocol.packet.server.RebuildNormal
import gg.rsmod.plugins.api.protocol.packet.server.RunClientScript
import gg.rsmod.plugins.api.protocol.packet.server.SmallVarpPacket
import gg.rsmod.plugins.api.protocol.packet.server.UpdateInvFull
import gg.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.writeByteAdd
import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeIntME
import io.guthix.buffer.writeShortAddLE
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlin.math.min

val structures: DevicePacketStructureMap by inject()
val packets = structures.server(Device.Desktop)

packets.register<UpdateInvFull> {
    opcode = 34
    length = PacketLength.Short
    write {
        it.writeInt(component)
        it.writeShort(key)
        it.writeShort(items.size)
        it.writeItemContainer(items)
    }
}

packets.register<RebuildNormal> {
    opcode = 16
    length = PacketLength.Short
    write {
        val xteas = xteasBuffer(viewport, xteas)
        val buf = gpi?.write(it) ?: it
        buf.writeShortAddLE(playerZone.x)
        buf.writeShortAddLE(playerZone.y)
        buf.writeBytes(xteas)
    }
}

packets.register<IfOpenTop> {
    opcode = 48
    write {
        it.writeShort(interfaceId)
    }
}

packets.register<IfOpenSub> {
    opcode = 65
    write {
        it.writeIntME(targetComponent)
        it.writeShort(interfaceId)
        it.writeByteNeg(clickMode)
    }
}

packets.register<IfCloseSub> {
    opcode = 55
    write {
        it.writeInt(component)
    }
}

packets.register<IfSetEvents> {
    opcode = 54
    write {
        it.writeInt(event)
        it.writeShort(dynamic.last)
        it.writeShortLE(dynamic.first)
        it.writeInt(component)
    }
}

packets.register<RunClientScript> {
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

packets.register<PlayerInfo> {
    opcode = 40
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<SmallVarpPacket> {
    opcode = 44
    write {
        it.writeShort(id)
        it.writeByteAdd(value)
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
        writeByteNeg(min(255, amount))
        if (amount >= 255) {
            writeInt(item?.amount ?: 0)
        }
        writeShort(id)
    }
}