package org.rsmod.api.cache.types.hitmark

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.buffer.writeVersionedString
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.writeNullableLargeSmart
import org.rsmod.api.cache.util.writeNullableShort
import org.rsmod.game.type.hitmark.HitmarkTypeBuilder
import org.rsmod.game.type.hitmark.UnpackedHitmarkType

public object HitmarkTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedHitmarkType>,
    ): List<UnpackedHitmarkType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.HITMARK
        val packed = mutableListOf<UnpackedHitmarkType>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf = buffer.clear().encodeConfig { encodeJs5(type, this) }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
                packed += type
            }
            oldBuf?.release()
        }
        buffer.release()
        return packed
    }

    public fun encodeJs5(type: UnpackedHitmarkType, data: ByteBuf): Unit =
        with(type) {
            if (damageFont != HitmarkTypeBuilder.DEFAULT_DAMAGE_FONT) {
                data.writeByte(1)
                data.writeNullableLargeSmart(damageFont)
            }

            if (damageColour != HitmarkTypeBuilder.DEFAULT_DAMAGE_COLOUR) {
                data.writeByte(2)
                data.writeMedium(damageColour)
            }

            if (classGraphic != HitmarkTypeBuilder.DEFAULT_CLASS_GRAPHIC) {
                data.writeByte(3)
                data.writeNullableLargeSmart(classGraphic)
            }

            if (leftGraphic != HitmarkTypeBuilder.DEFAULT_LEFT_GRAPHIC) {
                data.writeByte(4)
                data.writeNullableLargeSmart(leftGraphic)
            }

            if (middleGraphic != HitmarkTypeBuilder.DEFAULT_MIDDLE_GRAPHIC) {
                data.writeByte(5)
                data.writeNullableLargeSmart(middleGraphic)
            }

            if (rightGraphic != HitmarkTypeBuilder.DEFAULT_RIGHT_GRAPHIC) {
                data.writeByte(6)
                data.writeNullableLargeSmart(rightGraphic)
            }

            if (scrollToOffsetX != 0) {
                data.writeByte(7)
                data.writeShort(scrollToOffsetX)
            }

            if (damageFormat != HitmarkTypeBuilder.DEFAULT_DAMAGE_FORMAT) {
                data.writeByte(8)
                data.writeVersionedString(damageFormat)
            }

            if (stickTime != HitmarkTypeBuilder.DEFAULT_STICK_TIME) {
                data.writeByte(9)
                data.writeShort(stickTime)
            }

            if (scrollToOffsetY != 0) {
                data.writeByte(10)
                data.writeShort(scrollToOffsetY)
            }

            if (fadeout == 0) {
                data.writeByte(11)
            }

            if (replaceMode != HitmarkTypeBuilder.DEFAULT_REPLACE_MODE) {
                data.writeByte(12)
                data.writeByte(replaceMode)
            }

            if (damageYOf != 0) {
                data.writeByte(13)
                data.writeShort(damageYOf)
            }

            if (fadeout != 0 && fadeout != HitmarkTypeBuilder.DEFAULT_FADEOUT) {
                data.writeByte(14)
                data.writeShort(fadeout)
            }

            val multiMark = this.multiMark
            if (multiMark != null) {
                if (multiMarkDefault != null) {
                    data.writeByte(18)
                } else {
                    data.writeByte(17)
                }
                data.writeNullableShort(multiVarBit)
                data.writeNullableShort(multiVarp)
                if (multiMarkDefault != null) {
                    data.writeNullableShort(multiMarkDefault)
                }
                data.writeByte(multiMark.size - 2)
                for (i in 0 until multiMark.size - 1) {
                    data.writeNullableShort(multiMark[i].toInt())
                }
            }
        }
}
