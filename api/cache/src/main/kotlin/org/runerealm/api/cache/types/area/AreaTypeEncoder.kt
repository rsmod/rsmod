package org.runerealm.api.cache.types.area

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.runerealm.api.cache.Js5Archives
import org.runerealm.api.cache.Js5Configs
import org.runerealm.api.cache.util.EncoderContext
import org.runerealm.api.cache.util.encodeConfig
import org.runerealm.api.cache.util.readOrNull
import org.runerealm.game.type.area.AreaTypeBuilder
import org.runerealm.game.type.area.UnpackedAreaType

public object AreaTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedAreaType>,
        ctx: EncoderContext,
    ): List<UnpackedAreaType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.AREA
        val packed = mutableListOf<UnpackedAreaType>()
        for (type in types) {
            val oldBuf = cache.readOrNull(archive, config, type.id)
            val newBuf =
                buffer.clear().encodeConfig {
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

    public fun encodeGame(type: UnpackedAreaType, data: ByteBuf): Unit =
        with(type) {
            if (colour != AreaTypeBuilder.DEFAULT_COLOUR) {
                data.writeByte(200)
                data.writeInt(colour)
            }
        }
}
