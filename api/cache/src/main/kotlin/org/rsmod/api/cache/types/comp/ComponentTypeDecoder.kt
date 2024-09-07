package org.rsmod.api.cache.types.comp

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.readUnsignedShortOrNull
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.comp.ComponentTypeBuilder
import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.comp.UnpackedComponentType

public object ComponentTypeDecoder {
    public fun decodeAll(cache: Cache): ComponentTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedComponentType>()
        val groups = cache.list(Js5Archives.INTERFACES)
        for (group in groups) {
            val files = cache.list(Js5Archives.INTERFACES, group.id)
            for (file in files) {
                val combinedId = (group.id shl 16) or file.id
                val data = cache.read(Js5Archives.INTERFACES, group.id, file.id)
                val type = data.use { decode(combinedId, it).build(combinedId) }
                types[combinedId] = type.apply { TypeResolver[this] = combinedId }
            }
        }
        return ComponentTypeList(types)
    }

    public fun decode(combinedId: Int, data: ByteBuf): ComponentTypeBuilder {
        val builder = ComponentTypeBuilder(TextUtil.NULL)
        val version = data.getByte(data.readerIndex()).toInt()
        if (version == -1) {
            decodeV3(combinedId, builder, data)
        } else {
            decodeV1(combinedId, builder, data)
        }
        return builder
    }

    public fun decodeV1(combinedId: Int, builder: ComponentTypeBuilder, data: ByteBuf): Unit =
        with(builder) {
            v3 = false
            type = data.readUnsignedByte().toInt()
            buttonType = data.readUnsignedByte().toInt()
            contentType = data.readUnsignedShort()
            x = data.readShort().toInt()
            y = data.readShort().toInt()
            width = data.readUnsignedShort()
            height = data.readUnsignedShort()
            trans1 = data.readUnsignedByte().toInt()
            var layer = data.readUnsignedShortOrNull()
            if (layer != null) {
                layer += combinedId and -65536
            }
            this.layer = layer

            mouseOverRedirect = data.readUnsignedShortOrNull()

            val cs1ComparisonCount = data.readUnsignedByte().toInt()
            if (cs1ComparisonCount > 0) {
                val comparisons = ShortArray(cs1ComparisonCount)
                val values = IntArray(cs1ComparisonCount)
                for (i in 0 until cs1ComparisonCount) {
                    comparisons[i] = data.readUnsignedByte()
                    values[i] = data.readUnsignedShort()
                }
                this.cs1Comparisons = comparisons
                this.cs1ComparisonValues = values
            }

            val cs1InstructionCount = data.readUnsignedByte().toInt()
            if (cs1InstructionCount > 0) {
                val instructions = Array(cs1InstructionCount) { intArrayOf() }
                for (i in 0 until cs1InstructionCount) {
                    val innerCount = data.readUnsignedShort()
                    val innerInstructions = IntArray(innerCount)
                    for (j in 0 until innerCount) {
                        innerInstructions[j] = data.readUnsignedShortOrNull() ?: -1
                    }
                    instructions[i] = innerInstructions
                }
                cs1Instructions = instructions
            }

            if (type == 0) {
                scrollHeight = data.readUnsignedShort()
                hide = data.readBoolean()
            }

            if (type == 1) {
                data.readUnsignedShort()
                data.readUnsignedByte()
            }

            if (type == 3) {
                fill = data.readBoolean()
            }

            if (type == 4 || type == 1) {
                textAlignH = data.readUnsignedByte().toInt()
                textAlignV = data.readUnsignedByte().toInt()
                textLineHeight = data.readUnsignedByte().toInt()
                textFont = data.readUnsignedShortOrNull()
                textShadow = data.readBoolean()
            }

            if (type == 4) {
                text = data.readString()
                secondaryText = data.readString()
            }

            if (type == 1 || type == 3 || type == 4) {
                colour1 = data.readInt()
            }

            if (type == 3 || type == 4) {
                colour2 = data.readInt()
                mouseOverColour1 = data.readInt()
                mouseOverColour2 = data.readInt()
            }

            if (type == 5) {
                graphic = data.readInt()
                secondaryGraphic = data.readInt()
            }

            if (type == 6) {
                modelKind = 1
                model = data.readUnsignedShortOrNull()

                secondaryModelKind = 1
                secondaryModel = data.readUnsignedShortOrNull()

                modelAnim = data.readUnsignedShortOrNull()
                secondaryModelAnim = data.readUnsignedShortOrNull()

                modelZoom = data.readUnsignedShort()
                modelAngleX = data.readUnsignedShort()
                modelAngleY = data.readUnsignedShort()
            }

            if (type == 8) {
                text = data.readString()
            }

            if (buttonType == 2) {
                targetVerb = data.readString()
                targetBase = data.readString()
                val events = data.readUnsignedShort() and 63
                this.events = events or (events shl 11)
            }

            if (buttonType == 1 || buttonType == 4 || buttonType == 5 || buttonType == 6) {
                buttonText = data.readString()
                if (buttonText.isNullOrEmpty()) {
                    if (buttonType == 1) {
                        buttonText = TextUtil.OK
                    }

                    if (buttonType == 4) {
                        buttonText = TextUtil.SELECT
                    }

                    if (buttonType == 5) {
                        buttonText = TextUtil.SELECT
                    }

                    if (buttonType == 6) {
                        buttonText = TextUtil.CONTINUE
                    }
                }
            }

            if (buttonType == 1 || buttonType == 4 || buttonType == 5) {
                val events = events ?: 0
                this.events = events or 4194304
            }

            if (buttonType == 6) {
                val events = events ?: 0
                this.events = events or 1
            }
        }

    public fun decodeV3(combinedId: Int, builder: ComponentTypeBuilder, data: ByteBuf): Unit =
        with(builder) {
            data.readByte()
            v3 = true
            type = data.readUnsignedByte().toInt()
            contentType = data.readUnsignedShort()
            x = data.readShort().toInt()
            y = data.readShort().toInt()
            width = data.readUnsignedShort()
            height =
                if (type == 9) {
                    data.readShort().toInt()
                } else {
                    data.readUnsignedShort()
                }

            widthMode = data.readByte().toInt()
            heightMode = data.readByte().toInt()
            xMode = data.readByte().toInt()
            yMode = data.readByte().toInt()
            var layer = data.readUnsignedShortOrNull()
            if (layer != null) {
                layer += combinedId and -65536
            }
            this.layer = layer

            hide = data.readBoolean()

            if (type == 0) {
                scrollWidth = data.readUnsignedShort()
                scrollHeight = data.readUnsignedShort()
                noClickThrough = data.readBoolean()
            }

            if (type == 5) {
                graphic = data.readInt()
                angle2d = data.readUnsignedShort()
                tiling = data.readBoolean()
                trans1 = data.readUnsignedByte().toInt()
                outline = data.readUnsignedByte().toInt()
                graphicShadow = data.readInt()
                vFlip = data.readBoolean()
                hFlip = data.readBoolean()
            }

            if (type == 6) {
                modelKind = 1
                model = data.readUnsignedShortOrNull()

                modelX = data.readShort().toInt()
                modelY = data.readShort().toInt()
                modelAngleX = data.readUnsignedShort()
                modelAngleY = data.readUnsignedShort()
                modelAngleZ = data.readUnsignedShort()
                modelZoom = data.readUnsignedShort()
                modelAnim = data.readUnsignedShortOrNull()

                modelOrthog = data.readBoolean()
                data.readUnsignedShort()

                if (widthMode != 0) {
                    modelObjWidth = data.readUnsignedShort()
                }

                if (heightMode != 0) {
                    data.readUnsignedShort()
                }
            }

            if (type == 4) {
                textFont = data.readUnsignedShortOrNull()
                text = data.readString()
                textLineHeight = data.readUnsignedByte().toInt()
                textAlignH = data.readUnsignedByte().toInt()
                textAlignV = data.readUnsignedByte().toInt()
                textShadow = data.readBoolean()
                colour1 = data.readInt()
            }

            if (type == 3) {
                colour1 = data.readInt()
                fill = data.readBoolean()
                trans1 = data.readUnsignedByte().toInt()
            }

            if (type == 9) {
                lineWid = data.readUnsignedByte().toInt()
                colour1 = data.readInt()
                lineDirection = data.readBoolean()
            }

            events = data.readUnsignedMedium()
            opBase = data.readString()

            val opCount = data.readUnsignedByte().toInt()
            if (opCount > 0) {
                val op = Array(opCount) { "" }
                for (i in 0 until opCount) {
                    op[i] = data.readString()
                }
                this.op = op
            }

            dragDeadZone = data.readUnsignedByte().toInt()
            dragDeadTime = data.readUnsignedByte().toInt()
            draggableBehavior = data.readBoolean()
            targetVerb = data.readString()
            onLoad = decodeHook(data)
            onMouseOver = decodeHook(data)
            onMouseLeave = decodeHook(data)
            onTargetLeave = decodeHook(data)
            onTargetEnter = decodeHook(data)
            onVarTransmit = decodeHook(data)
            onInvTransmit = decodeHook(data)
            onStatTransmit = decodeHook(data)
            onTimer = decodeHook(data)
            onOp = decodeHook(data)
            onMouseRepeat = decodeHook(data)
            onClick = decodeHook(data)
            onClickRepeat = decodeHook(data)
            onRelease = decodeHook(data)
            onHold = decodeHook(data)
            onDrag = decodeHook(data)
            onDragComplete = decodeHook(data)
            onScrollWheel = decodeHook(data)
            onVarTransmitList = decodeHookTransmitList(data)
            onInvTransmitList = decodeHookTransmitList(data)
            onStatTransmitList = decodeHookTransmitList(data)
        }

    public fun decodeHook(data: ByteBuf): Array<Any>? {
        val count = data.readUnsignedByte().toInt()
        if (count == 0) {
            return null
        }
        val values = Array<Any>(count) {}
        for (i in 0 until count) {
            val type = data.readUnsignedByte().toInt()
            values[i] =
                if (type == 0) {
                    Integer.valueOf(data.readInt())
                } else {
                    data.readString()
                }
        }
        return values
    }

    public fun decodeHookTransmitList(data: ByteBuf): IntArray? {
        val count = data.readUnsignedByte().toInt()
        if (count == 0) {
            return null
        }
        val values = IntArray(count) { data.readInt() }
        return values
    }

    public fun assignInternal(list: ComponentTypeList, names: Map<String, Int>) {
        val reversedLookup = names.entries.associate { it.value to it.key }
        val types = list.values
        for (type in types) {
            val id = TypeResolver[type]
            val name = reversedLookup[id] ?: continue
            TypeResolver[type] = name
        }
    }
}
