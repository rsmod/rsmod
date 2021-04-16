package org.rsmod.plugins.api.protocol.structure.server

import io.guthix.buffer.writeByteAdd
import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeIntIME
import io.guthix.buffer.writeIntME
import io.guthix.buffer.writeShortAdd
import io.guthix.buffer.writeShortAddLE
import io.guthix.buffer.writeSmallSmart
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.rsmod.game.message.PacketLength
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.item.Item
import org.rsmod.game.model.map.MapSquare
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.server.IfCloseSub
import org.rsmod.plugins.api.protocol.packet.server.IfOpenSub
import org.rsmod.plugins.api.protocol.packet.server.IfOpenTop
import org.rsmod.plugins.api.protocol.packet.server.IfSetAnim
import org.rsmod.plugins.api.protocol.packet.server.IfSetEvents
import org.rsmod.plugins.api.protocol.packet.server.IfSetNpcHead
import org.rsmod.plugins.api.protocol.packet.server.IfSetText
import org.rsmod.plugins.api.protocol.packet.server.MessageGame
import org.rsmod.plugins.api.protocol.packet.server.MinimapFlagSet
import org.rsmod.plugins.api.protocol.packet.server.NpcInfoLargeViewport
import org.rsmod.plugins.api.protocol.packet.server.NpcInfoSmallViewport
import org.rsmod.plugins.api.protocol.packet.server.PlayerInfo
import org.rsmod.plugins.api.protocol.packet.server.RebuildNormal
import org.rsmod.plugins.api.protocol.packet.server.ResetAnims
import org.rsmod.plugins.api.protocol.packet.server.ResetClientVarCache
import org.rsmod.plugins.api.protocol.packet.server.RunClientScript
import org.rsmod.plugins.api.protocol.packet.server.UpdateInvFull
import org.rsmod.plugins.api.protocol.packet.server.UpdateInvPartial
import org.rsmod.plugins.api.protocol.packet.server.UpdateRunEnergy
import org.rsmod.plugins.api.protocol.packet.server.UpdateStat
import org.rsmod.plugins.api.protocol.packet.server.VarpLarge
import org.rsmod.plugins.api.protocol.packet.server.VarpSmall
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import org.rsmod.util.security.Xtea
import kotlin.math.min

val structures: DevicePacketStructureMap by inject()
val packets = structures.server(Device.Desktop)

packets.register<UpdateInvFull> {
    opcode = 74
    length = PacketLength.Short
    write {
        it.writeInt(component)
        it.writeShort(key)
        it.writeShort(items.size)
        it.writeFullItemContainer(items)
    }
}

packets.register<UpdateInvPartial> {
    opcode = 35
    length = PacketLength.Short
    write {
        it.writeInt(component)
        it.writeShort(key)
        it.writePartialItemContainer(updated)
    }
}

packets.register<RebuildNormal> {
    opcode = 18
    length = PacketLength.Short
    write {
        val xteas = xteasBuffer(viewport, xteas)
        val buf = gpi?.write(it) ?: it
        buf.writeShortAdd(playerZone.y)
        buf.writeShortLE(playerZone.x)
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
    opcode = 34
    write {
        it.writeShort(interfaceId)
        it.writeIntME(targetComponent)
        it.writeByte(clickMode)
    }
}

packets.register<IfCloseSub> {
    opcode = 76
    write {
        it.writeInt(component)
    }
}

packets.register<IfSetText> {
    opcode = 13
    length = PacketLength.Short
    write {
        it.writeIntME(component)
        it.writeStringCP1252(text)
    }
}

packets.register<IfSetNpcHead> {
    opcode = 71
    write {
        it.writeShortAdd(npc)
        it.writeIntME(component)
    }
}

packets.register<IfSetAnim> {
    opcode = 14
    write {
        it.writeIntLE(component)
        it.writeShortAdd(anim)
    }
}

packets.register<IfSetEvents> {
    opcode = 68
    write {
        it.writeIntLE(events)
        it.writeShortLE(dynamic.first)
        it.writeShort(dynamic.last)
        it.writeIntIME(component)
    }
}

packets.register<RunClientScript> {
    opcode = 58
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
    opcode = 66
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<NpcInfoSmallViewport> {
    opcode = 59
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<NpcInfoLargeViewport> {
    opcode = 5
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<VarpSmall> {
    opcode = 85
    write {
        it.writeByteAdd(value)
        it.writeShort(id)
    }
}

packets.register<VarpLarge> {
    opcode = 31
    write {
        it.writeInt(value)
        it.writeShortLE(id)
    }
}

packets.register<ResetClientVarCache> {
    opcode = 3
    write { /* empty */ }
}

packets.register<ResetAnims> {
    opcode = 51
    write { /* empty */ }
}

packets.register<UpdateRunEnergy> {
    opcode = 52
    write {
        it.writeByte(energy)
    }
}

packets.register<UpdateStat> {
    opcode = 28
    write {
        it.writeIntLE(xp)
        it.writeByteNeg(currLevel)
        it.writeByte(skill)
    }
}

packets.register<MinimapFlagSet> {
    opcode = 78
    write {
        it.writeByte(x)
        it.writeByte(y)
    }
}

packets.register<MessageGame> {
    opcode = 39
    length = PacketLength.Byte
    write {
        it.writeSmallSmart(type)
        it.writeBoolean(username != null)
        if (username != null) {
            it.writeStringCP1252(username)
        }
        it.writeStringCP1252(text)
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

fun ByteBuf.writeFullItemContainer(items: List<Item?>) {
    items.forEach { item ->
        val id = (item?.id ?: -1) + 1
        val amount = (item?.amount ?: 0)
        writeByte(min(255, amount))
        if (amount >= 255) {
            writeIntME(item?.amount ?: 0)
        }
        writeShortAddLE(id)
    }
}

fun ByteBuf.writePartialItemContainer(items: Map<Int, Item?>) {
    items.forEach { (slot, item) ->
        val id = (item?.id ?: -1) + 1
        val amount = (item?.amount ?: 0)
        writeSmallSmart(slot)
        writeShort(id)
        if (id != 0) {
            writeByte(min(255, amount))
            if (amount >= 255) {
                writeInt(item?.amount ?: 0)
            }
        }
    }
}
