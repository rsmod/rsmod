package org.runerealm.api.cache.types.struct

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.runerealm.api.cache.Js5Archives
import org.runerealm.api.cache.Js5Configs
import org.runerealm.api.cache.util.EncoderContext
import org.runerealm.api.cache.util.encodeConfig
import org.runerealm.api.cache.util.filterTransmit
import org.runerealm.api.cache.util.writeRawParams
import org.runerealm.game.type.struct.UnpackedStructType

public object StructTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedStructType>,
        ctx: EncoderContext,
    ): List<UnpackedStructType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.STRUCT
        val packed = mutableListOf<UnpackedStructType>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf =
                buffer.clear().encodeConfig {
                    encodeJs5(type, this, ctx)
                    if (ctx.encodeFull) {
                        encodeGame(type, this)
                    }
                }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
                packed += type
            }
            oldBuf?.release()
        }
        buffer.release()
        return packed
    }

    public fun encodeJs5(type: UnpackedStructType, data: ByteBuf, ctx: EncoderContext): Unit =
        with(type) {
            val params = paramMap?.filterTransmit(ctx)?.primitiveMap
            if (params?.isNotEmpty() == true) {
                data.writeByte(249)
                data.writeRawParams(params)
            }
        }

    public fun encodeGame(type: UnpackedStructType, data: ByteBuf): Unit = with(type) {}
}
