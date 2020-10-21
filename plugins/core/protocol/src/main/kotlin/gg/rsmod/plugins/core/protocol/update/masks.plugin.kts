package gg.rsmod.plugins.core.protocol.update

import gg.rsmod.plugins.core.protocol.Device
import gg.rsmod.plugins.core.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeBytesReversed
import io.guthix.buffer.writeStringCP1252

val structures: DevicePacketStructureMap by inject()
val desktop = structures.update(Device.Desktop)

desktop.register<BitMask> {
    mask = 0x80
    write {
        if (packed >= 0xFF) {
            val mask = packed or mask
            it.writeByte(mask and 0xFF)
            it.writeByte(mask shr 8)
        } else {
            it.writeByte(packed and 0xFF)
        }
    }
}

desktop.register<DirectionMask> {
    mask = 0x2
    write {
        it.writeShortLE(angle)
    }
}

desktop.register<AppearanceMask> {
    mask = 0x1
    write {
        val appBuf = it.alloc().buffer()
        appBuf.writeByte(gender)
        appBuf.writeByte(skull)
        appBuf.writeByte(overheadPrayer)

        if (npc > 0) {
            appBuf.writeShort(-1)
            appBuf.writeShort(npc)
        } else {
            appBuf.writeBytes(looks)
        }

        colors.forEach { color ->
            appBuf.writeByte(color)
        }

        bas.forEach { animation ->
            appBuf.writeShort(animation)
        }

        appBuf.writeStringCP1252(username)
        appBuf.writeByte(combatLevel)
        appBuf.writeShort(0)
        appBuf.writeBoolean(invisible)

        it.writeByteNeg(appBuf.writerIndex())
        it.writeBytesReversed(appBuf)
    }
}
