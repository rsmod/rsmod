package org.rsmod.api.cache.types.loc

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
import org.rsmod.api.cache.util.readUnsignedShortOrNull
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.util.ParamMap
import org.rsmod.game.type.util.CompactableIntArray

public object LocTypeDecoder {
    public fun decodeAll(cache: Cache): LocTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedLocType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.LOC)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.LOC, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return LocTypeList(types)
    }

    public fun decode(data: ByteBuf): LocTypeBuilder {
        val builder = LocTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: LocTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> {
                    val count = data.readUnsignedByte().toInt()
                    val models = IntArray(count)
                    val shapes = IntArray(count)
                    repeat(count) {
                        models[it] = data.readUnsignedShort()
                        shapes[it] = data.readUnsignedByte().toInt()
                    }
                    this.model = CompactableIntArray(models)
                    this.modelShape = CompactableIntArray(shapes)
                }
                2 -> name = data.readString()
                3 -> desc = data.readString()
                5 -> {
                    val count = data.readUnsignedByte().toInt()
                    val models = IntArray(count)
                    repeat(count) { models[it] = data.readUnsignedShort() }
                    this.model = CompactableIntArray(models)
                }
                14 -> width = data.readUnsignedByte().toInt()
                15 -> length = data.readUnsignedByte().toInt()
                17 -> {
                    blockWalk = 0
                    blockRange = false
                }
                18 -> blockRange = false
                19 -> active = data.readUnsignedByte().toInt()
                21 -> hillSkew = 0
                22 -> shareLight = true
                23 -> occlude = true
                24 -> anim = data.readUnsignedShortOrNull()
                27 -> blockWalk = 1
                28 -> wallWidth = data.readUnsignedByte().toInt()
                29 -> ambient = data.readByte().toInt()
                in 30 until 35 -> {
                    op[code - 30] = data.readString()
                }
                39 -> contrast = data.readByte() * 25
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
                        else -> throw NotImplementedError("Unhandled .loc config code.")
                    }
                }
                61 -> category = data.readUnsignedShort()
                62 -> mirror = true
                64 -> shadow = false
                65 -> resizeX = data.readUnsignedShort()
                66 -> resizeY = data.readUnsignedShort()
                67 -> resizeZ = data.readUnsignedShort()
                68 -> mapscene = data.readUnsignedShort()
                69 -> forceApproachFlags = data.readUnsignedByte().toInt()
                70 -> offsetX = data.readShort().toInt()
                71 -> offsetY = data.readShort().toInt()
                72 -> offsetZ = data.readShort().toInt()
                73 -> forceDecor = true
                74 -> breakRouteFinding = true
                75 -> raiseObject = data.readUnsignedByte().toInt()
                77,
                92 -> {
                    val multiVarBit = data.readUnsignedShortOrNull()
                    val multiVarp = data.readUnsignedShortOrNull()
                    var defaultLoc: Int? = null
                    if (code == 92) {
                        defaultLoc = data.readUnsignedShortOrNull()
                    }
                    val count = data.readUnsignedByte().toInt()
                    val multiLoc = IntArray(count + 1) { 0 }
                    for (i in multiLoc.indices) {
                        multiLoc[i] = data.readUnsignedShortOrNull() ?: -1
                    }
                    this.multiVarp = multiVarp
                    this.multiVarBit = multiVarBit
                    this.multiLocDefault = multiLocDefault
                    this.multiLoc = CompactableIntArray(multiLoc)
                }
                78 -> {
                    bgsoundSound = data.readUnsignedShort()
                    bgsoundRange = data.readUnsignedByte().toInt()
                    bgsoundSize = data.readUnsignedByte().toInt()
                }
                79 -> {
                    bgsoundMinDelay = data.readUnsignedShort()
                    bgsoundMaxDelay = data.readUnsignedShort()
                    bgsoundRange = data.readUnsignedByte().toInt()
                    bgsoundSize = data.readUnsignedByte().toInt()
                    val count = data.readUnsignedByte().toInt()
                    val randomSounds = IntArray(count) { data.readUnsignedShort() }
                    this.bgsoundRandomSound = CompactableIntArray(randomSounds)
                }
                81 -> treeSkew = data.readUnsignedByte().toInt()
                82 -> mapIcon = data.readUnsignedShort()
                89 -> randomAnimFrame = false
                90 -> fixLocAnimAfterLocChange = true
                200 -> contentType = data.readUnsignedShort()
                249 -> paramMap = ParamMap(data.readRawParams())
                else -> throw IOException("Error unrecognised .loc config code: $code")
            }
        }

    public fun assignInternal(list: LocTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }
}
