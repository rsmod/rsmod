package org.rsmod.api.cache.types.inv

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType
import org.rsmod.game.type.inv.InvStock
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.inv.UnpackedInvType

public object InvTypeDecoder {
    public fun decodeAll(cache: Cache): InvTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedInvType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.INV)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.INV, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return InvTypeList(types)
    }

    public fun decode(data: ByteBuf): InvTypeBuilder {
        val builder = InvTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    @Suppress("DEPRECATION")
    public fun decode(builder: InvTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                2 -> size = data.readUnsignedShort()
                200 -> {
                    val id = data.readByte().toInt()
                    scope = InvScope.entries.first { it.id == id }
                }
                201 -> {
                    val id = data.readByte().toInt()
                    stack = InvStackType.entries.first { it.id == id }
                }
                202 -> size = data.readUnsignedByte().toInt()
                203 -> size = data.readUnsignedShort()
                204 -> flags = data.readInt()
                205 -> {
                    val stockCount = data.readUnsignedByte().toInt() + 1
                    val defaultStock = arrayOfNulls<InvStock>(stockCount)
                    for (i in defaultStock.indices) {
                        val countHeader = data.readUnsignedByte().toInt()
                        if (countHeader == 0) {
                            continue
                        }
                        val count =
                            if (countHeader == 255) {
                                data.readInt()
                            } else {
                                countHeader - 1
                            }
                        val obj = data.readUnsignedShort()
                        val restockCycles = data.readUnsignedByte().toInt()
                        defaultStock[i] = InvStock(obj, count, restockCycles)
                    }
                    this.stock = defaultStock
                }
                else -> throw IOException("Error unrecognised .inv config code: $code")
            }
        }

    public fun assignInternal(list: InvTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }
}
