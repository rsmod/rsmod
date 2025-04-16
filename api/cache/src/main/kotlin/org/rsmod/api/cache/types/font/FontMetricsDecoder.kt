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
        val groups = cache.list(Js5Archives.FONTMETRICS)
        for (group in groups) {
            val data = cache.read(Js5Archives.FONTMETRICS, group.id, file = 0)
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
            val glyphAdvances = IntArray(256)
            for (i in glyphAdvances.indices) {
                glyphAdvances[i] = data.readUnsignedByte().toInt()
            }
            this.glyphAdvances = glyphAdvances

            if (data.readableBytes() == Byte.SIZE_BYTES) {
                val ascent = data.readUnsignedByte().toInt()
                this.ascent = ascent
                return
            }

            val glyphHeights = IntArray(256)
            for (i in glyphHeights.indices) {
                glyphHeights[i] = data.readUnsignedByte().toInt()
            }

            val bearingY = IntArray(256)
            for (i in bearingY.indices) {
                bearingY[i] = data.readUnsignedByte().toInt()
            }

            val rightKern = Array(256) { ByteArray(glyphHeights[it]) }
            for (i in rightKern.indices) {
                var kern: Byte = 0
                for (j in rightKern[i].indices) {
                    kern = (kern + data.readByte()).toByte()
                    rightKern[i][j] = kern
                }
            }

            var tmpPos = 0
            val leftKern = Array(256) { ByteArray(glyphHeights[it]) }
            for (i in leftKern.indices) {
                var kern: Byte = 0
                for (j in leftKern[i].indices) {
                    kern = (kern + data.getByte(tmpPos++)).toByte()
                    leftKern[i][j] = kern
                }
            }

            val kerning = ByteArray(65536)
            for (leftGlyph in 0 until 256) {
                if (leftGlyph == 32 || leftGlyph == 160) {
                    continue
                }
                for (rightGlyph in 0 until 256) {
                    val computed =
                        computeKerning(
                            rightKern,
                            leftKern,
                            bearingY,
                            glyphAdvances,
                            glyphHeights,
                            leftGlyph,
                            rightGlyph,
                        )
                    kerning[(leftGlyph shl 8) or rightGlyph] = computed.toByte()
                }
            }
            this.kerning = kerning

            this.ascent = glyphHeights[32] + bearingY[32]
        }

    private fun computeKerning(
        rightKern: Array<ByteArray>,
        leftKern: Array<ByteArray>,
        bearingY: IntArray,
        width: IntArray,
        height: IntArray,
        leftGlyph: Int,
        rightGlyph: Int,
    ): Int {
        val minY1 = bearingY[leftGlyph]
        val maxY1 = minY1 + height[leftGlyph]
        val minY2 = bearingY[rightGlyph]
        val maxY2 = minY2 + height[rightGlyph]

        val minY = minY1.coerceAtLeast(minY2)
        val maxY = maxY1.coerceAtMost(maxY2)

        var kern = width[leftGlyph].coerceAtMost(width[rightGlyph])
        val leftGlyphKern = leftKern[leftGlyph]
        val rightGlyphKern = rightKern[rightGlyph]

        var y1 = minY - minY1
        var y2 = minY - minY2

        for (i in minY until maxY) {
            val total = leftGlyphKern[y1++].toInt() + rightGlyphKern[y2++].toInt()
            if (total < kern) {
                kern = total
            }
        }

        return -kern
    }
}
