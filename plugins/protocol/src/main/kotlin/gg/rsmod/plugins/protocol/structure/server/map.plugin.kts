package gg.rsmod.plugins.protocol.structure.server

import gg.rsmod.cache.util.Xtea
import gg.rsmod.game.message.PacketLength
import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.game.model.map.MapSquare
import gg.rsmod.game.model.map.Zone
import gg.rsmod.plugins.protocol.packet.server.RebuildNormal
import gg.rsmod.plugins.protocol.structure.DesktopPacketStructure
import io.guthix.buffer.writeShortAdd
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.server

packets.register<RebuildNormal> {
    opcode = 60
    length = PacketLength.Short
    write {
        val xtea = writeXteas(playerZone, viewport, xteas)
        val buf = gpi?.write(it) ?: it
        buf.writeShortLE(playerZone.y)
        buf.writeShortAdd(playerZone.x)
        buf.writeBytes(xtea)
    }
}

fun writeXteas(zone: Zone, viewport: List<MapSquare>, xteasRepository: XteaRepository): ByteBuf {
    var emptySurroundings = false
    if ((zone.x / 8 == 48 || zone.x / 8 == 49) && zone.y / 8 == 48 || zone.x / 8 == 48 && zone.y / 8 == 148) {
        emptySurroundings = true
    }

    val buf = Unpooled.buffer(Short.SIZE_BYTES + (Int.SIZE_BYTES * 4 * 4))
    buf.writeShort(0)

    var regionCount = 0
    viewport.forEach { mapSquare ->
        val validRegion = mapSquare.y != 49 && mapSquare.y != 149 && mapSquare.y != 147 && mapSquare.x != 50 && (mapSquare.x != 49 || mapSquare.y != 47)
        if (!emptySurroundings || validRegion) {
            val region = (mapSquare.x shl 8) or mapSquare.y
            val xteas = xteasRepository[region] ?: Xtea.EMPTY_KEY_SET
            xteas.forEach { buf.writeInt(it) }
            regionCount++
        }
    }
    buf.setShort(0, regionCount)
    return buf
}
