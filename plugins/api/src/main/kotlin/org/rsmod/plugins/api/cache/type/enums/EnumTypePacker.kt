package org.rsmod.plugins.api.cache.type.enums

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.type.ConfigType

private const val CONFIG_ARCHIVE = 2
private const val ENUM_GROUP = 8

public object EnumTypePacker {

    public fun pack(
        cache: Cache,
        types: Iterable<EnumType<Any, Any>>,
        isJs5: Boolean
    ): List<EnumType<Any, Any>> {
        val buf = Unpooled.buffer()
        val packed = mutableListOf<EnumType<Any, Any>>()
        types.forEach { type ->
            buf.clear().writeType(type, isJs5)
            val oldData = if (cache.exists(CONFIG_ARCHIVE, ENUM_GROUP, type.id)) {
                cache.read(CONFIG_ARCHIVE, ENUM_GROUP, type.id)
            } else {
                null
            }
            if (buf == oldData) return@forEach
            cache.write(CONFIG_ARCHIVE, ENUM_GROUP, type.id, buf)
            packed += type
        }
        return packed
    }

    private fun ByteBuf.writeType(type: EnumType<Any, Any>, isJs5: Boolean) {
        writeByte(1)
        writeByte(type.keyType.char.code)
        writeByte(2)
        writeByte(type.valType.char.code)
        if (type.valType.isString) {
            type.default?.let { default ->
                writeByte(3)
                writeString(type.valType.encodeString(default))
            }
            writeByte(5)
            writeShort(type.size)
            type.forEach { (key, value) ->
                val encodedKey = type.keyType.encodeInt(key)
                val encodedValue = type.valType.encodeString(value)
                writeInt(encodedKey)
                writeString(encodedValue)
            }
        } else if (type.valType.isInt) {
            type.default?.let { default ->
                writeByte(4)
                writeInt(type.valType.encodeInt(default))
            }
            writeByte(6)
            writeShort(type.size)
            type.forEach { (key, value) ->
                val encodedKey = type.keyType.encodeInt(key)
                val encodedValue = type.valType.encodeInt(value)
                writeInt(encodedKey)
                writeInt(encodedValue)
            }
        }
        if (!isJs5) {
            if (type.transmit) {
                writeByte(ConfigType.TRANSMISSION_OPCODE)
            }
            type.name?.let {
                writeByte(ConfigType.INTERNAL_NAME_OPCODE)
                writeString(it)
            }
        }
        writeByte(0)
    }
}
