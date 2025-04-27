package org.rsmod.api.cache.types.varbit

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.varbit.UnpackedVarBitType

public object VarBitTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedVarBitType>,
        ctx: EncoderContext,
    ): List<UnpackedVarBitType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.VARBIT
        val packed = mutableListOf<UnpackedVarBitType>()
        for (type in types) {
            // Skip server-side varps when packing into the client cache.
            if (ctx.clientOnly && type.varpId !in ctx.clientVarps) {
                continue
            }
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf =
                buffer.clear().encodeConfig {
                    encodeJs5(type, this)
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

    public fun encodeJs5(type: UnpackedVarBitType, data: ByteBuf): Unit =
        with(type) {
            check(varpId >= 0) { "`baseVar` must be set. (type=$type)" }
            data.writeByte(1)
            data.writeShort(varpId)
            data.writeByte(lsb)
            data.writeByte(msb)
        }

    public fun encodeGame(type: UnpackedVarBitType, data: ByteBuf): Unit = with(type) {}
}
