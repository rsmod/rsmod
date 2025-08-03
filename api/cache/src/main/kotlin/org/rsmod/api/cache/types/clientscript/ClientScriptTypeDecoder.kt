package org.rsmod.api.cache.types.clientscript

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.clientscript.ClientScriptTypeBuilder
import org.rsmod.game.type.clientscript.ClientScriptTypeList
import org.rsmod.game.type.clientscript.UnpackedClientScriptType

public object ClientScriptTypeDecoder {
    private const val OPCODE_PUSH_CONSTANT_STRING = 3
    private const val OPCODE_RETURN = 21
    private const val OPCODE_POP_INT_DISCARD = 38
    private const val OPCODE_POP_STRING_DISCARD = 39
    private const val OPCODE_PUSH_CONSTANT_NULL = 63

    private const val CORE_OPCODE_LIMIT = 100

    public fun decodeAll(cache: Cache): ClientScriptTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedClientScriptType>()
        val groups = cache.list(Js5Archives.CLIENTSCRIPTS)
        for (group in groups) {
            val data = cache.read(Js5Archives.CLIENTSCRIPTS, group.id, file = 0)
            val type = data.use { decode(it).build(group.id) }
            types[group.id] = type.apply { TypeResolver[this] = group.id }
        }
        return ClientScriptTypeList(types)
    }

    public fun decode(data: ByteBuf): ClientScriptTypeBuilder {
        val builder = ClientScriptTypeBuilder(TextUtil.NULL)
        decode(builder, data)
        return builder
    }

    public fun decode(builder: ClientScriptTypeBuilder, data: ByteBuf): Unit =
        with(builder) {
            val switchDataLength = data.getUnsignedShort(data.writerIndex() - Short.SIZE_BYTES)
            val commandEndPosition = data.writerIndex() - Short.SIZE_BYTES - switchDataLength - 12

            // As of 231, there is at least one script with only 2 readable bytes (script id: 6775).
            val emptyScript = data.readableBytes() <= Short.SIZE_BYTES
            if (!emptyScript) {
                data.readerIndex(commandEndPosition)
            }

            val commandCount = if (emptyScript) 0 else data.readInt()
            val commands = IntArray(commandCount)
            val intOperands = IntArray(commandCount)
            val stringOperands = arrayOfNulls<String>(commandCount)

            val intLocalCount: Int
            val stringLocalCount: Int
            val intArgumentCount: Int
            val stringArgumentCount: Int
            val switchCount: Int

            if (!emptyScript) {
                intLocalCount = data.readUnsignedShort()
                stringLocalCount = data.readUnsignedShort()
                intArgumentCount = data.readUnsignedShort()
                stringArgumentCount = data.readUnsignedShort()
                switchCount = data.readUnsignedByte().toInt()
            } else {
                intLocalCount = 0
                stringLocalCount = 0
                intArgumentCount = 0
                stringArgumentCount = 0
                switchCount = 0
            }

            val switches =
                Array<Map<Int, Int>>(switchCount) {
                    val entryCount = data.readUnsignedShort()
                    val map = Int2IntOpenHashMap()
                    repeat(entryCount) {
                        val key = data.readInt()
                        val value = data.readInt()
                        map[key] = value
                    }
                    map
                }

            if (!emptyScript) {
                data.readerIndex(0)
                data.readString()

                var index = 0
                while (data.readerIndex() < commandEndPosition) {
                    val command = data.readUnsignedShort()
                    when (command) {
                        OPCODE_PUSH_CONSTANT_STRING -> {
                            stringOperands[index] = data.readString()
                        }
                        OPCODE_RETURN,
                        OPCODE_POP_INT_DISCARD,
                        OPCODE_POP_STRING_DISCARD,
                        OPCODE_PUSH_CONSTANT_NULL -> {
                            intOperands[index] = data.readUnsignedByte().toInt()
                        }
                        else -> {
                            if (command < CORE_OPCODE_LIMIT) {
                                intOperands[index] = data.readInt()
                            } else {
                                intOperands[index] = data.readUnsignedByte().toInt()
                            }
                        }
                    }
                    commands[index] = command
                    index++
                }
            }

            this.intOperands = intOperands
            this.stringOperands = stringOperands
            this.intLocalCount = intLocalCount
            this.stringLocalCount = stringLocalCount
            this.intArgumentCount = intArgumentCount
            this.stringArgumentCount = stringArgumentCount
            this.commands = commands
            this.switches = switches
        }
}
