package org.rsmod.api.cache.types.enums

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.enums.UnpackedEnumType
import org.rsmod.game.type.literal.BaseCacheVarType

public object EnumTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedEnumType<*, *>>,
        ctx: EncoderContext,
    ): List<UnpackedEnumType<*, *>> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.ENUM
        val packed = mutableListOf<UnpackedEnumType<*, *>>()
        for (type in types) {
            // Skip server-side enums when packing into the client cache.
            if (!type.transmit && ctx.clientOnly) {
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

    public fun encodeJs5(type: UnpackedEnumType<*, *>, data: ByteBuf): Unit =
        with(type) {
            data.writeByte(1)
            data.writeByte(keyLiteral.char.code)

            data.writeByte(2)
            data.writeByte(valLiteral.char.code)

            defaultStr?.let { default ->
                check(defaultInt == null) { "`defaultInt` must not be set." }
                data.writeByte(3)
                data.writeString(default)
            }

            defaultInt?.let { default ->
                check(defaultStr == null) { "`defaultStr` must not be set." }
                data.writeByte(4)
                data.writeInt(default)
            }

            when (valLiteral.type) {
                BaseCacheVarType.String -> {
                    data.writeByte(5)
                    data.writeShort(primitiveMap.size)
                    for ((key, value) in primitiveMap) {
                        data.writeInt(key as Int)
                        data.writeString(value as? String ?: "")
                    }
                }
                BaseCacheVarType.Integer -> {
                    data.writeByte(6)
                    data.writeShort(primitiveMap.size)
                    for ((key, value) in primitiveMap) {
                        data.writeInt(key as Int)
                        data.writeInt(value as? Int ?: -1)
                    }
                }
            }
        }

    @Suppress("unused")
    public fun encodeGame(type: UnpackedEnumType<*, *>, data: ByteBuf): Unit =
        with(type) {
            if (!transmit) {
                data.writeByte(200)
            }
        }
}
