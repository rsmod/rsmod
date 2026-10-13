package org.runerealm.api.cache.types.mod

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.runerealm.api.cache.Js5Archives
import org.runerealm.api.cache.Js5Configs
import org.runerealm.api.cache.util.EncoderContext
import org.runerealm.api.cache.util.encodeConfig
import org.runerealm.api.cache.util.readOrNull
import org.runerealm.game.type.mod.UnpackedModLevelType

public object ModLevelTypeEncoder {
    public fun encodeAll(cache: Cache, types: Iterable<UnpackedModLevelType>, ctx: EncoderContext) {
        if (ctx.clientOnly) {
            return
        }
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.MODLEVEL
        for (type in types) {
            val oldBuf = cache.readOrNull(archive, config, type.id)
            val newBuf = buffer.clear().encodeConfig { encodeGame(type, this) }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
            }
            oldBuf?.release()
        }
        buffer.release()
    }

    public fun encodeGame(type: UnpackedModLevelType, data: ByteBuf): Unit =
        with(type) {
            data.writeByte(1)
            data.writeByte(clientCode)

            if (accessFlags != 0L) {
                data.writeByte(2)
                data.writeLong(accessFlags)
            }
        }
}
