package org.rsmod.api.cache.types.enums

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.enums.UnpackedEnumType
import org.rsmod.game.type.literal.BaseCacheVarType

public object EnumTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedEnumType<*, *>>,
        serverCache: Boolean,
        reusableBuf: ByteBuf,
    ): List<UnpackedEnumType<*, *>> {
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.ENUM
        val packed = mutableListOf<UnpackedEnumType<*, *>>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf =
                reusableBuf.clear().encodeConfig {
                    encodeJs5(type, this)
                    if (serverCache) {
                        encodeGame(type, this)
                    }
                }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
                packed += type
            }
        }
        return packed
    }

    public fun encodeFull(type: UnpackedEnumType<*, *>, data: ByteBuf): ByteBuf =
        data.encodeConfig {
            encodeJs5(type, this)
            encodeGame(type, this)
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
    public fun encodeGame(type: UnpackedEnumType<*, *>, data: ByteBuf): Unit = with(type) {}
}
