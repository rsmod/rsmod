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
            writeType(buf.clear(), type, isJs5)
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

    public fun writeType(buf: ByteBuf, type: EnumType<Any, Any>, isJs5: Boolean) {
        buf.writeByte(1)
        buf.writeByte(type.keyType.char.code)
        buf.writeByte(2)
        buf.writeByte(type.valType.char.code)
        if (type.valType.isString) {
            type.default?.let { default ->
                buf.writeByte(3)
                buf.writeString(type.valType.encodeString(default))
            }
            buf.writeByte(5)
            buf.writeShort(type.size)
            type.forEach { (key, value) ->
                val encodedKey = type.keyType.encodeInt(key)
                val encodedValue = type.valType.encodeString(value)
                buf.writeInt(encodedKey)
                buf.writeString(encodedValue)
            }
        } else if (type.valType.isInt) {
            type.default?.let { default ->
                buf.writeByte(4)
                buf.writeInt(type.valType.encodeInt(default))
            }
            buf.writeByte(6)
            buf.writeShort(type.size)
            type.forEach { (key, value) ->
                val encodedKey = type.keyType.encodeInt(key)
                val encodedValue = type.valType.encodeInt(value)
                buf.writeInt(encodedKey)
                buf.writeInt(encodedValue)
            }
        }
        if (!isJs5) {
            if (type.transmit) {
                buf.writeByte(ConfigType.TRANSMISSION_OPCODE)
            }
            type.name?.let {
                buf.writeByte(ConfigType.INTERNAL_NAME_OPCODE)
                buf.writeString(it)
            }
        }
        buf.writeByte(0)
    }
}
