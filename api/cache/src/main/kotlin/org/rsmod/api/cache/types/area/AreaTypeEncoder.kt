package org.rsmod.api.cache.types.area

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.readOrNull
import org.rsmod.game.type.area.AreaTypeBuilder
import org.rsmod.game.type.area.UnpackedAreaType

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
