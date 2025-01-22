package org.rsmod.api.cache.types.varp

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.varp.UnpackedVarpType
import org.rsmod.game.type.varp.VarpTypeBuilder

public object VarpTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedVarpType>,
        serverCache: Boolean,
    ): List<UnpackedVarpType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.VARPLAYER
        val packed = mutableListOf<UnpackedVarpType>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf =
                buffer.clear().encodeConfig {
                    encodeJs5(type, this)
                    if (serverCache) {
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
            if (!transmit) {
                data.writeByte(200)
            }
            if (protect) {
                data.writeByte(201)
            }
            if (bitProtect) {
                data.writeByte(202)
            }
        }
}
