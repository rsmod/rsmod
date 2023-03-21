package org.rsmod.plugins.api.prot.desktop

import org.openrs2.buffer.writeBytesA
import org.openrs2.buffer.writeString
import org.rsmod.plugins.api.net.info.ExtendedPlayerInfo
import org.rsmod.plugins.api.net.platform.info.InfoPlatformPacketEncoders
import org.rsmod.plugins.api.net.writeByteAlt1
import org.rsmod.plugins.api.net.writeByteAlt2
import org.rsmod.plugins.api.net.writeByteAlt3
import org.rsmod.plugins.api.net.writeIntAlt1
import org.rsmod.plugins.api.net.writeShortAlt1
import org.rsmod.plugins.api.net.writeShortAlt2
import org.rsmod.plugins.api.net.writeShortAlt3
import org.rsmod.plugins.info.player.model.ExtendedInfoSizes

private val encoders: InfoPlatformPacketEncoders by inject()
private val info = encoders.desktop.player

info.order {
    -ExtendedPlayerInfo.Spotanim::class
    -ExtendedPlayerInfo.Appearance::class
    -ExtendedPlayerInfo.ExactMove::class
    -ExtendedPlayerInfo.Hit::class
    -ExtendedPlayerInfo.MoveSpeedTemp::class
    -ExtendedPlayerInfo.Chat::class
    -ExtendedPlayerInfo.MoveSpeedPerm::class
    -ExtendedPlayerInfo.Anim::class
    -ExtendedPlayerInfo.Recolor::class
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

info.register<ExtendedPlayerInfo.Anim> {
    bitmask = 16
    encode { info, buf ->
        buf.writeShort(info.sequence)
        buf.writeByteAlt2(info.delay)
    }
}

info.register<ExtendedPlayerInfo.Appearance> {
    bitmask = 64
    encode { info, buf ->
        val appBuf = buf.alloc().buffer(ExtendedInfoSizes.APPEARANCE_MAX_BYTE_SIZE).let {
            it.writeByte(info.gender)
            it.writeByte(info.overheadSkull ?: -1)
            it.writeByte(info.overheadPrayer ?: -1)
            if (info.transmog != null) {
                it.writeShort(-1)
                it.writeShort(info.transmog)
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

info.register<ExtendedPlayerInfo.Chat> {
    bitmask = 1
    encode { info, buf ->
        TODO("huffman")
    }
}

info.register<ExtendedPlayerInfo.ExactMove> {
    bitmask = 4096
    encode { info, buf ->
        buf.writeByteAlt1(info.deltaX1)
        buf.writeByteAlt1(info.deltaZ1)
        buf.writeByteAlt1(info.deltaX2)
        buf.writeByteAlt2(info.deltaZ2)
        buf.writeShort(info.arriveDelay1)
        buf.writeShortAlt3(info.arriveDelay2)
    }
}

info.register<ExtendedPlayerInfo.FaceEntity> {
    bitmask = 8
    encode { info, buf ->
        buf.writeShortAlt1(info.index and 0xFFFF)
        buf.writeByteAlt2(info.index shr 16)
    }
}

info.register<ExtendedPlayerInfo.FaceSquare> {
    bitmask = 128
    encode { info, buf ->
        buf.writeShortAlt2(info.orientation)
    }
}

info.register<ExtendedPlayerInfo.Hit> {
    bitmask = 4
    encode { info, buf ->
        TODO("Hit model")
    }
}

info.register<ExtendedPlayerInfo.MoveSpeedTemp> {
    bitmask = 256
    encode { info, buf ->
        buf.writeByteAlt3(info.type)
    }
}

info.register<ExtendedPlayerInfo.MoveSpeedPerm> {
    bitmask = 16384
    encode { info, buf ->
        buf.writeByteAlt1(info.type)
    }
}

info.register<ExtendedPlayerInfo.Prefix> {
    bitmask = 1024
    encode { info, buf ->
        buf.writeString(info.string1 ?: "")
        buf.writeString(info.string2 ?: "")
        buf.writeString(info.string3 ?: "")
    }
}

info.register<ExtendedPlayerInfo.Recolor> {
    bitmask = 2048
    encode { info, buf ->
        buf.writeShort(info.startDelay)
        buf.writeShortAlt1(info.endDelay)
        buf.writeByteAlt1(info.hue)
        buf.writeByte(info.sat)
        buf.writeByteAlt2(info.lum)
        buf.writeByteAlt3(info.amount)
    }
}

info.register<ExtendedPlayerInfo.Spotanim> {
    bitmask = 8192
    encode { info, buf ->
        buf.writeShortAlt2(info.id)
        buf.writeIntAlt1((info.height shl 16) or info.delay)
    }
}

info.register<ExtendedPlayerInfo.Say> {
    bitmask = 32
    encode { info, buf ->
        val text = if (info.expose) "~${info.text}" else info.text
        buf.writeString(text)
    }
}
