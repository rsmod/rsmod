package gg.rsmod.plugins.core.protocol.structure.server

import gg.rsmod.cache.util.Xtea
import gg.rsmod.game.message.PacketLength
import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.game.model.map.MapSquare
import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.packet.server.RebuildNormal
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.writeShortAdd
import io.guthix.buffer.writeShortAddLE
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

val structures: DevicePacketStructureMap by inject()
val desktop = structures.server(Device.Desktop)

desktop.register<RebuildNormal> {
    opcode = 16
    length = PacketLength.Short
    write {
        val xteas = writeXteas(viewport, xteas)
        val buf = gpi?.write(it) ?: it
        buf.writeShortAddLE(playerZone.x)
        buf.writeShortAddLE(playerZone.y)
        buf.writeBytes(xteas)
    }
}

fun writeXteas(viewport: List<MapSquare>, xteasRepository: XteaRepository): ByteBuf {
    val buf = Unpooled.buffer(Short.SIZE_BYTES + (Int.SIZE_BYTES * 4 * 4))
    buf.writeShort(viewport.size)
    viewport.forEach { mapSquare ->
        val xteas = xteasRepository[mapSquare.id] ?: Xtea.EMPTY_KEY_SET
        xteas.forEach { buf.writeInt(it) }
    }
    return buf
}
