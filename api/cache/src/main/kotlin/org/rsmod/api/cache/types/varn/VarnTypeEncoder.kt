package org.rsmod.api.cache.types.varn

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.varn.UnpackedVarnType

public object VarnTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedVarnType>,
        ctx: EncoderContext,
    ): List<UnpackedVarnType> {
        // Varns are a server-only config.
        if (ctx.clientOnly) {
            return emptyList()
        }
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.VARNPC
        val packed = mutableListOf<UnpackedVarnType>()
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

    public fun encodeGame(type: UnpackedVarnType, data: ByteBuf): Unit =
        with(type) {
            if (!bitProtect) {
                data.writeByte(1)
            }
        }
}
