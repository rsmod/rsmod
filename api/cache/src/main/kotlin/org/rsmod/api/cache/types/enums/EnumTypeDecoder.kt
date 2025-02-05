package org.rsmod.api.cache.types.enums

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.enums.EnumTypeBuilder
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.enums.UnpackedEnumType

public object EnumTypeDecoder {
    public fun decodeAll(cache: Cache): EnumTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedEnumType<Any, Any>>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.ENUM)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.ENUM, file.id)
            val type = data.use { decode(it).build(file.id) } ?: continue
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return EnumTypeList(types)
    }

    public fun decode(data: ByteBuf): EnumTypeBuilder<Any, Any> {
        val builder = EnumTypeBuilder<Any, Any>(Any::class, Any::class)
        builder.internal = TextUtil.NULL
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    @Suppress("UNCHECKED_CAST")
    public fun decode(builder: EnumTypeBuilder<Any, Any>, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> keyCharId = data.readUnsignedByte().toInt().toChar()
                2 -> valCharId = data.readUnsignedByte().toInt().toChar()
                3 -> defaultStr = data.readString()
                4 -> defaultInt = data.readInt()
                5 -> {
                    val map = hashMapOf<Int, String>()
                    val count = data.readUnsignedShort()
                    repeat(count) {
                        val key = data.readInt()
                        val value = data.readString()
                        map[key] = value
                    }
                    this.primitiveMap = map as Map<Any, Any>
                }
                6 -> {
                    val map = hashMapOf<Int, Int>()
                    val count = data.readUnsignedShort()
                    repeat(count) {
                        val key = data.readInt()
                        val value = data.readInt()
                        map[key] = value
                    }
                    this.primitiveMap = map as Map<Any, Any>
                }
                200 -> transmit = false
                else -> throw IOException("Error unrecognised .enum config code: $code")
            }
        }

    public fun assignInternal(list: EnumTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }
}
