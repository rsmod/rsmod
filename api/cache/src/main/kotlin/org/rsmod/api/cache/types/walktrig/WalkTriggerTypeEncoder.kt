package org.rsmod.api.cache.types.walktrig

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.type.walktrig.WalkTriggerTypeBuilder

public object WalkTriggerTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<WalkTriggerType>,
        ctx: EncoderContext,
    ): List<WalkTriggerType> {
        // Walk triggers are a server-only config.
        if (ctx.clientOnly) {
            return emptyList()
        }
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.WALKTRIGGER
        val packed = mutableListOf<WalkTriggerType>()
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

    public fun encodeGame(type: WalkTriggerType, data: ByteBuf): Unit =
        with(type) {
            if (priority != WalkTriggerTypeBuilder.DEFAULT_PRIORITY) {
                data.writeByte(1)
                data.writeByte(priority.id)
            }
        }
}
