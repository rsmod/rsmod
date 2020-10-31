package gg.rsmod.plugins.api.protocol.structure.server

import gg.rsmod.game.message.PacketLength
import gg.rsmod.game.model.item.Item
import gg.rsmod.plugins.api.protocol.Device
import gg.rsmod.plugins.api.protocol.packet.server.UpdateInvFull
import gg.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.writeByteNeg
import io.netty.buffer.ByteBuf
import kotlin.math.min

val structures: DevicePacketStructureMap by inject()
val desktop = structures.server(Device.Desktop)

desktop.register<UpdateInvFull> {
    opcode = 34
    length = PacketLength.Short
    write {
        it.writeInt(component)
        it.writeShort(key)
        it.writeShort(items.size)
        it.writeItemContainer(items)
    }
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
