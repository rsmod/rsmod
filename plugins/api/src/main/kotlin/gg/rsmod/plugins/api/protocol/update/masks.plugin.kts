package gg.rsmod.plugins.api.protocol.update

import gg.rsmod.plugins.api.protocol.Device
import gg.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeBytesReversedAdd
import io.guthix.buffer.writeStringCP1252

val structures: DevicePacketStructureMap by inject()
val desktop = structures.update(Device.Desktop)

desktop.order {
    -DirectionMask::class
    -AppearanceMask::class
}

desktop.register<BitMask> {
    mask = 0x10
    write {
        if (packed >= 0xFF) {
            val bitmask = packed or mask
            it.writeByte(bitmask and 0xFF)
            it.writeByte(bitmask shr 8)
        } else {
            it.writeByte(packed and 0xFF)
        }
    }
}

desktop.register<DirectionMask> {
    mask = 0x20
    write {
        it.writeShortLE(angle)
    }
}

desktop.register<AppearanceMask> {
    mask = 0x4
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
        appBuf.writeShort(0) /* unknown */
        appBuf.writeBoolean(invisible)

        it.writeByteNeg(appBuf.writerIndex())
        it.writeBytesReversedAdd(appBuf)
    }
}
