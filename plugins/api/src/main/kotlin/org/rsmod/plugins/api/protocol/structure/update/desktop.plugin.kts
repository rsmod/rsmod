package org.rsmod.plugins.api.protocol.structure.update

import io.guthix.buffer.writeByteAdd
import io.guthix.buffer.writeBytesReversed
import io.guthix.buffer.writeShortAddLE
import io.guthix.buffer.writeStringCP1252
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.update.AppearanceMask
import org.rsmod.plugins.api.protocol.packet.update.BitMask
import org.rsmod.plugins.api.protocol.packet.update.DirectionMask
import org.rsmod.plugins.api.protocol.packet.update.MovementPermMask
import org.rsmod.plugins.api.protocol.packet.update.MovementTempMask
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap

val structures: DevicePacketStructureMap by inject()
val masks = structures.update(Device.Desktop)

masks.order {
    -MovementPermMask::class
    -MovementTempMask::class
    -DirectionMask::class
    -AppearanceMask::class
}

masks.register<BitMask> {
    mask = 0x4
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

masks.register<DirectionMask> {
    mask = 0x1
    write {
        it.writeShortAddLE(angle)
    }
}

masks.register<MovementPermMask> {
    mask = 0x800
    write {
        it.writeByteAdd(type)
    }
}

masks.register<MovementTempMask> {
    mask = 0x200
    write {
        it.writeByteAdd(type)
    }
}

masks.register<AppearanceMask> {
    mask = 0x2
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

        it.writeByteAdd(appBuf.writerIndex())
        it.writeBytesReversed(appBuf)
    }
}
