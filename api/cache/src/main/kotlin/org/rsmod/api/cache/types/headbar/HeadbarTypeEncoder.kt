package org.rsmod.api.cache.types.headbar

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.writeNullableLargeSmart
import org.rsmod.game.type.headbar.HeadbarTypeBuilder
import org.rsmod.game.type.headbar.UnpackedHeadbarType

public object HeadbarTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedHeadbarType>,
    ): List<UnpackedHeadbarType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.HEADBAR
        val packed = mutableListOf<UnpackedHeadbarType>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf = buffer.clear().encodeConfig { encodeJs5(type, this) }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
                packed += type
            }
            oldBuf?.release()
        }
        buffer.release()
        return packed
    }

    public fun encodeJs5(type: UnpackedHeadbarType, data: ByteBuf): Unit =
        with(type) {
            if (unknown1 != HeadbarTypeBuilder.DEFAULT_UNKNOWN1) {
                data.writeByte(1)
                data.writeShort(unknown1)
            }

            if (showPriority != HeadbarTypeBuilder.DEFAULT_SHOW_PRIORITY) {
                data.writeByte(2)
                data.writeByte(showPriority)
            }

            if (hidePriority != HeadbarTypeBuilder.DEFAULT_HIDE_PRIORITY) {
                data.writeByte(3)
                data.writeByte(hidePriority)
            }

            if (fadeout == 0) {
                data.writeByte(4)
            }

            if (stickTime != HeadbarTypeBuilder.DEFAULT_STICK_TIME) {
                data.writeByte(5)
                data.writeShort(stickTime)
            }

            if (unknown6 != HeadbarTypeBuilder.DEFAULT_UNKNOWN6) {
                data.writeByte(6)
                data.writeByte(unknown6)
            }

            if (full != null) {
                data.writeByte(7)
                data.writeNullableLargeSmart(full)
            }

            if (empty != null) {
                data.writeByte(8)
                data.writeNullableLargeSmart(empty)
            }

            if (fadeout != 0 && fadeout != HeadbarTypeBuilder.DEFAULT_FADEOUT) {
                data.writeByte(11)
                data.writeShort(fadeout)
            }

            if (segments != HeadbarTypeBuilder.DEFAULT_SEGMENTS) {
                data.writeByte(14)
                data.writeByte(segments)
            }

            if (padding != 0) {
                data.writeByte(15)
                data.writeByte(padding)
            }
        }
}
