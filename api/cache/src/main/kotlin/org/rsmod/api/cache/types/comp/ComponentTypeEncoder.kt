package org.rsmod.api.cache.types.comp

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.writeString
import org.rsmod.game.type.comp.UnpackedComponentType

public object ComponentTypeEncoder {
    public fun encode(type: UnpackedComponentType, data: ByteBuf) {
        if (type.v3) {
            encodeV3(type, data)
        } else {
            encodeV1(type, data)
        }
    }

    public fun encodeV1(unpacked: UnpackedComponentType, data: ByteBuf): Unit =
        with(unpacked) {
            data.writeByte(type)
            data.writeByte(buttonType)
            data.writeShort(clientCode)
            data.writeShort(x)
            data.writeShort(y)
            data.writeShort(width)
            data.writeShort(height)
            data.writeByte(trans1)
            data.writeShort(layer)
            data.writeShort(mouseOverRedirect)

            data.writeByte(cs1Comparisons?.size ?: 0)
            val cs1Comparisons = cs1Comparisons
            val cs1ComparisonValues = cs1ComparisonValues
            if (cs1Comparisons != null && cs1ComparisonValues != null) {
                for (i in cs1Comparisons.indices) {
                    data.writeByte(cs1Comparisons[i].toInt())
                    data.writeShort(cs1ComparisonValues[i].toInt())
                }
            }

            data.writeByte(cs1Instructions?.size ?: 0)
            val cs1Instructions = cs1Instructions
            if (cs1Instructions != null) {
                for (i in cs1Instructions.indices) {
                    data.writeShort(cs1Instructions[i].size)
                    for (j in cs1Instructions[i].indices) {
                        data.writeShort(cs1Instructions[i][j].toInt())
                    }
                }
            }

            if (type == 0) {
                data.writeShort(scrollHeight)
                data.writeBoolean(hide)
            }

            if (type == 1) {
                data.writeShort(0)
                data.writeByte(0)
            }

            if (type == 3) {
                data.writeBoolean(fill)
            }

            if (type == 4 || type == 1) {
                data.writeByte(textAlignH)
                data.writeByte(textAlignV)
                data.writeByte(textLineHeight)
                data.writeShort(textFont)
                data.writeBoolean(textShadow)
            }

            if (type == 4) {
                data.writeString(text)
                data.writeString(secondaryText)
            }

            if (type == 1 || type == 3 || type == 4) {
                data.writeInt(colour1)
            }

            if (type == 3 || type == 4) {
                data.writeInt(colour2)
                data.writeInt(mouseOverColour1)
                data.writeInt(mouseOverColour2)
            }

            if (type == 5) {
                data.writeInt(graphic)
                data.writeInt(secondaryGraphic)
            }

            if (type == 6) {
                data.writeShort(model)
                data.writeShort(secondaryModel)
                data.writeShort(modelAnim)
                data.writeShort(secondaryModelAnim)
                data.writeShort(modelZoom)
                data.writeShort(modelAngleX)
                data.writeShort(modelAngleY)
            }

            if (type == 8) {
                data.writeString(text)
            }

            if (buttonType == 2) {
                data.writeString(targetVerb)
                data.writeString(targetBase)
                data.writeShort(events)
            }

            if (buttonType == 1 || buttonType == 4 || buttonType == 5 || buttonType == 6) {
                data.writeString(buttonText)
            }
        }

    public fun encodeV3(unpacked: UnpackedComponentType, data: ByteBuf): Unit =
        with(unpacked) {
            data.writeByte(-1)
            data.writeByte(type)
            data.writeShort(clientCode)
            data.writeShort(x)
            data.writeShort(y)
            data.writeShort(width)
            data.writeShort(height)
            data.writeByte(widthMode)
            data.writeByte(heightMode)
            data.writeByte(xMode)
            data.writeByte(yMode)
            data.writeShort(layer)

            data.writeBoolean(hide)

            if (type == 0) {
                data.writeShort(scrollWidth)
                data.writeShort(scrollHeight)
                data.writeBoolean(noClickThrough)
            }

            if (type == 5) {
                data.writeInt(graphic)
                data.writeShort(angle2d)
                data.writeBoolean(tiling)
                data.writeByte(trans1)
                data.writeByte(outline)
                data.writeInt(graphicShadow)
                data.writeBoolean(vFlip)
                data.writeBoolean(hFlip)
            }

            if (type == 6) {
                data.writeShort(model)
                data.writeShort(modelX)
                data.writeShort(modelY)
                data.writeShort(modelAngleX)
                data.writeShort(modelAngleY)
                data.writeShort(modelAngleZ)
                data.writeShort(modelZoom)
                data.writeShort(modelAnim)
                data.writeBoolean(modelOrthog)
                data.writeShort(0)
                if (widthMode != 0) {
                    data.writeShort(modelObjWidth)
                }
                if (heightMode != 0) {
                    data.writeShort(0)
                }
            }

            if (type == 4) {
                data.writeShort(textFont)
                data.writeString(text)
                data.writeByte(textLineHeight)
                data.writeByte(textAlignH)
                data.writeByte(textAlignV)
                data.writeBoolean(textShadow)
                data.writeInt(colour1)
            }

            if (type == 3) {
                data.writeInt(colour1)
                data.writeBoolean(fill)
                data.writeByte(trans1)
            }

            if (type == 9) {
                data.writeByte(lineWid)
                data.writeInt(colour1)
                data.writeBoolean(lineDirection)
            }

            data.writeMedium(events)
            data.writeString(opBase)

            data.writeByte(op.size)
            for (i in op.indices) {
                data.writeString(op[i])
            }

            data.writeByte(dragDeadZone)
            data.writeByte(dragDeadTime)
            data.writeBoolean(draggableBehavior)
            data.writeString(targetVerb)

            encodeHook(onLoad, data)
            encodeHook(onMouseOver, data)
            encodeHook(onMouseLeave, data)
            encodeHook(onTargetLeave, data)
            encodeHook(onTargetEnter, data)
            encodeHook(onVarTransmit, data)
            encodeHook(onInvTransmit, data)
            encodeHook(onStatTransmit, data)
            encodeHook(onTimer, data)
            encodeHook(onOp, data)
            encodeHook(onMouseRepeat, data)
            encodeHook(onClick, data)
            encodeHook(onClickRepeat, data)
            encodeHook(onRelease, data)
            encodeHook(onHold, data)
            encodeHook(onDrag, data)
            encodeHook(onDragComplete, data)
            encodeHook(onScrollWheel, data)
            encodeHookTransmitList(onVarTransmitList, data)
            encodeHookTransmitList(onInvTransmitList, data)
            encodeHookTransmitList(onStatTransmitList, data)
        }

    public fun encodeHook(values: Array<Any>?, data: ByteBuf) {
        data.writeByte(values?.size ?: 0)
        if (values == null) {
            return
        }
        for (i in values.indices) {
            val value = values[i]
            if (value is Int) {
                data.writeByte(0)
                data.writeInt(value)
            } else if (value is String) {
                data.writeByte(1)
                data.writeString(value)
            } else {
                error("Invalid value type: $value (${value::class.simpleName})")
            }
        }
    }

    public fun encodeHookTransmitList(values: IntArray?, data: ByteBuf) {
        data.writeByte(values?.size ?: 0)
        if (values == null) {
            return
        }
        for (i in values.indices) {
            data.writeInt(values[i])
        }
    }
}
