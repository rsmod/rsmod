package org.rsmod.plugins.api.cache.type.ui

import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf
import org.rsmod.game.cache.GameCache
import org.rsmod.game.cache.type.CacheTypeLoader
import javax.inject.Inject

private val logger = InlineLogger()

private const val COMPONENT_ARCHIVE = 3

class ComponentTypeLoader @Inject constructor(
    private val cache: GameCache,
    private val types: ComponentTypeList
) : CacheTypeLoader {

    override fun load() {
        val archive = cache.archive(COMPONENT_ARCHIVE)
        val groups = archive.groupSettings.keys
        groups.forEach { groupId ->
            val group = archive.readGroup(groupId)
            group.files.forEach { (fileId, file) ->
                val component = (groupId shl 16) or fileId
                val type = file.data.type(component)
                types[component] = type
            }
        }
        logger.info { "Loaded ${groups.size} interface type files" }
    }

    private fun ByteBuf.type(id: Int): ComponentType {
        val builder = ComponentTypeBuilder().apply { this.id = id }
        val ifType = getByte(readerIndex()).toInt()
        if (ifType == -1) {
            builder.readBufferIf3(this)
        } else {
            builder.readBufferIf1(this)
        }
        return builder.build()
    }

    private fun ComponentTypeBuilder.readBufferIf1(buf: ByteBuf) {
        hasScripts = false
        type = buf.readUnsignedByte().toInt()
        menuType = buf.readUnsignedByte().toInt()
        contentType = buf.readUnsignedShort()
        originalX = buf.readShort().toInt()
        originalY = buf.readShort().toInt()
        originalWidth = buf.readUnsignedShort()
        originalHeight = buf.readUnsignedShort()
        opacity = buf.readUnsignedByte().toInt()
        parentId = buf.readUnsignedShort()
        if (parentId == 0xFFFF) {
            parentId = -1
        } else {
            parentId += id and 0xFFFF.inv()
        }
        hoverSibling = buf.readUnsignedShortOrMinusOne()

        val operationSize = buf.readUnsignedByte().toInt()
        if (operationSize > 0) {
            operationType = IntArray(operationSize)
            operandRhs = IntArray(operationSize)
            for (i in 0 until operationSize) {
                operationType[i] = buf.readUnsignedByte().toInt()
                operandRhs[i] = buf.readUnsignedShort()
            }
        }

        val cs1Instructions = buf.readUnsignedByte().toInt()
        if (cs1Instructions > 0) {
            val instructionCount = Array(cs1Instructions) {
                val size = buf.readUnsignedShort()
                val instructions = IntArray(size) { buf.readUnsignedShortOrMinusOne() }
                instructions
            }
            instructionCountCs1 = instructionCount.toList()
        }

        if (type == 0) {
            scrollHeight = buf.readUnsignedShort()
            hidden = buf.readBoolean()
        }

        if (type == 1) {
            buf.readUnsignedShort()
            buf.readUnsignedByte()
        }

        if (type == 2) {
            if (buf.readBoolean()) clickMask = clickMask or 0x10000000
            if (buf.readBoolean()) clickMask = clickMask or 0x40000000
            if (buf.readBoolean()) clickMask = clickMask or 0x7fffffff.inv()
            if (buf.readBoolean()) clickMask = clickMask or 0x20000000
            itemId = IntArray(originalX * originalY)
            itemAmount = IntArray(originalX * originalY)
            pitchX = buf.readUnsignedByte().toInt()
            pitchY = buf.readUnsignedByte().toInt()
            offsetX = IntArray(20)
            offsetY = IntArray(20)
            sprites = IntArray(20) { -1 }
            for (i in sprites.indices) {
                val read = buf.readBoolean()
                if (read) {
                    offsetX[i] = buf.readShort().toInt()
                    offsetY[i] = buf.readShort().toInt()
                    sprites[i] = buf.readInt()
                }
            }
            configActions = Array(5) { null }
            for (i in configActions.indices) {
                val text = buf.readStringCP1252()
                if (text.isNotEmpty()) {
                    configActions[i] = text
                    clickMask = clickMask or (1 shl (i + 23))
                }
            }
        }

        if (type == 3) {
            filled = buf.readBoolean()
        }

        if (type == 4 || type == 1) {
            textAlignmentX = buf.readUnsignedByte().toInt()
            textAlignmentY = buf.readUnsignedByte().toInt()
            lineHeight = buf.readUnsignedByte().toInt()
            fontId = buf.readUnsignedShortOrMinusOne()
            shadowedText = buf.readBoolean()
        }

        if (type == 4) {
            text = buf.readStringCP1252()
            altText = buf.readStringCP1252()
        }

        if (type == 1 || type == 3 || type == 4) {
            textColor = buf.readInt()
        }

        if (type == 3 || type == 4) {
            altTextColor = buf.readInt()
            hoverTextColor = buf.readInt()
            altHoverTextColor = buf.readInt()
        }

        if (type == 5) {
            spriteId = buf.readInt()
            altSpriteId = buf.readInt()
        }

        if (type == 6) {
            modelType = 1
            modelId = buf.readUnsignedShortOrMinusOne()
            altModelId = buf.readUnsignedShortOrMinusOne()
            animationId = buf.readUnsignedShortOrMinusOne()
            altAnimationId = buf.readUnsignedShortOrMinusOne()
            modelZoom = buf.readUnsignedShort()
            rotationX = buf.readUnsignedShort()
            rotationZ = buf.readUnsignedShort()
        }

        if (type == 7) {
            itemId = IntArray(originalWidth * originalHeight)
            itemAmount = IntArray(originalWidth * originalHeight)
            textAlignmentX = buf.readUnsignedByte().toInt()
            fontId = buf.readUnsignedShortOrMinusOne()
            shadowedText = buf.readBoolean()
            textColor = buf.readInt()
            pitchX = buf.readShort().toInt()
            pitchY = buf.readShort().toInt()
            if (buf.readBoolean()) clickMask = clickMask or 0x40000000
            configActions = Array(5) { null }
            for (i in configActions.indices) {
                val text = buf.readStringCP1252()
                if (text.isNotEmpty()) {
                    configActions[i] = text
                    clickMask = clickMask or (1 shl (i + 23))
                }
            }
        }

        if (type == 8) {
            text = buf.readStringCP1252()
        }

        if (menuType == 2 || type == 2) {
            targetVerb = buf.readStringCP1252()
            spellName = buf.readStringCP1252()
            val otherMasks = buf.readUnsignedShort() or 0x3F
            clickMask = clickMask or (otherMasks shl 11)
        }

        if (menuType == 1 || menuType == 4 || menuType == 5 || menuType == 6) {
            tooltip = buf.readStringCP1252()
            if (tooltip.isEmpty()) {
                if (menuType == 1) tooltip = "Ok"
                if (menuType in 4..5) tooltip = "Select"
                if (menuType == 6) tooltip = "Continue"
            }
        }

        if (menuType == 1 || menuType == 4 || menuType == 5) {
            clickMask = clickMask or 0x400000
        }

        if (menuType == 6) {
            clickMask = clickMask or 0x1
        }
    }

    private fun ComponentTypeBuilder.readBufferIf3(buf: ByteBuf) {
        buf.skipBytes(Byte.SIZE_BYTES)
        hasScripts = true
        type = buf.readUnsignedByte().toInt()
        contentType = buf.readUnsignedShort()
        originalX = buf.readShort().toInt()
        originalY = buf.readShort().toInt()
        originalWidth = buf.readUnsignedShort()
        originalHeight = if (type == 9) buf.readShort().toInt() else buf.readUnsignedShort()
        dynamicWidth = buf.readByte().toInt()
        buttonType = buf.readByte().toInt()
        dynamicX = buf.readByte().toInt()
        dynamicY = buf.readByte().toInt()
        parentId = buf.readUnsignedShort()
        if (parentId == 0xFFFF) {
            parentId = -1
        } else {
            parentId += id and 0xFFFF.inv()
        }
        hidden = buf.readBoolean()

        if (type == 0) {
            scrollWidth = buf.readUnsignedShort()
            scrollHeight = buf.readUnsignedShort()
            disabledClickThrough = buf.readBoolean()
        }

        if (type == 5) {
            spriteId = buf.readInt()
            textureId = buf.readUnsignedShort()
            spriteTiling = buf.readBoolean()
            opacity = buf.readUnsignedByte().toInt()
            borderThickness = buf.readUnsignedByte().toInt()
            spriteId2 = buf.readInt()
            verticalFlip = buf.readBoolean()
            horizontalFlip = buf.readBoolean()
        }

        if (type == 6) {
            modelType = 1
            modelId = buf.readUnsignedShortOrMinusOne()
            offsetX2d = buf.readShort().toInt()
            offsetY2d = buf.readShort().toInt()
            rotationX = buf.readUnsignedShort()
            rotationZ = buf.readUnsignedShort()
            rotationY = buf.readUnsignedShort()
            modelZoom = buf.readUnsignedShort()
            animationId = buf.readUnsignedShortOrMinusOne()
            orthogonal = buf.readBoolean()
            buf.readUnsignedShort()
            if (dynamicWidth != 0) modelHeight = buf.readUnsignedShort()
            if (buttonType != 0) buf.readUnsignedShort()
        }

        if (type == 4) {
            fontId = buf.readUnsignedShortOrMinusOne()
            text = buf.readStringCP1252()
            lineHeight = buf.readUnsignedByte().toInt()
            textAlignmentX = buf.readUnsignedByte().toInt()
            textAlignmentY = buf.readUnsignedByte().toInt()
            shadowedText = buf.readBoolean()
            textColor = buf.readInt()
        }

        if (type == 3) {
            textColor = buf.readInt()
            filled = buf.readBoolean()
            opacity = buf.readUnsignedByte().toInt()
        }

        if (type == 9) {
            lineWidth = buf.readUnsignedByte().toInt()
            textColor = buf.readInt()
            lineDirection = buf.readBoolean()
        }

        clickMask = buf.readUnsignedMedium()
        opBase = buf.readStringCP1252()
        val actionCount = buf.readUnsignedByte().toInt()
        if (actionCount > 0) {
            val actions = Array(actionCount) { buf.readStringCP1252() }
            this.actions = actions.toList()
        }

        dragDeadZone = buf.readUnsignedByte().toInt()
        dragDeadTime = buf.readUnsignedByte().toInt()
        dragRender = buf.readBoolean()
        targetVerb = buf.readStringCP1252()
        loadListener = buf.readListener()
        mouseOverListener = buf.readListener()
        mouseLeaveListener = buf.readListener()
        targetLeaveListener = buf.readListener()
        targetEnterListener = buf.readListener()
        varTransmitListener = buf.readListener()
        invTransmitListener = buf.readListener()
        statTransmitListener = buf.readListener()
        timerListener = buf.readListener()
        opListener = buf.readListener()
        mouseRepeatListener = buf.readListener()
        clickStartListener = buf.readListener()
        clickRepeatListener = buf.readListener()
        clickReleaseListener = buf.readListener()
        holdListener = buf.readListener()
        dragStartListener = buf.readListener()
        dragReleaseListener = buf.readListener()
        scrollWheelListener = buf.readListener()
        varTransmitTrigger = buf.readTriggers()
        invTransmitTrigger = buf.readTriggers()
        statTransmitTrigger = buf.readTriggers()
    }

    private fun ByteBuf.readListener(): List<Any> {
        val size = readUnsignedByte().toInt()
        if (size == 0) {
            return emptyList()
        }
        val listeners = Array<Any>(size) {}
        for (i in 0 until size) {
            val type = readUnsignedByte().toInt()
            if (type == 0) {
                listeners[i] = readInt()
            } else if (type == 1) {
                listeners[i] = readStringCP1252()
            }
        }
        return listeners.toList()
    }

    private fun ByteBuf.readTriggers(): List<Int> {
        val size = readUnsignedByte().toInt()
        if (size == 0) {
            return emptyList()
        }
        val triggers = IntArray(size) { readInt() }
        return triggers.toList()
    }

    private fun ByteBuf.readUnsignedShortOrMinusOne(): Int {
        val value = readUnsignedShort()
        return if (value == 0xFFFF) -1 else value
    }
}
