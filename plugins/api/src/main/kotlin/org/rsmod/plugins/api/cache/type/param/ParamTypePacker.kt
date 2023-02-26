package org.rsmod.plugins.api.cache.type.param

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.type.ConfigType

public object ParamTypePacker {

    private const val CONFIG_ARCHIVE = 2
    private const val PARAM_GROUP = 11

    public fun pack(cache: Cache, types: Iterable<ParamType>, isJs5: Boolean): List<ParamType> {
        val buf = Unpooled.buffer()
        val packed = mutableListOf<ParamType>()
        types.forEach { type ->
            buf.clear().writeType(type, isJs5)
            val oldData = if (cache.exists(CONFIG_ARCHIVE, PARAM_GROUP, type.id)) {
                cache.read(CONFIG_ARCHIVE, PARAM_GROUP, type.id)
            } else {
                null
            }
            if (buf == oldData) return@forEach
            cache.write(CONFIG_ARCHIVE, PARAM_GROUP, type.id, buf)
            packed += type
        }
        return packed
    }

    private fun ByteBuf.writeType(param: ParamType, isJs5: Boolean) {
        param.type?.let {
            writeByte(1)
            writeByte(it.char.code)
        }
        param.default?.let {
            val type = param.type
            if (type == null) {
                if (it is String) {
                    writeByte(5)
                    writeString(it)
                } else if (it is Int) {
                    writeByte(2)
                    writeInt(it)
                }
            } else if (type.isString) {
                val encoded = type.encodeString(it)
                writeByte(5)
                writeString(encoded)
            } else if (type.isInt) {
                val encoded = type.encodeInt(it)
                writeByte(2)
                writeInt(encoded)
            }
            return@let
        }
        if (!param.autoDisable) {
            writeByte(4)
        }
        if (!isJs5) {
            if (param.transmit) {
                writeByte(ConfigType.TRANSMISSION_OPCODE)
            }
            param.name?.let {
                writeByte(ConfigType.INTERNAL_NAME_OPCODE)
                writeString(it)
            }
        }
        writeByte(0)
    }
}
