package org.rsmod.api.cache.types.font

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.font.FontMetricsTypeBuilder
import org.rsmod.game.type.font.FontMetricsTypeList
import org.rsmod.game.type.font.UnpackedFontMetricsType

public object FontMetricsDecoder {
    public fun decodeAll(cache: Cache): FontMetricsTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedFontMetricsType>()
        val groups = cache.list(Js5Archives.FONT_METRICS)
        for (group in groups) {
            val data = cache.read(Js5Archives.FONT_METRICS, group.id, file = 0)
            val type = data.use { decode(it).build(group.id) }
            types[group.id] = type.apply { TypeResolver[this] = group.id }
        }
        return FontMetricsTypeList(types)
    }

    public fun decode(data: ByteBuf): FontMetricsTypeBuilder {
        val builder = FontMetricsTypeBuilder(TextUtil.NULL)
        decode(builder, data)
        return builder
    }

    public fun decode(builder: FontMetricsTypeBuilder, data: ByteBuf): Unit =
        with(builder) {
            val charWidths = IntArray(256)
            for (i in charWidths.indices) {
                charWidths[i] = data.readUnsignedByte().toInt()
            }
            this.charWidths = charWidths

            if (data.readableBytes() == Byte.SIZE_BYTES) {
                val ascent = data.readUnsignedByte().toInt()
                this.ascent = ascent
                return
            }

            val startWidths = IntArray(256)
            for (i in startWidths.indices) {
                startWidths[i] = data.readUnsignedByte().toInt()
            }

            val endWidths = IntArray(256)
            for (i in endWidths.indices) {
                endWidths[i] = data.readUnsignedByte().toInt()
            }

            val startByteArrays = Array(256) { ByteArray(startWidths[it]) }
            for (i in startByteArrays.indices) {
                var tmp: Byte = 0
                for (j in startByteArrays[i].indices) {
                    tmp = (tmp + data.readByte()).toByte()
                    startByteArrays[i][j] = tmp
                }
            }

            var tmpPos = 0
            val endByteArrays = Array(256) { ByteArray(startWidths[it]) }
            for (i in endByteArrays.indices) {
                var tmp: Byte = 0
                for (j in endByteArrays[i].indices) {
                    tmp = (tmp + data.getByte(tmpPos++)).toByte()
                    endByteArrays[i][j] = tmp
                }
            }

            val kerning = ByteArray(65536)
            for (prevChar in 0 until 256) {
                if (prevChar == 32 || prevChar == 160) {
                    continue
                }
                for (currChar in 0 until 256) {
                    val calculated =
                        calculateKerning(
                            startByteArrays,
                            endByteArrays,
                            endWidths,
                            charWidths,
                            startWidths,
                            prevChar,
                            currChar,
                        )
                    kerning[(prevChar shl 8) or currChar] = calculated.toByte()
                }
            }
            this.kerning = kerning

            this.ascent = startWidths[32] + endWidths[32]
        }

    private fun calculateKerning(
        startArray: Array<ByteArray>,
        endArray: Array<ByteArray>,
        endWidths: IntArray,
        charWidths: IntArray,
        startWidths: IntArray,
        prevChar: Int,
        currChar: Int,
    ): Int {
        val startOffset = endWidths[prevChar]
        val startLimit = startOffset + startWidths[prevChar]
        val currOffset = endWidths[currChar]
        val currLimit = currOffset + startWidths[currChar]

        val overlapStart = startOffset.coerceAtLeast(currOffset)
        val overlapEnd = startLimit.coerceAtMost(currLimit)

        var minOverlap = charWidths[prevChar].coerceAtMost(charWidths[currChar])
        val prevCharData = endArray[prevChar]
        val currCharData = startArray[currChar]

        var startIndex = overlapStart - startOffset
        var currIndex = overlapStart - currOffset

        for (i in overlapStart until overlapEnd) {
            val overlap = prevCharData[startIndex++].toInt() + currCharData[currIndex++].toInt()
            if (overlap < minOverlap) {
                minOverlap = overlap
            }
        }

        return -minOverlap
    }

    public fun assignInternal(list: FontMetricsTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }
}
