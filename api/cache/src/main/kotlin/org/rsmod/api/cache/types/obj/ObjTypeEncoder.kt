package org.rsmod.api.cache.types.obj

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.filterTransmit
import org.rsmod.api.cache.util.writeRawParams
import org.rsmod.game.type.obj.ObjTypeBuilder
import org.rsmod.game.type.obj.UnpackedObjType

public object ObjTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedObjType>,
        ctx: EncoderContext,
    ): List<UnpackedObjType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.OBJ
        val packed = mutableListOf<UnpackedObjType>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf =
                buffer.clear().encodeConfig {
                    encodeJs5(type, this, ctx)
                    if (ctx.encodeFull) {
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

    public fun encodeJs5(type: UnpackedObjType, data: ByteBuf, ctx: EncoderContext): Unit =
        with(type) {
            if (model != 0) {
                data.writeByte(1)
                data.writeShort(model)
            }

            if (name.isNotBlank()) {
                data.writeByte(2)
                data.writeString(name)
            }

            if (desc.isNotBlank()) {
                data.writeByte(3)
                data.writeString(desc)
            }

            if (zoom2d != ObjTypeBuilder.DEFAULT_ZOOM2D) {
                data.writeByte(4)
                data.writeShort(zoom2d)
            }

            if (xan2d != 0) {
                data.writeByte(5)
                data.writeShort(xan2d)
            }

            if (yan2d != 0) {
                data.writeByte(6)
                data.writeShort(yan2d)
            }

            if (xof2d != 0) {
                data.writeByte(7)
                data.writeShort(xof2d)
            }

            if (yof2d != 0) {
                data.writeByte(8)
                data.writeShort(yof2d)
            }

            if (stackable) {
                data.writeByte(11)
            }

            if (cost != ObjTypeBuilder.DEFAULT_COST) {
                data.writeByte(12)
                data.writeInt(cost)
            }

            if (wearpos1 != ObjTypeBuilder.DEFAULT_WEARPOS) {
                data.writeByte(13)
                data.writeByte(wearpos1)
            }

            if (wearpos2 != ObjTypeBuilder.DEFAULT_WEARPOS2) {
                data.writeByte(14)
                data.writeByte(wearpos2)
            }

            if (members) {
                data.writeByte(16)
            }

            if (manwear != ObjTypeBuilder.DEFAULT_MANWEAR) {
                data.writeByte(23)
                data.writeShort(manwear)
                data.writeByte(manwearOff)
            }

            if (manwear2 != ObjTypeBuilder.DEFAULT_MANWEAR2) {
                data.writeByte(24)
                data.writeShort(manwear2)
            }

            if (womanwear != ObjTypeBuilder.DEFAULT_WOMANWEAR) {
                data.writeByte(25)
                data.writeShort(womanwear)
                data.writeByte(womanwearOff)
            }

            if (womanwear2 != ObjTypeBuilder.DEFAULT_WOMANWEAR2) {
                data.writeByte(26)
                data.writeShort(womanwear2)
            }

            if (wearpos3 != ObjTypeBuilder.DEFAULT_WEARPOS3) {
                data.writeByte(27)
                data.writeByte(wearpos3)
            }

            for (i in op.indices) {
                val op = op[i] ?: continue
                data.writeByte(30 + i)
                data.writeString(op)
            }

            for (i in iop.indices) {
                val op = iop[i] ?: continue
                data.writeByte(35 + i)
                data.writeString(op)
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

            if (shiftclickiop != ObjTypeBuilder.DEFAULT_SHIFTCLICKIOP) {
                data.writeByte(42)
                data.writeByte(shiftclickiop)
            }

            fun writeIsubop(op: Int, isubop: Array<String>) {
                require(isubop.any { it.isNotBlank() }) { "`isubop` must not be empty: $this" }
                data.writeByte(43)
                data.writeByte(op - 1)
                for (i in isubop.indices) {
                    val op = isubop[i]
                    if (op.isBlank()) {
                        continue
                    }
                    data.writeByte(i + 1)
                    data.writeString(op)
                }
                data.writeByte(0)
            }

            isubop1?.let { writeIsubop(1, it) }
            isubop2?.let { writeIsubop(2, it) }
            isubop3?.let { writeIsubop(3, it) }
            isubop4?.let { writeIsubop(4, it) }
            isubop5?.let { writeIsubop(5, it) }

            if (stockmarket) {
                data.writeByte(65)
            }

            if (weight != 0) {
                data.writeByte(75)
                data.writeShort(weight)
            }

            if (manwear3 != ObjTypeBuilder.DEFAULT_MANWEAR3) {
                data.writeByte(78)
                data.writeShort(manwear3)
            }

            if (womanwear3 != ObjTypeBuilder.DEFAULT_WOMANWEAR3) {
                data.writeByte(79)
                data.writeShort(womanwear3)
            }

            if (manhead != ObjTypeBuilder.DEFAULT_MANHEAD) {
                data.writeByte(90)
                data.writeShort(manhead)
            }

            if (womanhead != ObjTypeBuilder.DEFAULT_WOMANHEAD) {
                data.writeByte(91)
                data.writeShort(womanhead)
            }

            if (manhead2 != ObjTypeBuilder.DEFAULT_MANHEAD2) {
                data.writeByte(92)
                data.writeShort(manhead2)
            }

            if (womanhead2 != ObjTypeBuilder.DEFAULT_WOMANHEAD2) {
                data.writeByte(93)
                data.writeShort(womanhead2)
            }

            if (category != ObjTypeBuilder.DEFAULT_CATEGORY) {
                data.writeByte(94)
                data.writeShort(category)
            }

            if (zan2d != 0) {
                data.writeByte(95)
                data.writeShort(zan2d)
            }

            if (certlink != 0) {
                data.writeByte(97)
                data.writeShort(certlink)
            }

            if (certtemplate != 0) {
                data.writeByte(98)
                data.writeShort(certtemplate)
            }

            for (i in countObj.indices) {
                val obj = countObj[i]
                if (obj == 0) {
                    continue
                }
                val count = countCount[i]
                data.writeByte(100 + i)
                data.writeShort(obj)
                data.writeShort(count)
            }

            if (resizeX != ObjTypeBuilder.DEFAULT_RESIZE_X) {
                data.writeByte(110)
                data.writeShort(resizeX)
            }

            if (resizeY != ObjTypeBuilder.DEFAULT_RESIZE_Y) {
                data.writeByte(111)
                data.writeShort(resizeY)
            }

            if (resizeZ != ObjTypeBuilder.DEFAULT_RESIZE_Z) {
                data.writeByte(112)
                data.writeShort(resizeZ)
            }

            if (ambient != 0) {
                data.writeByte(113)
                data.writeByte(ambient)
            }

            if (contrast != 0) {
                data.writeByte(114)
                data.writeByte(contrast)
            }

            if (team != 0) {
                data.writeByte(115)
                data.writeByte(team)
            }

            if (boughtlink != 0) {
                data.writeByte(139)
                data.writeShort(boughtlink)
            }

            if (boughttemplate != 0) {
                data.writeByte(140)
                data.writeShort(boughttemplate)
            }

            if (placeholderlink != 0) {
                data.writeByte(148)
                data.writeShort(placeholderlink)
            }

            if (placeholdertemplate != 0) {
                data.writeByte(149)
                data.writeShort(placeholdertemplate)
            }

            val params = paramMap?.filterTransmit(ctx)?.primitiveMap
            if (params?.isNotEmpty() == true) {
                data.writeByte(249)
                data.writeRawParams(params)
            }
        }

    public fun encodeGame(type: UnpackedObjType, data: ByteBuf): Unit =
        with(type) {
            if (objvar.isNotEmpty()) {
                data.writeByte(200)
                data.writeByte(objvar.size)
                for (objvar in objvar) {
                    check(objvar and 0xFFFF < 65535)
                    data.writeShort(objvar)
                }
            }

            if (playerCost != 0) {
                data.writeByte(201)
                data.writeInt(playerCost)
            }

            if (playerCostDerived != 0) {
                data.writeByte(202)
                data.writeInt(playerCostDerived)
            }

            if (playerCostDerivedConst != 0) {
                data.writeByte(203)
                data.writeInt(playerCostDerivedConst)
            }

            if (stockMarketBuyLimit != 0) {
                data.writeByte(204)
                data.writeShort(stockMarketBuyLimit)
            }

            if (stockMarketRecalcUsers != 0) {
                data.writeByte(205)
                data.writeShort(stockMarketRecalcUsers)
            }

            if (!tradeable) {
                data.writeByte(206)
            }

            if (respawnRate != ObjTypeBuilder.DEFAULT_RESPAWN_RATE) {
                data.writeByte(207)
                data.writeShort(respawnRate)
            }

            if (dummyitem != ObjTypeBuilder.DEFAULT_DUMMYITEM) {
                data.writeByte(208)
                data.writeByte(dummyitem)
            }

            if (contentGroup != ObjTypeBuilder.DEFAULT_CONTENT_GROUP) {
                data.writeByte(209)
                data.writeShort(contentGroup)
            }

            if (transformlink != 0) {
                data.writeByte(210)
                data.writeShort(transformlink)
            }

            if (transformtemplate != 0) {
                data.writeByte(211)
                data.writeShort(transformtemplate)
            }

            if (weaponCategory != ObjTypeBuilder.DEFAULT_WEAPON_CATEGORY) {
                data.writeByte(212)
                data.writeByte(weaponCategory)
            }
        }
}
