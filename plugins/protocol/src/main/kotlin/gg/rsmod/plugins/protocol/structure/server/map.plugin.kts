package gg.rsmod.plugins.protocol.structure.server

import gg.rsmod.cache.util.Xtea
import gg.rsmod.game.message.PacketLength
import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.game.model.map.Region
import gg.rsmod.plugins.protocol.DesktopPacketStructure
import gg.rsmod.plugins.protocol.packet.server.RebuildNormal
import io.guthix.buffer.writeShortAdd
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.server

packets.register<RebuildNormal> {
    opcode = 60
    length = PacketLength.Short
    write {
        val xtea = writeXteas(zoneX, zoneY, xteas)
        val buf = gpi?.write(it) ?: it
        buf.writeShortLE(zoneY)
        buf.writeShortAdd(zoneX)
        buf.writeBytes(xtea)
    }
}

fun writeXteas(zoneX: Int, zoneY: Int, xteasRepository: XteaRepository): ByteBuf {
    val lx = (zoneX - (Region.SIZE shr 4)) shr 3
    val rx = (zoneX + (Region.SIZE shr 4)) shr 3
    val ly = (zoneY - (Region.SIZE shr 4)) shr 3
    val ry = (zoneY + (Region.SIZE shr 4)) shr 3

    var emptySurroundings = false
    if ((zoneX / 8 == 48 || zoneX / 8 == 49) && zoneY / 8 == 48 || zoneX / 8 == 48 && zoneY / 8 == 148) {
        emptySurroundings = true
    }

    val buf = Unpooled.buffer(Short.SIZE_BYTES + (Int.SIZE_BYTES * 4 * 4))
    buf.writeShort(0)

    var regionCount = 0
    for (x in lx..rx) {
        for (y in ly..ry) {
            val validRegion = y != 49 && y != 149 && y != 147 && x != 50 && (x != 49 || y != 47)
            if (!emptySurroundings || validRegion) {
                val region = (x shl 8) or y
                val xteas = xteasRepository[region] ?: Xtea.EMPTY_KEY_SET
                xteas.forEach { buf.writeInt(it) }
                regionCount++
            }
        }
    }
    buf.setShort(0, regionCount)
    return buf
}
