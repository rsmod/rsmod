package org.rsmod.api.cache.types.stat

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.stat.StatTypeBuilder
import org.rsmod.game.type.stat.UnpackedStatType

public object StatTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedStatType>,
        ctx: EncoderContext,
    ): List<UnpackedStatType> {
        // Stats are a server-only config.
        if (ctx.clientOnly) {
            return emptyList()
        }
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.STAT
        val packed = mutableListOf<UnpackedStatType>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf = buffer.clear().encodeConfig { encodeGame(type, this) }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
                packed += type
            }
            oldBuf?.release()
        }
        buffer.release()
        return packed
    }

    public fun encodeGame(type: UnpackedStatType, data: ByteBuf): Unit =
        with(type) {
            data.writeByte(1)
            data.writeString(displayName)

            if (unreleased) {
                data.writeByte(2)
            }

            if (maxLevel != StatTypeBuilder.DEFAULT_MAX_LEVEL) {
                data.writeByte(3)
                data.writeByte(maxLevel)
            }
        }
}
