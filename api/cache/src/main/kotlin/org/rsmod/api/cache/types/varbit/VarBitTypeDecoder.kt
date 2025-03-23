package org.rsmod.api.cache.types.varbit

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.varbit.UnpackedVarBitType
import org.rsmod.game.type.varbit.VarBitTypeBuilder
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList

public object VarBitTypeDecoder {
    public fun decodeAll(cache: Cache): VarBitTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedVarBitType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.VARBIT)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.VARBIT, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return VarBitTypeList(types)
    }

    public fun decode(data: ByteBuf): VarBitTypeBuilder {
        val builder = VarBitTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: VarBitTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> {
                    baseVar = data.readUnsignedShort()
                    lsb = data.readUnsignedByte().toInt()
                    msb = data.readUnsignedByte().toInt()
                }
                else -> throw IOException("Error unrecognised .varbit config code: $code")
            }
        }

    public fun assignBaseVars(varbits: VarBitTypeList, varps: VarpTypeList) {
        val grouped = varbits.values.groupBy { it.varpId }
        for ((baseVar, children) in grouped) {
            val varp = varps[baseVar] ?: error("VarpType with id `$baseVar` does not exist.")
            for (varbit in children) {
                TypeResolver[varbit] = varp
            }
        }
    }
}
