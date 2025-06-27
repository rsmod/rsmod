package org.rsmod.api.cache.types.proj

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.proj.ProjAnimTypeBuilder
import org.rsmod.game.type.proj.UnpackedProjAnimType

public object ProjAnimTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedProjAnimType>,
        ctx: EncoderContext,
    ): List<UnpackedProjAnimType> {
        // Projanims are a server-only config.
        if (ctx.clientOnly) {
            return emptyList()
        }
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.PROJANIM
        val packed = mutableListOf<UnpackedProjAnimType>()
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

    public fun encodeGame(type: UnpackedProjAnimType, data: ByteBuf): Unit =
        with(type) {
            val extendedStartHeight = startHeight > 255
            if (!extendedStartHeight) {
                data.writeByte(1)
                data.writeByte(startHeight)
            }

            val extendedEndHeight = endHeight > 255
            if (!extendedEndHeight) {
                data.writeByte(2)
                data.writeByte(endHeight)
            }

            data.writeByte(3)
            data.writeByte(delay)

            data.writeByte(4)
            data.writeByte(angle)

            if (lengthAdjustment != 0) {
                data.writeByte(5)
                data.writeByte(lengthAdjustment)
            }

            if (progress != ProjAnimTypeBuilder.DEFAULT_PROGRESS) {
                data.writeByte(6)
                data.writeByte(progress)
            }

            if (stepMultiplier != ProjAnimTypeBuilder.DEFAULT_STEP_MULTIPLIER) {
                data.writeByte(7)
                data.writeByte(stepMultiplier)
            }

            if (extendedStartHeight) {
                data.writeByte(8)
                data.writeShort(startHeight)
            }

            if (extendedEndHeight) {
                data.writeByte(9)
                data.writeShort(endHeight)
            }
        }
}
