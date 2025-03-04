package org.rsmod.api.cache.types.varnbit

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.varn.VarnTypeList
import org.rsmod.game.type.varnbit.UnpackedVarnBitType
import org.rsmod.game.type.varnbit.VarnBitTypeBuilder
import org.rsmod.game.type.varnbit.VarnBitTypeList

public object VarnBitTypeDecoder {
    public fun decodeAll(cache: Cache, symbols: Map<String, Int>): VarnBitTypeList {
        if (!cache.exists(Js5Archives.CONFIG, Js5Configs.VARNBIT)) {
            return createDefaultTypeList(symbols)
        }
        val types = Int2ObjectOpenHashMap<UnpackedVarnBitType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.VARNBIT)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.VARNBIT, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return VarnBitTypeList(types)
    }

    public fun decode(data: ByteBuf): VarnBitTypeBuilder {
        val builder = VarnBitTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: VarnBitTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> {
                    baseVar = data.readUnsignedShort()
                    lsb = data.readUnsignedByte().toInt()
                    msb = data.readUnsignedByte().toInt()
                }
                else -> throw IOException("Error unrecognised .varnbit config code: $code")
            }
        }

    public fun assignInternal(list: VarnBitTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }

    public fun assignBaseVars(varnbits: VarnBitTypeList, varns: VarnTypeList) {
        val grouped = varnbits.values.groupBy { it.varnId }
        for ((baseVar, children) in grouped) {
            val varn = varns[baseVar] ?: error("VarnType with id `$baseVar` does not exist.")
            for (varnbit in children) {
                TypeResolver.setBaseVar(varnbit, varn)
            }
        }
    }

    private fun createDefaultTypeList(symbols: Map<String, Int>): VarnBitTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedVarnBitType>()
        for ((name, id) in symbols) {
            val builder = VarnBitTypeBuilder(name)
            types[id] = builder.buildDefault(id)
        }
        return VarnBitTypeList(types)
    }
}
