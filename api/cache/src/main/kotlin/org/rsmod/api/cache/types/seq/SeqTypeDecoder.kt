package org.rsmod.api.cache.types.seq

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.seq.PostanimMove
import org.rsmod.game.type.seq.PreanimMove
import org.rsmod.game.type.seq.SeqFrameSound
import org.rsmod.game.type.seq.SeqTypeBuilder
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.seq.UnpackedSeqType

public object SeqTypeDecoder {
    public fun decodeAll(cache: Cache): SeqTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedSeqType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.SEQ)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.SEQ, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return SeqTypeList(types)
    }

    public fun decode(data: ByteBuf): SeqTypeBuilder {
        val builder = SeqTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: SeqTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> {
                    val count = data.readUnsignedShort()
                    val delay = ShortArray(count)
                    val groups = ShortArray(count)
                    val files = ShortArray(count)
                    for (i in delay.indices) {
                        delay[i] = data.readUnsignedShort().toShort()
                    }
                    for (i in groups.indices) {
                        groups[i] = data.readUnsignedShort().toShort()
                    }
                    for (i in files.indices) {
                        files[i] = data.readUnsignedShort().toShort()
                    }
                    this.delay = delay
                    this.frameGroup = groups
                    this.frameIndex = files
                }
                2 -> replayOff = data.readUnsignedShort()
                3 -> {
                    val count = data.readUnsignedByte().toInt()
                    val walkMerge = IntArray(count + 1)
                    for (i in 0 until count) {
                        walkMerge[i] = data.readUnsignedByte().toInt()
                    }
                    walkMerge[count] = 9_999_999
                    this.walkMerge = walkMerge
                }
                4 -> stretches = true
                5 -> priority = data.readUnsignedByte().toInt()
                6 -> offhand = data.readUnsignedShort()
                7 -> mainhand = data.readUnsignedShort()
                8 -> replayCount = data.readUnsignedByte().toInt()
                9 -> {
                    val id = data.readUnsignedByte().toInt()
                    val preanimMove = PreanimMove.entries.firstOrNull { it.id == id }
                    this.preanimMove =
                        checkNotNull(preanimMove) {
                            "`id` $id is not associated with a PreanimMove type."
                        }
                }
                10 -> {
                    val id = data.readUnsignedByte().toInt()
                    val postanimMove = PostanimMove.entries.firstOrNull { it.id == id }
                    this.postanimMove =
                        checkNotNull(postanimMove) {
                            "`id` $id is not associated with a Postanimmove type."
                        }
                }
                11 -> replaceMode = data.readUnsignedByte().toInt()
                12 -> {
                    val count = data.readUnsignedByte().toInt()
                    val groups = ShortArray(count)
                    val files = ShortArray(count)
                    for (i in groups.indices) {
                        groups[i] = data.readUnsignedShort().toShort()
                    }
                    for (i in files.indices) {
                        files[i] = data.readUnsignedShort().toShort()
                    }
                    this.iframeGroup = groups
                    this.iframeIndex = files
                }
                13 -> keyframeSet = data.readInt()
                14 -> {
                    val count = data.readUnsignedShort()
                    val mayaAnimationSounds = HashMap<Int, SeqFrameSound>(count)
                    repeat(count) {
                        val index = data.readUnsignedShort()
                        val type = data.readUnsignedShort()
                        val weight = data.readUnsignedByte().toInt()
                        val loops = data.readUnsignedByte().toInt()
                        val range = data.readUnsignedByte().toInt()
                        val size = data.readUnsignedByte().toInt()
                        if (type >= 1 && loops >= 1) {
                            val sound =
                                SeqFrameSound(
                                    type = type,
                                    weight = weight,
                                    loops = loops,
                                    range = range,
                                    size = size,
                                )
                            mayaAnimationSounds[index] = sound
                        }
                    }
                    this.mayaAnimationSounds = mayaAnimationSounds
                }
                15 -> {
                    keyframeRangeStart = data.readUnsignedShort()
                    keyframeRangeEnd = data.readUnsignedShort()
                }
                17 -> {
                    val keyframeWalkMerge = BooleanArray(256)
                    val count = data.readUnsignedByte().toInt()
                    repeat(count) {
                        val id = data.readUnsignedByte().toInt()
                        keyframeWalkMerge[id] = true
                    }
                    this.keyframeWalkMerge = keyframeWalkMerge
                }
                18 -> debugName = data.readString()
                19 -> crossWorldSound = true
                else -> throw IOException("Error unrecognised .seq config code: $code")
            }
        }
}
