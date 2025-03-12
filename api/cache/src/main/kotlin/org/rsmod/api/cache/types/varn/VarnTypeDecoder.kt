package org.rsmod.api.cache.types.varn

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.varn.UnpackedVarnType
import org.rsmod.game.type.varn.VarnTypeBuilder
import org.rsmod.game.type.varn.VarnTypeList

public object VarnTypeDecoder {
    public fun decodeAll(cache: Cache, symbols: Map<String, Int>): VarnTypeList {
        if (!cache.exists(Js5Archives.CONFIG, Js5Configs.VARNPC)) {
            return createDefaultTypeList(symbols)
        }
        val types = Int2ObjectOpenHashMap<UnpackedVarnType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.VARNPC)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.VARNPC, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return VarnTypeList(types)
    }

    public fun decode(data: ByteBuf): VarnTypeBuilder {
        val builder = VarnTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    @Suppress("unused")
    public fun decode(builder: VarnTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> bitProtect = false
                else -> throw IOException("Error unrecognised .varn config code: $code")
            }
        }

    public fun assignInternal(list: VarnTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }

    private fun createDefaultTypeList(symbols: Map<String, Int>): VarnTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedVarnType>()
        for ((name, id) in symbols) {
            val builder = VarnTypeBuilder(name)
            types[id] = builder.build(id)
        }
        return VarnTypeList(types)
    }
}
