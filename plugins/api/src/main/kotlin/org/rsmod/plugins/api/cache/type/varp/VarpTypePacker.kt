package org.rsmod.plugins.api.cache.type.varp

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.type.ConfigType

public object VarpTypePacker {

    private const val CONFIG_ARCHIVE = 2
    private const val VARP_GROUP = 16

    public fun pack(cache: Cache, types: Iterable<VarpType>, isJs5: Boolean): List<VarpType> {
        val buf = Unpooled.buffer()
        val packed = mutableListOf<VarpType>()
        types.forEach { type ->
            writeType(buf.clear(), type, isJs5)
            val oldData = if (cache.exists(CONFIG_ARCHIVE, VARP_GROUP, type.id)) {
                cache.read(CONFIG_ARCHIVE, VARP_GROUP, type.id)
            } else {
                null
            }
            if (buf == oldData) return@forEach
            cache.write(CONFIG_ARCHIVE, VARP_GROUP, type.id, buf)
            packed += type
        }
        return packed
    }

    public fun writeType(buf: ByteBuf, type: VarpType, isJs5: Boolean) {
        type.clientCode?.let {
            buf.writeByte(5)
            buf.writeShort(it)
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
