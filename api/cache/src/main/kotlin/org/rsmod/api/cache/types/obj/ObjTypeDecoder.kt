package org.rsmod.api.cache.types.obj

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.readRawParams
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.obj.ObjTypeBuilder
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.util.CompactableIntArray
import org.rsmod.game.type.util.ParamMap

public object ObjTypeDecoder {
    public fun decodeAll(cache: Cache): ObjTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedObjType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.OBJ)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.OBJ, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return ObjTypeList(types)
    }

    public fun decode(data: ByteBuf): ObjTypeBuilder {
        val builder = ObjTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: ObjTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> model = data.readUnsignedShort()
                2 -> name = data.readString()
                3 -> desc = data.readString()
                4 -> zoom2d = data.readUnsignedShort()
                5 -> xan2d = data.readUnsignedShort()
                6 -> yan2d = data.readUnsignedShort()
                7 -> xof2d = data.readUnsignedShort()
                8 -> yof2d = data.readUnsignedShort()
                11 -> stackable = true
                12 -> cost = data.readInt()
                13 -> wearpos1 = data.readByte().toInt()
                14 -> wearpos2 = data.readByte().toInt()
                16 -> members = true
                23 -> {
                    manwear = data.readUnsignedShort()
                    manwearOff = data.readUnsignedByte().toInt()
                }
                24 -> manwear2 = data.readUnsignedShort()
                25 -> {
                    womanwear = data.readUnsignedShort()
                    womanwearOff = data.readUnsignedByte().toInt()
                }
                26 -> womanwear2 = data.readUnsignedShort()
                27 -> wearpos3 = data.readByte().toInt()
                in 30 until 35 -> {
                    op[code - 30] = data.readString()
                }
                in 35 until 40 -> {
                    iop[code - 35] = data.readString()
                }
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
                        else -> throw NotImplementedError("Unhandled .obj config code.")
                    }
                }
                42 -> shiftclickiop = data.readByte().toInt()
                43 -> {
                    val op = data.readUnsignedByte().toInt() + 1
                    var subop = data.readUnsignedByte().toInt()
                    val isubop = Array<String>(20) { "" }
                    while (subop != 0) {
                        isubop[subop - 1] = data.readString()
                        subop = data.readUnsignedByte().toInt()
                    }
                    when (op) {
                        1 -> this.isubop1 = isubop
                        2 -> this.isubop2 = isubop
                        3 -> this.isubop3 = isubop
                        4 -> this.isubop4 = isubop
                        5 -> this.isubop5 = isubop
                        else -> error("Unhandled isubop parent op `$op`.")
                    }
                }
                65 -> stockmarket = true
                75 -> weight = data.readShort().toInt()
                78 -> manwear3 = data.readUnsignedShort()
                79 -> womanwear3 = data.readUnsignedShort()
                90 -> manhead = data.readUnsignedShort()
                91 -> womanhead = data.readUnsignedShort()
                92 -> manhead2 = data.readUnsignedShort()
                93 -> womanhead2 = data.readUnsignedShort()
                94 -> category = data.readUnsignedShort()
                95 -> zan2d = data.readUnsignedShort()
                97 -> certlink = data.readUnsignedShort()
                98 -> certtemplate = data.readUnsignedShort()
                in 100 until 110 -> {
                    countObj[code - 100] = data.readUnsignedShort()
                    countCount[code - 100] = data.readUnsignedShort()
                }
                110 -> resizeX = data.readUnsignedShort()
                111 -> resizeY = data.readUnsignedShort()
                112 -> resizeZ = data.readUnsignedShort()
                113 -> ambient = data.readByte().toInt()
                114 -> contrast = data.readByte().toInt()
                115 -> team = data.readByte().toInt()
                139 -> boughtlink = data.readUnsignedShort()
                140 -> boughttemplate = data.readUnsignedShort()
                148 -> placeholderlink = data.readUnsignedShort()
                149 -> placeholdertemplate = data.readUnsignedShort()
                200 -> {
                    val count = data.readUnsignedByte().toInt()
                    val objvar = IntArray(count)
                    for (i in objvar.indices) {
                        objvar[i] = data.readUnsignedShort()
                    }
                    this.objvar = CompactableIntArray(objvar)
                }
                201 -> playerCost = data.readInt()
                202 -> playerCostDerived = data.readInt()
                203 -> playerCostDerivedConst = data.readInt()
                204 -> stockMarketBuyLimit = data.readUnsignedShort()
                205 -> stockMarketRecalcUsers = data.readUnsignedShort()
                206 -> tradeable = false
                207 -> respawnRate = data.readUnsignedShort()
                208 -> dummyitem = data.readByte().toInt()
                209 -> contentGroup = data.readUnsignedShort()
                210 -> transformlink = data.readUnsignedShort()
                211 -> transformtemplate = data.readUnsignedShort()
                249 -> paramMap = ParamMap(data.readRawParams())
                else -> throw IOException("Error unrecognised .obj config code: $code")
            }
        }

    public fun assignInternal(list: ObjTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }
}
