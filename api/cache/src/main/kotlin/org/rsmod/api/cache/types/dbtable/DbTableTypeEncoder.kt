package org.rsmod.api.cache.types.dbtable

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.buffer.writeUnsignedShortSmart
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.readOrNull
import org.rsmod.api.cache.util.writeColumnValues
import org.rsmod.game.type.dbtable.UnpackedDbTableType

public object DbTableTypeEncoder {
    public fun encodeAll(cache: Cache, types: Iterable<UnpackedDbTableType>, ctx: EncoderContext) {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.DBTABLE
        for (type in types) {
            // Skip server-side db tables when packing into the client cache.
            val skipWrite = ctx.clientOnly && type.id !in ctx.clientTables
            val oldBuf = cache.readOrNull(archive, config, type.id)
            val newBuf =
                buffer.clear().encodeConfig {
                    if (!skipWrite) {
                        encodeJs5(type, this)
                        if (ctx.encodeFull) {
                            encodeGame(type, this)
                        }
                    }
                }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
            }
            oldBuf?.release()
        }
        buffer.release()
    }

    public fun encodeJs5(type: UnpackedDbTableType, data: ByteBuf): Unit =
        with(type) {
            if (types.isNotEmpty()) {
                data.writeByte(1)
                data.writeByte(columnCount)
                for ((columnId, typeList) in types) {
                    var attributes = columnId and 0x7F
                    val defaultValue = defaults[columnId]
                    if (defaultValue != null) {
                        attributes = attributes or UnpackedDbTableType.REQUIRED
                    }
                    data.writeByte(attributes)

                    data.writeByte(typeList.size)
                    typeList.forEach(data::writeUnsignedShortSmart)

                    if (defaultValue != null) {
                        data.writeColumnValues(defaultValue, typeList)
                    }
                }
                data.writeByte(0xFF)
            }
        }

    public fun encodeGame(type: UnpackedDbTableType, data: ByteBuf): Unit =
        with(type) {
            data.writeByte(200)
            attributes.values.forEach(data::writeShort)
            data.writeShort(0xFFFF)
        }
}
