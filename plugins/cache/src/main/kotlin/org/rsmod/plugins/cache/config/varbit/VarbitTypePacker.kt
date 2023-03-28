package org.rsmod.plugins.cache.config.varbit

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.plugins.cache.config.ConfigType

public object VarbitTypePacker {

    private const val CONFIG_ARCHIVE = 2
    private const val VARBIT_GROUP = 14

    public fun pack(cache: Cache, types: Iterable<VarbitType>, isJs5: Boolean): List<VarbitType> {
        val buf = Unpooled.buffer()
        val packed = mutableListOf<VarbitType>()
        types.forEach { type ->
            writeType(buf.clear(), type, isJs5)
            val oldData = if (cache.exists(CONFIG_ARCHIVE, VARBIT_GROUP, type.id)) {
                cache.read(CONFIG_ARCHIVE, VARBIT_GROUP, type.id)
            } else {
                null
            }
            if (buf == oldData) return@forEach
            cache.write(CONFIG_ARCHIVE, VARBIT_GROUP, type.id, buf)
            packed += type
        }
        return packed
    }

    public fun writeType(buf: ByteBuf, type: VarbitType, isJs5: Boolean) {
        buf.writeByte(1)
        buf.writeShort(type.varp)
        buf.writeByte(type.lsb)
        buf.writeByte(type.msb)
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
