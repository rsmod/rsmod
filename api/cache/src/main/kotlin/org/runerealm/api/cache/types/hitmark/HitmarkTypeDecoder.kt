package org.runerealm.api.cache.types.hitmark

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.readVersionedString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.runerealm.api.cache.Js5Archives
import org.runerealm.api.cache.Js5Configs
import org.runerealm.api.cache.util.TextUtil
import org.runerealm.api.cache.util.readNullableLargeSmart
import org.runerealm.api.cache.util.readUnsignedShortOrNull
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.hitmark.HitmarkTypeBuilder
import org.runerealm.game.type.hitmark.HitmarkTypeList
import org.runerealm.game.type.hitmark.UnpackedHitmarkType

public object HitmarkTypeDecoder {
    public fun decodeAll(cache: Cache): HitmarkTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedHitmarkType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.HITMARK)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.HITMARK, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return HitmarkTypeList(types)
    }

    public fun decode(data: ByteBuf): HitmarkTypeBuilder {
        val builder = HitmarkTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: HitmarkTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> damageFont = data.readNullableLargeSmart()
                2 -> damageColour = data.readUnsignedMedium()
                3 -> classGraphic = data.readNullableLargeSmart()
                4 -> leftGraphic = data.readNullableLargeSmart()
                5 -> middleGraphic = data.readNullableLargeSmart()
                6 -> rightGraphic = data.readNullableLargeSmart()
                7 -> scrollToOffsetX = data.readShort().toInt()
                8 -> damageFormat = data.readVersionedString()
                9 -> stickTime = data.readUnsignedShort()
                10 -> scrollToOffsetY = data.readShort().toInt()
                11 -> fadeout = 0
                12 -> replaceMode = data.readUnsignedByte().toInt()
                13 -> damageYOf = data.readShort().toInt()
                14 -> fadeout = data.readUnsignedShort()
                17,
                18 -> {
                    val multiVarBit = data.readUnsignedShortOrNull()
                    val multiVarp = data.readUnsignedShortOrNull()
                    var multiMarkDefault: Short? = null
                    if (code == 18) {
                        multiMarkDefault = data.readUnsignedShortOrNull()?.toShort()
                    }
                    val count = data.readUnsignedByte().toInt()
                    val multiMark = ShortArray(count + 2)
                    for (i in 0..count) {
                        multiMark[i] = data.readUnsignedShortOrNull()?.toShort() ?: -1
                    }
                    multiMark[count + 1] = multiMarkDefault ?: -1
                    this.multiMarkDefault = multiMarkDefault?.toInt()
                    this.multiVarBit = multiVarBit
                    this.multiVarp = multiVarp
                    this.multiMark = multiMark
                }
                else -> throw IOException("Error unrecognised .hitmark config code: $code")
            }
        }
}
