package org.rsmod.plugins.api.cache.type.varp

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.type.ConfigType

private const val CONFIG_ARCHIVE = 2
private const val VARP_GROUP = 16

public object VarpTypePacker {

    public fun pack(isJs5: Boolean, cache: Cache, types: Iterable<VarpType>): List<VarpType> {
        val buf = Unpooled.buffer()
        val packed = mutableListOf<VarpType>()
        types.forEach { type ->
            buf.clear().writeType(type, isJs5)
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

    private fun ByteBuf.writeType(type: VarpType, isJs5: Boolean) {
        type.clientCode?.let {
            writeByte(5)
            writeShort(it)
        }
        if (!isJs5) {
            if (type.transmit) {
                writeByte(VarpType.TRANSMISSION_OPCODE)
            }
            type.alias?.let {
                writeByte(ConfigType.ALIAS_OPCODE)
                writeString(it)
            }
        }
        writeByte(0)
    }
}
