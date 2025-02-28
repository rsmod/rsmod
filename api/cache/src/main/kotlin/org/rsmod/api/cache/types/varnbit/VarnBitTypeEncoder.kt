package org.rsmod.api.cache.types.varnbit

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.varnbit.UnpackedVarnBitType

public object VarnBitTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedVarnBitType>,
        ctx: EncoderContext,
    ): List<UnpackedVarnBitType> {
        // Varnbits are a server-only config.
        if (ctx.clientOnly) {
            return emptyList()
        }
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.VARNBIT
        val packed = mutableListOf<UnpackedVarnBitType>()
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

    public fun encodeGame(type: UnpackedVarnBitType, data: ByteBuf): Unit =
        with(type) {
            check(varnId >= 0) { "`baseVar` must be set. (type=$type)" }
            data.writeByte(1)
            data.writeShort(varnId)
            data.writeByte(lsb)
            data.writeByte(msb)
        }
}
