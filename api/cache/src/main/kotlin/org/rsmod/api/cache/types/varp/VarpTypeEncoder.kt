package org.rsmod.api.cache.types.varp

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.varp.UnpackedVarpType
import org.rsmod.game.type.varp.VarpTypeBuilder

public object VarpTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedVarpType>,
        ctx: EncoderContext,
    ): List<UnpackedVarpType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.VARPLAYER
        val packed = mutableListOf<UnpackedVarpType>()
        for (type in types) {
            // Skip server-side varps when packing into the client cache.
            if (ctx.clientOnly && type.transmit.never) {
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

    public fun encodeJs5(type: UnpackedVarpType, data: ByteBuf): Unit =
        with(type) {
            if (clientCode != VarpTypeBuilder.DEFAULT_CLIENT_CODE) {
                data.writeByte(5)
                data.writeShort(clientCode)
            }
        }

    public fun encodeGame(type: UnpackedVarpType, data: ByteBuf): Unit =
        with(type) {
            // Note: Opcodes 200-201 are free to use.

            if (bitProtect) {
                data.writeByte(202)
            }

            if (scope != VarpTypeBuilder.DEFAULT_SCOPE) {
                data.writeByte(203)
                data.writeByte(scope.id)
            }

            if (transmit != VarpTypeBuilder.DEFAULT_TRANSMIT) {
                data.writeByte(204)
                data.writeByte(transmit.id)
            }
        }
}
