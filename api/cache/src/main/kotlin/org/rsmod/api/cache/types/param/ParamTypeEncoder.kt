package org.rsmod.api.cache.types.param

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.param.ParamTypeBuilder
import org.rsmod.game.type.param.UnpackedParamType

public object ParamTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedParamType<*>>,
        serverCache: Boolean,
        reusableBuf: ByteBuf,
    ): List<UnpackedParamType<*>> {
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.PARAM
        val packed = mutableListOf<UnpackedParamType<*>>()
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

    public fun encodeFull(type: UnpackedParamType<*>, data: ByteBuf): ByteBuf =
        data.encodeConfig {
            encodeJs5(type, this)
            encodeGame(type, this)
        }

    public fun encodeJs5(type: UnpackedParamType<*>, data: ByteBuf): Unit =
        with(type) {
            typeLiteral?.let {
                data.writeByte(1)
                data.writeByte(it.char.code)
            }
            defaultInt?.let {
                data.writeByte(2)
                data.writeInt(it)
            }
            if (autoDisable != ParamTypeBuilder.DEFAULT_AUTO_DISABLE) {
                data.writeByte(4)
            }
            defaultStr?.let {
                data.writeByte(5)
                data.writeString(it)
            }
        }

    public fun encodeGame(type: UnpackedParamType<*>, data: ByteBuf): Unit = with(type) {}
}
