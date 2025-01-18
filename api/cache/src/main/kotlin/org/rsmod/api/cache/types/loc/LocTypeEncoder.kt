package org.rsmod.api.cache.types.loc

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.writeRawParams
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.UnpackedLocType

public object LocTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedLocType>,
        serverCache: Boolean,
    ): List<UnpackedLocType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.LOC
        val packed = mutableListOf<UnpackedLocType>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf =
                buffer.clear().encodeConfig {
                    encodeJs5(type, this)
                    if (serverCache) {
                        encodeGame(type, this)
                    }
                }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
                packed += type
            }
            oldBuf?.release()
        }
        buffer.release()
        return packed
    }

    public fun encodeFull(type: UnpackedLocType, data: ByteBuf): ByteBuf =
        data.encodeConfig {
            encodeJs5(type, this)
            encodeGame(type, this)
        }

    public fun encodeJs5(type: UnpackedLocType, data: ByteBuf): Unit =
        with(type) {
            if (models.isNotEmpty() && shapes.isNotEmpty()) {
                data.writeByte(1)
                data.writeByte(models.size)
                for (i in models.indices) {
                    data.writeShort(models[i])
                    data.writeByte(shapes[i].toInt())
                }
            }

            if (name.isNotEmpty()) {
                data.writeByte(2)
                data.writeString(name)
            }

            if (models.isNotEmpty() && shapes.isEmpty()) {
                data.writeByte(5)
                data.writeByte(models.size)
                for (model in models) {
                    data.writeShort(model)
                }
            }

            if (width != LocTypeBuilder.DEFAULT_WIDTH) {
                data.writeByte(14)
                data.writeByte(width)
            }

            if (length != LocTypeBuilder.DEFAULT_LENGTH) {
                data.writeByte(15)
                data.writeByte(length)
            }

            if (blockWalk == 0 && !blockRange) {
                data.writeByte(17)
            } else if (!blockRange) {
                data.writeByte(18)
            }

            if (active != LocTypeBuilder.DEFAULT_ACTIVE) {
                data.writeByte(19)
                data.writeByte(active)
            }

            if (hillSkew == 0) {
                data.writeByte(21)
            }

            if (shareLight) {
                data.writeByte(22)
            }

            if (occlude) {
                data.writeByte(23)
            }

            if (anim != LocTypeBuilder.DEFAULT_ANIM) {
                data.writeByte(24)
                data.writeShort(anim)
            }

            if (blockWalk == 1) {
                data.writeByte(27)
            }

            if (wallWidth != LocTypeBuilder.DEFAULT_WALL_WIDTH) {
                data.writeByte(28)
                data.writeByte(wallWidth)
            }

            if (ambient != 0) {
                data.writeByte(29)
                data.writeByte(ambient)
            }

            for (i in op.indices) {
                val op = op[i] ?: continue
                data.writeByte(30 + i)
                data.writeString(op)
            }

            if (contrast != 0) {
                data.writeByte(39)
                data.writeByte(contrast / 25)
            }

            if (recolS.isNotEmpty()) {
                check(recolS.size == recolD.size)
                data.writeByte(40)
                data.writeByte(recolS.size)
                for (i in recolS.indices) {
                    data.writeShort(recolS[i].toInt())
                    data.writeShort(recolD[i].toInt())
                }
            }

            if (retexS.isNotEmpty()) {
                check(retexS.size == retexD.size)
                data.writeByte(41)
                data.writeByte(retexS.size)
                for (i in retexS.indices) {
                    data.writeShort(retexS[i].toInt())
                    data.writeShort(retexD[i].toInt())
                }
            }

            if (category != LocTypeBuilder.DEFAULT_CATEGORY) {
                data.writeByte(61)
                data.writeShort(category)
            }

            if (mirror) {
                data.writeByte(62)
            }

            if (!shadow) {
                data.writeByte(64)
            }

            if (resizeX != LocTypeBuilder.DEFAULT_RESIZE_X) {
                data.writeByte(65)
                data.writeShort(resizeX)
            }

            if (resizeY != LocTypeBuilder.DEFAULT_RESIZE_Y) {
                data.writeByte(66)
                data.writeShort(resizeY)
            }

            if (resizeZ != LocTypeBuilder.DEFAULT_RESIZE_Z) {
                data.writeByte(67)
                data.writeShort(resizeZ)
            }

            if (mapscene != LocTypeBuilder.DEFAULT_MAP_SCENE) {
                data.writeByte(68)
                data.writeShort(mapscene)
            }

            if (forceApproachFlags != 0) {
                data.writeByte(69)
                data.writeByte(forceApproachFlags)
            }

            if (offsetX != 0) {
                data.writeByte(70)
                data.writeShort(offsetX)
            }

            if (offsetY != 0) {
                data.writeByte(71)
                data.writeShort(offsetY)
            }

            if (offsetZ != 0) {
                data.writeByte(72)
                data.writeShort(offsetZ)
            }

            if (forceDecor) {
                data.writeByte(73)
            }

            if (breakRouteFinding) {
                data.writeByte(74)
            }

            if (raiseObject != LocTypeBuilder.DEFAULT_RAISE_OBJECT) {
                data.writeByte(75)
                data.writeByte(raiseObject)
            }

            if (multiLoc.isNotEmpty()) {
                val hasDefault = multiLocDefault != LocTypeBuilder.DEFAULT_MULTI_LOC_DEFAULT
                if (hasDefault) {
                    data.writeByte(92)
                } else {
                    data.writeByte(77)
                }
                data.writeShort(multiVarBit)
                data.writeShort(multiVarp)
                if (hasDefault) {
                    data.writeShort(multiLocDefault)
                }
                data.writeByte(multiLoc.size - 1)
                for (i in multiLoc.indices) {
                    data.writeShort(multiLoc[i].toInt())
                }
            }

            if (bgsoundSound != LocTypeBuilder.DEFAULT_BGSOUND_SOUND) {
                data.writeByte(78)
                data.writeShort(bgsoundSound)
                data.writeByte(bgsoundRange)
                data.writeByte(bgsoundSize)
            }

            if (bgsoundRandomSounds.isNotEmpty()) {
                data.writeByte(79)
                data.writeShort(bgsoundMinDelay)
                data.writeShort(bgsoundMaxDelay)
                data.writeByte(bgsoundRange)
                data.writeByte(bgsoundSize)
                data.writeByte(bgsoundRandomSounds.size)
                for (sound in bgsoundRandomSounds) {
                    data.writeShort(sound.toInt())
                }
            }

            if (treeSkew != 0) {
                data.writeByte(81)
                data.writeByte(treeSkew)
            }

            if (mapIcon != LocTypeBuilder.DEFAULT_MAP_ICON) {
                data.writeByte(82)
                data.writeShort(mapIcon)
            }

            if (!randomAnimFrame) {
                data.writeByte(89)
            }

            if (fixLocAnimAfterLocChange) {
                data.writeByte(90)
            }

            val params = paramMap?.primitiveMap
            if (params?.isNotEmpty() == true) {
                data.writeByte(249)
                data.writeRawParams(params)
            }
        }

    public fun encodeGame(type: UnpackedLocType, data: ByteBuf): Unit =
        with(type) {
            if (desc.isNotBlank()) {
                data.writeByte(3)
                data.writeString(desc)
            }

            if (contentGroup != LocTypeBuilder.DEFAULT_CONTENT_GROUP) {
                data.writeByte(200)
                data.writeShort(contentGroup)
            }
        }
}
