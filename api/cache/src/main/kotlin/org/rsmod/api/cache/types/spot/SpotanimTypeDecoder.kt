package org.rsmod.api.cache.types.spot

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import kotlin.collections.iterator
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.readUnsignedShortOrNull
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.spot.SpotanimTypeBuilder
import org.rsmod.game.type.spot.SpotanimTypeList
import org.rsmod.game.type.spot.UnpackedSpotanimType
import org.rsmod.game.type.util.CompactableIntArray

public object SpotanimTypeDecoder {
    public fun decodeAll(cache: Cache): SpotanimTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedSpotanimType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.SPOTANIM)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.SPOTANIM, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return SpotanimTypeList(types)
    }

    private fun decode(data: ByteBuf): SpotanimTypeBuilder {
        val builder = SpotanimTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: SpotanimTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> model = data.readUnsignedShortOrNull()
                2 -> anim = data.readUnsignedShort()
                4 -> resizeH = data.readUnsignedShort()
                5 -> resizeV = data.readUnsignedShort()
                6 -> rotation = data.readUnsignedShort()
                7 -> ambient = data.readUnsignedByte().toInt()
                8 -> contrast = data.readUnsignedByte().toInt()
                40,
                41 -> {
                    val count = data.readUnsignedByte().toInt()
                    val src = IntArray(count)
                    val dest = IntArray(count)
                    repeat(count) {
                        src[it] = data.readUnsignedShort()
                        dest[it] = data.readUnsignedShort()
                    }
                    when (code) {
                        40 -> {
                            recolS = CompactableIntArray(src)
                            recolD = CompactableIntArray(dest)
                        }
                        41 -> {
                            retexS = CompactableIntArray(src)
                            retexD = CompactableIntArray(dest)
                        }
                        else -> throw NotImplementedError("Unhandled .spotanim config code: $code")
                    }
                }
                else -> throw IOException("Error unrecognised .spotanim config code: $code")
            }
        }
}
