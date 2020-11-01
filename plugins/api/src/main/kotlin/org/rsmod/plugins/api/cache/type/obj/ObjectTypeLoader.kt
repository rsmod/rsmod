package org.rsmod.plugins.api.cache.type.obj

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import org.rsmod.game.cache.GameCache
import org.rsmod.game.cache.ConfigTypeLoader
import org.rsmod.game.model.obj.ObjectType
import org.rsmod.game.model.obj.ObjectTypeList
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf

private val logger = InlineLogger()
private const val OBJ_ARCHIVE = 2
private const val OBJ_GROUP = 6

class ObjectTypeLoader @Inject constructor(
    private val cache: GameCache,
    private val types: ObjectTypeList
) : ConfigTypeLoader {

    override fun load() {
        val files = cache.readGroups(OBJ_ARCHIVE, OBJ_GROUP)
        files.forEach { (file, data) ->
            val type = data.type(file)
            types.add(type)
        }
        logger.info { "Loaded ${types.size} object type files" }
    }

    override fun save() {
        TODO()
    }

    private fun ByteBuf.type(id: Int): ObjectType {
        val builder = ObjectTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.build()
    }

    private fun ObjectTypeBuilder.readBuffer(instruction: Int, buf: ByteBuf) {
        when (instruction) {
            1 -> {
                val count = buf.readUnsignedByte().toInt()
                for (i in 0 until count) {
                    buf.readUnsignedShort() /* model id */
                    buf.readUnsignedByte() /* model type */
                }
            }
            2 -> name = buf.readStringCP1252()
            5 -> {
                val count = buf.readUnsignedByte().toInt()
                repeat(count) {
                    buf.readUnsignedShort()
                }
            }
            14 -> width = buf.readUnsignedByte().toInt()
            15 -> length = buf.readUnsignedByte().toInt()
            17 -> blockPath = false
            18 -> blockProjectile = false
            19 -> interact = buf.readBoolean()
            24 -> {
                animation = buf.readUnsignedShort()
                if (animation == 65535) animation = -1
            }
            28 -> buf.readUnsignedByte()
            29 -> buf.readByte()
            in 30 until 35 -> {
                if (defaultOptions) options = arrayOfNulls(5)
                val index = instruction - 30
                val option = buf.readStringCP1252()
                options[index] = if (option == "Hidden") null else option
            }
            39 -> buf.readByte()
            40 -> {
                val count = buf.readUnsignedByte().toInt()
                repeat(count) {
                    buf.readUnsignedShort() /* color original */
                    buf.readUnsignedShort() /* color replacement */
                }
            }
            41 -> {
                val count = buf.readUnsignedByte().toInt()
                repeat(count) {
                    buf.readUnsignedShort() /* texture original */
                    buf.readUnsignedShort() /* texture replacement */
                }
            }
            62 -> rotated = true
            64 -> {}
            65 -> buf.readUnsignedShort()
            66 -> buf.readUnsignedShort()
            67 -> buf.readUnsignedShort()
            68 -> buf.readUnsignedShort()
            69 -> clipMask = buf.readUnsignedByte().toInt()
            70 -> buf.readShort()
            71 -> buf.readShort()
            72 -> buf.readShort()
            73 -> obstruct = true
            75 -> buf.readUnsignedByte()
            77, 92 -> {
                varbit = buf.readUnsignedShort()
                if (varbit == 65535) varbit = -1

                varp = buf.readUnsignedShort()
                if (varp == 65535) varp = -1

                var defaultTransform = if (instruction == 92) buf.readUnsignedShort() else -1
                if (defaultTransform == 65535) defaultTransform = -1

                val count = buf.readUnsignedByte().toInt()
                transforms = Array(count + 2) { 0 }
                for (i in 0..count) {
                    val objectId = buf.readUnsignedShort()
                    transforms[i] = objectId
                }
                transforms[count + 1] = defaultTransform
            }
            78 -> {
                buf.readUnsignedShort()
                buf.readUnsignedByte()
            }
            79 -> {
                buf.readUnsignedShort()
                buf.readUnsignedShort()
                buf.readUnsignedByte()
                val count = buf.readUnsignedByte().toInt()
                repeat(count) {
                    buf.readUnsignedShort()
                }
            }
            81 -> buf.readUnsignedByte()
            82 -> buf.readUnsignedShort()
        }
    }
}
