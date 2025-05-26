package org.rsmod.api.cache.types.dbrow

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.buffer.writeUnsignedShortSmart
import org.openrs2.buffer.writeVarInt
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.readOrNull
import org.rsmod.api.cache.util.writeColumnValues
import org.rsmod.game.type.dbrow.UnpackedDbRowType

public object DbRowTypeEncoder {
    public fun encodeAll(cache: Cache, types: Iterable<UnpackedDbRowType>, ctx: EncoderContext) {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.DBROW
        for (type in types) {
            // Skip db rows when their parent tables are server-side only.
            if (ctx.clientOnly && type.table !in ctx.clientTables) {
                continue
            }
            val oldBuf = cache.readOrNull(archive, config, type.id)
            val newBuf = buffer.clear().encodeConfig { encodeJs5(type, this) }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
            }
            oldBuf?.release()
        }
        buffer.release()
    }

    public fun encodeJs5(type: UnpackedDbRowType, data: ByteBuf): Unit =
        with(type) {
            val columns = this.data
            if (columns.isNotEmpty()) {
                data.writeByte(3)
                data.writeByte(columnCount)
                for ((columnId, values) in columns) {
                    val types =
                        types[columnId] ?: error("`types` not set for column: $columnId ($type)")
                    data.writeByte(columnId)
                    data.writeByte(types.size)
                    types.forEach(data::writeUnsignedShortSmart)
                    data.writeColumnValues(values, types)
                }
                data.writeByte(0xFF)
            }
            data.writeByte(4)
            data.writeVarInt(table)
        }
}
