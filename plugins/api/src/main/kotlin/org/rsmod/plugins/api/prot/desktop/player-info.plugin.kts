package org.rsmod.plugins.api.prot.desktop

import org.openrs2.buffer.writeBytesA
import org.openrs2.buffer.writeString
import org.rsmod.plugins.api.net.info.ExtendedPlayerInfo
import org.rsmod.plugins.api.net.platform.info.InfoPlatformPacketEncoders
import org.rsmod.plugins.api.net.writeByteAlt1
import org.rsmod.plugins.info.player.model.ExtendedInfoSizes

private val encoders: InfoPlatformPacketEncoders by inject()
private val info = encoders.desktop.player

info.order.apply {
    this += ExtendedPlayerInfo.Appearance::class.java
    this += ExtendedPlayerInfo.MovementPermMask::class.java
}

info.register<ExtendedPlayerInfo.ExtendedFlag> {
    bitmask = 2
    encode { info, buf ->
        if (info.bitmasks >= 0xFF) {
            buf.writeShortLE(info.bitmasks or bitmask)
        } else {
            buf.writeByte(info.bitmasks)
        }
    }
}

info.register<ExtendedPlayerInfo.Appearance> {
    bitmask = 64
    encode { info, buf ->
        val appBuf = buf.alloc().buffer(ExtendedInfoSizes.APPEARANCE_MAX_BYTE_SIZE).let {
            it.writeByte(info.gender)
            it.writeByte(info.overheadSkull ?: -1)
            it.writeByte(info.overheadPrayer ?: -1)
            if (info.transmogId != null) {
                it.writeShort(-1)
                it.writeShort(info.transmogId)
            } else {
                it.writeBytes(info.looks)
            }
            info.colors.forEach { color -> it.writeByte(color) }
            info.bas.forEach { anim -> it.writeShort(anim) }
            it.writeString(info.displayName)
            it.writeByte(info.combatLevel)
            it.writeShort(info.unknownShortValue)
            it.writeBoolean(info.invisible)
            it.writeShort(info.unknownShortValue)
            info.prefixes.forEach { prefix -> it.writeString(prefix) }
            it.writeByte(info.unknownByteValue)
        }
        buf.writeByte(appBuf.readableBytes())
        buf.writeBytesA(appBuf)
    }
}

info.register<ExtendedPlayerInfo.MovementPermMask> {
    bitmask = 16384
    encode { info, buf ->
        buf.writeByteAlt1(info.type)
    }
}
