package org.rsmod.api.cache.types.struct

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.writeRawParams
import org.rsmod.game.type.struct.UnpackedStructType

public object StructTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedStructType>,
        serverCache: Boolean,
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

    public fun encodeJs5(type: UnpackedStructType, data: ByteBuf): Unit =
        with(type) {
            paramMap?.let {
                data.writeByte(249)
                data.writeRawParams(it.primitiveMap)
            }
        }

    public fun encodeGame(type: UnpackedStructType, data: ByteBuf): Unit = with(type) {}
}
