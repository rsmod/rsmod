package org.rsmod.plugins.api.cache.type.varbit

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.type.ConfigType
import org.rsmod.plugins.api.cache.type.varp.VarpType

private const val CONFIG_ARCHIVE = 2
private const val VARBIT_GROUP = 14

public object VarbitTypePacker {

    public fun pack(cache: Cache, types: Iterable<VarbitType>, isJs5: Boolean): List<VarbitType> {
        val buf = Unpooled.buffer()
        val packed = mutableListOf<VarbitType>()
        types.forEach { type ->
            buf.clear().writeType(type, isJs5)
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

    private fun ByteBuf.writeType(type: VarbitType, isJs5: Boolean) {
        writeByte(1)
        writeShort(type.varp)
        writeByte(type.lsb)
        writeByte(type.msb)
        if (!isJs5) {
            if (type.transmit) {
                writeByte(VarpType.TRANSMISSION_OPCODE)
            }
            type.alias?.let {
                writeByte(ConfigType.ALIAS_OPCODE)
                writeString(type.alias)
            }
        }
        writeByte(0)
    }
}
