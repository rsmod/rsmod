package org.rsmod.game.type.comp

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.ui.Component.Companion.CHILD_BIT_MASK
import org.rsmod.game.ui.Component.Companion.CHILD_BIT_OFFSET
import org.rsmod.game.ui.Component.Companion.PARENT_BIT_MASK
import org.rsmod.game.ui.Component.Companion.PARENT_BIT_OFFSET

public sealed class ComponentType : CacheType() {
    public val packed: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val interfaceId: Int
        get() = (packed shr PARENT_BIT_OFFSET) and PARENT_BIT_MASK

    public val component: Int
        get() = (packed shr CHILD_BIT_OFFSET) and CHILD_BIT_MASK

    public fun isType(other: ComponentType): Boolean = other.internalId == internalId
}

public data class HashedComponentType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, ComponentType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "ComponentType(" +
            "internalName='$internalName', " +
            "component=$interfaceId:$component, " +
            "combinedId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedComponentType) return false
        if (startHash != other.startHash) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + (startHash?.hashCode() ?: 0)
        return result
    }
}

public data class UnpackedComponentType(
    public val v3: Boolean,
    public val type: Int,
    public val buttonType: Int,
    public val clientCode: Int,
    public val x: Int,
    public val y: Int,
    public val width: Int,
    public val height: Int,
    public val trans1: Int,
    public val layer: Int,
    public val mouseOverRedirect: Int,
    public val cs1Comparisons: ShortArray?,
    public val cs1ComparisonValues: IntArray?,
    public val cs1Instructions: Array<IntArray>?,
    public val scrollHeight: Int,
    public val hide: Boolean,
    public val fill: Boolean,
    public val textAlignH: Int,
    public val textAlignV: Int,
    public val textLineHeight: Int,
    public val textFont: Int,
    public val textShadow: Boolean,
    public val secondaryText: String,
    public val colour1: Int,
    public val colour2: Int,
    public val mouseOverColour1: Int,
    public val mouseOverColour2: Int,
    public val graphic: Int,
    public val secondaryGraphic: Int,
    public val modelKind: Int,
    public val model: Int,
    public val secondaryModelKind: Int,
    public val secondaryModel: Int,
    public val modelAnim: Int,
    public val secondaryModelAnim: Int,
    public val modelZoom: Int,
    public val modelAngleX: Int,
    public val modelAngleY: Int,
    public val text: String,
    public val targetVerb: String,
    public val targetBase: String,
    public val events: Int,
    public val buttonText: String,
    public val widthMode: Int,
    public val heightMode: Int,
    public val xMode: Int,
    public val yMode: Int,
    public val scrollWidth: Int,
    public val noClickThrough: Boolean,
    public val angle2d: Int,
    public val tiling: Boolean,
    public val outline: Int,
    public val graphicShadow: Int,
    public val vFlip: Boolean,
    public val hFlip: Boolean,
    public val modelX: Int,
    public val modelY: Int,
    public val modelAngleZ: Int,
    public val modelOrthog: Boolean,
    public val modelObjWidth: Int,
    public val lineWid: Int,
    public val lineDirection: Boolean,
    public val opBase: String,
    public val op: Array<String>,
    public val dragDeadZone: Int,
    public val dragDeadTime: Int,
    public val draggableBehavior: Boolean,
    public val onLoad: Array<Any>?,
    public val onMouseOver: Array<Any>?,
    public val onMouseLeave: Array<Any>?,
    public val onTargetLeave: Array<Any>?,
    public val onTargetEnter: Array<Any>?,
    public val onVarTransmit: Array<Any>?,
    public val onInvTransmit: Array<Any>?,
    public val onStatTransmit: Array<Any>?,
    public val onTimer: Array<Any>?,
    public val onOp: Array<Any>?,
    public val onMouseRepeat: Array<Any>?,
    public val onClick: Array<Any>?,
    public val onClickRepeat: Array<Any>?,
    public val onRelease: Array<Any>?,
    public val onHold: Array<Any>?,
    public val onDrag: Array<Any>?,
    public val onDragComplete: Array<Any>?,
    public val onScrollWheel: Array<Any>?,
    public val onVarTransmitList: IntArray?,
    public val onInvTransmitList: IntArray?,
    public val onStatTransmitList: IntArray?,
    override var internalId: Int?,
    override var internalName: String?,
) : ComponentType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun hasEvent(event: IfEvent): Boolean {
        return (events and event.bitmask) != 0
    }

    public fun toHashedType(): HashedComponentType =
        HashedComponentType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = v3.hashCode().toLong()
        result = 61 * result + type
        result = 61 * result + buttonType
        result = 61 * result + clientCode
        result = 61 * result + x
        result = 61 * result + y
        result = 61 * result + width
        result = 61 * result + height
        result = 61 * result + trans1
        result = 61 * result + layer
        result = 61 * result + mouseOverRedirect
        result = 61 * result + cs1Comparisons.contentHashCode()
        result = 61 * result + cs1ComparisonValues.contentHashCode()
        result = 61 * result + cs1Instructions.contentDeepHashCode()
        result = 61 * result + scrollHeight
        result = 61 * result + hide.hashCode()
        result = 61 * result + fill.hashCode()
        result = 61 * result + textAlignH
        result = 61 * result + textAlignV
        result = 61 * result + textLineHeight
        result = 61 * result + textFont
        result = 61 * result + textShadow.hashCode()
        result = 61 * result + secondaryText.hashCode()
        result = 61 * result + colour1
        result = 61 * result + colour2
        result = 61 * result + mouseOverColour1
        result = 61 * result + mouseOverColour2
        result = 61 * result + graphic
        result = 61 * result + secondaryGraphic
        result = 61 * result + modelKind
        result = 61 * result + model
        result = 61 * result + secondaryModelKind
        result = 61 * result + secondaryModel
        result = 61 * result + modelAnim
        result = 61 * result + secondaryModelAnim
        result = 61 * result + modelZoom
        result = 61 * result + modelAngleX
        result = 61 * result + modelAngleY
        result = 61 * result + text.hashCode()
        result = 61 * result + targetVerb.hashCode()
        result = 61 * result + targetBase.hashCode()
        result = 61 * result + events
        result = 61 * result + buttonText.hashCode()
        result = 61 * result + widthMode
        result = 61 * result + heightMode
        result = 61 * result + xMode
        result = 61 * result + yMode
        result = 61 * result + scrollWidth
        result = 61 * result + noClickThrough.hashCode()
        result = 61 * result + angle2d
        result = 61 * result + tiling.hashCode()
        result = 61 * result + outline
        result = 61 * result + graphicShadow
        result = 61 * result + vFlip.hashCode()
        result = 61 * result + hFlip.hashCode()
        result = 61 * result + modelX
        result = 61 * result + modelY
        result = 61 * result + modelAngleZ
        result = 61 * result + modelOrthog.hashCode()
        result = 61 * result + modelObjWidth
        result = 61 * result + lineWid
        result = 61 * result + lineDirection.hashCode()
        result = 61 * result + opBase.hashCode()
        result = 61 * result + op.contentHashCode()
        result = 61 * result + dragDeadZone
        result = 61 * result + dragDeadTime
        result = 61 * result + draggableBehavior.hashCode()
        result = 61 * result + onLoad.contentHashCode()
        result = 61 * result + onMouseOver.contentHashCode()
        result = 61 * result + onMouseLeave.contentHashCode()
        result = 61 * result + onTargetLeave.contentHashCode()
        result = 61 * result + onTargetEnter.contentHashCode()
        result = 61 * result + onVarTransmit.contentHashCode()
        result = 61 * result + onInvTransmit.contentHashCode()
        result = 61 * result + onStatTransmit.contentHashCode()
        result = 61 * result + onTimer.contentHashCode()
        result = 61 * result + onOp.contentHashCode()
        result = 61 * result + onMouseRepeat.contentHashCode()
        result = 61 * result + onClick.contentHashCode()
        result = 61 * result + onClickRepeat.contentHashCode()
        result = 61 * result + onRelease.contentHashCode()
        result = 61 * result + onHold.contentHashCode()
        result = 61 * result + onDrag.contentHashCode()
        result = 61 * result + onDragComplete.contentHashCode()
        result = 61 * result + onScrollWheel.contentHashCode()
        result = 61 * result + onVarTransmitList.contentHashCode()
        result = 61 * result + onInvTransmitList.contentHashCode()
        result = 61 * result + onStatTransmitList.contentHashCode()
        result = 61 * result + (internalId?.hashCode() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedComponentType(" +
            "internalName='$internalName', " +
            "internalId=$interfaceId:$component (packed=$packed), " +
            "internalHash=${computeIdentityHash()}, " +
            "v3=$v3, " +
            "type=$type, " +
            "buttonType=$buttonType, " +
            "clientCode=$clientCode, " +
            "x=$x, " +
            "y=$y, " +
            "width=$width, " +
            "height=$height, " +
            "trans1=$trans1, " +
            "layer=$layer, " +
            "mouseOverRedirect=$mouseOverRedirect, " +
            "cs1Comparisons=${cs1Comparisons.contentToString()}, " +
            "cs1ComparisonValues=${cs1ComparisonValues.contentToString()}, " +
            "cs1Instructions=${cs1Instructions?.contentsFormat()}, " +
            "scrollHeight=$scrollHeight, " +
            "hide=$hide, " +
            "fill=$fill, " +
            "textAlignH=$textAlignH, " +
            "textAlignV=$textAlignV, " +
            "textLineHeight=$textLineHeight, " +
            "textFont=$textFont, " +
            "textShadow=$textShadow, " +
            "secondaryText='$secondaryText', " +
            "colour1=$colour1, " +
            "colour2=$colour2, " +
            "mouseOverColour1=$mouseOverColour1, " +
            "mouseOverColour2=$mouseOverColour2, " +
            "graphic=$graphic, " +
            "secondaryGraphic=$secondaryGraphic, " +
            "modelKind=$modelKind, " +
            "model=$model, " +
            "secondaryModelKind=$secondaryModelKind, " +
            "secondaryModel=$secondaryModel, " +
            "modelAnim=$modelAnim, " +
            "secondaryModelAnim=$secondaryModelAnim, " +
            "modelZoom=$modelZoom, " +
            "modelAngleX=$modelAngleX, " +
            "modelAngleY=$modelAngleY, " +
            "text='$text', " +
            "targetVerb='$targetVerb', " +
            "targetBase='$targetBase', " +
            "events=$events, " +
            "buttonText='$buttonText', " +
            "widthMode=$widthMode, " +
            "heightMode=$heightMode, " +
            "xMode=$xMode, " +
            "yMode=$yMode, " +
            "scrollWidth=$scrollWidth, " +
            "noClickThrough=$noClickThrough, " +
            "angle2d=$angle2d, " +
            "tiling=$tiling, " +
            "outline=$outline, " +
            "graphicShadow=$graphicShadow, " +
            "vFlip=$vFlip, " +
            "hFlip=$hFlip, " +
            "modelX=$modelX, " +
            "modelY=$modelY, " +
            "modelAngleZ=$modelAngleZ, " +
            "modelOrthog=$modelOrthog, " +
            "modelObjWidth=$modelObjWidth, " +
            "lineWid=$lineWid, " +
            "lineDirection=$lineDirection, " +
            "opBase='$opBase', " +
            "op=${op.contentToString()}, " +
            "dragDeadZone=$dragDeadZone, " +
            "dragDeadTime=$dragDeadTime, " +
            "draggableBehavior=$draggableBehavior, " +
            "onLoad=${onLoad.contentToString()}, " +
            "onMouseOver=${onMouseOver.contentToString()}, " +
            "onMouseLeave=${onMouseLeave.contentToString()}, " +
            "onTargetLeave=${onTargetLeave.contentToString()}, " +
            "onTargetEnter=${onTargetEnter.contentToString()}, " +
            "onVarTransmit=${onVarTransmit.contentToString()}, " +
            "onInvTransmit=${onInvTransmit.contentToString()}, " +
            "onStatTransmit=${onStatTransmit.contentToString()}, " +
            "onTimer=${onTimer.contentToString()}, " +
            "onOp=${onOp.contentToString()}, " +
            "onMouseRepeat=${onMouseRepeat.contentToString()}, " +
            "onClick=${onClick.contentToString()}, " +
            "onClickRepeat=${onClickRepeat.contentToString()}, " +
            "onRelease=${onRelease.contentToString()}, " +
            "onHold=${onHold.contentToString()}, " +
            "onDrag=${onDrag.contentToString()}, " +
            "onDragComplete=${onDragComplete.contentToString()}, " +
            "onScrollWheel=${onScrollWheel.contentToString()}, " +
            "onVarTransmitList=${onVarTransmitList.contentToString()}, " +
            "onInvTransmitList=${onInvTransmitList.contentToString()}, " +
            "onStatTransmitList=${onStatTransmitList.contentToString()}" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedComponentType) return false

        if (v3 != other.v3) return false
        if (type != other.type) return false
        if (buttonType != other.buttonType) return false
        if (clientCode != other.clientCode) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (trans1 != other.trans1) return false
        if (layer != other.layer) return false
        if (mouseOverRedirect != other.mouseOverRedirect) return false
        if (cs1Comparisons != null) {
            if (other.cs1Comparisons == null) return false
            if (!cs1Comparisons.contentEquals(other.cs1Comparisons)) return false
        } else if (other.cs1Comparisons != null) return false
        if (cs1ComparisonValues != null) {
            if (other.cs1ComparisonValues == null) return false
            if (!cs1ComparisonValues.contentEquals(other.cs1ComparisonValues)) return false
        } else if (other.cs1ComparisonValues != null) return false
        if (cs1Instructions != null) {
            if (other.cs1Instructions == null) return false
            if (!cs1Instructions.contentDeepEquals(other.cs1Instructions)) return false
        } else if (other.cs1Instructions != null) return false
        if (scrollHeight != other.scrollHeight) return false
        if (hide != other.hide) return false
        if (fill != other.fill) return false
        if (textAlignH != other.textAlignH) return false
        if (textAlignV != other.textAlignV) return false
        if (textLineHeight != other.textLineHeight) return false
        if (textFont != other.textFont) return false
        if (textShadow != other.textShadow) return false
        if (secondaryText != other.secondaryText) return false
        if (colour1 != other.colour1) return false
        if (colour2 != other.colour2) return false
        if (mouseOverColour1 != other.mouseOverColour1) return false
        if (mouseOverColour2 != other.mouseOverColour2) return false
        if (graphic != other.graphic) return false
        if (secondaryGraphic != other.secondaryGraphic) return false
        if (modelKind != other.modelKind) return false
        if (model != other.model) return false
        if (secondaryModelKind != other.secondaryModelKind) return false
        if (secondaryModel != other.secondaryModel) return false
        if (modelAnim != other.modelAnim) return false
        if (secondaryModelAnim != other.secondaryModelAnim) return false
        if (modelZoom != other.modelZoom) return false
        if (modelAngleX != other.modelAngleX) return false
        if (modelAngleY != other.modelAngleY) return false
        if (text != other.text) return false
        if (targetVerb != other.targetVerb) return false
        if (targetBase != other.targetBase) return false
        if (events != other.events) return false
        if (buttonText != other.buttonText) return false
        if (widthMode != other.widthMode) return false
        if (heightMode != other.heightMode) return false
        if (xMode != other.xMode) return false
        if (yMode != other.yMode) return false
        if (scrollWidth != other.scrollWidth) return false
        if (noClickThrough != other.noClickThrough) return false
        if (angle2d != other.angle2d) return false
        if (tiling != other.tiling) return false
        if (outline != other.outline) return false
        if (graphicShadow != other.graphicShadow) return false
        if (vFlip != other.vFlip) return false
        if (hFlip != other.hFlip) return false
        if (modelX != other.modelX) return false
        if (modelY != other.modelY) return false
        if (modelAngleZ != other.modelAngleZ) return false
        if (modelOrthog != other.modelOrthog) return false
        if (modelObjWidth != other.modelObjWidth) return false
        if (lineWid != other.lineWid) return false
        if (lineDirection != other.lineDirection) return false
        if (opBase != other.opBase) return false
        if (!op.contentEquals(other.op)) return false
        if (dragDeadZone != other.dragDeadZone) return false
        if (dragDeadTime != other.dragDeadTime) return false
        if (draggableBehavior != other.draggableBehavior) return false
        if (onLoad != null) {
            if (other.onLoad == null) return false
            if (!onLoad.contentEquals(other.onLoad)) return false
        } else if (other.onLoad != null) return false
        if (onMouseOver != null) {
            if (other.onMouseOver == null) return false
            if (!onMouseOver.contentEquals(other.onMouseOver)) return false
        } else if (other.onMouseOver != null) return false
        if (onMouseLeave != null) {
            if (other.onMouseLeave == null) return false
            if (!onMouseLeave.contentEquals(other.onMouseLeave)) return false
        } else if (other.onMouseLeave != null) return false
        if (onTargetLeave != null) {
            if (other.onTargetLeave == null) return false
            if (!onTargetLeave.contentEquals(other.onTargetLeave)) return false
        } else if (other.onTargetLeave != null) return false
        if (onTargetEnter != null) {
            if (other.onTargetEnter == null) return false
            if (!onTargetEnter.contentEquals(other.onTargetEnter)) return false
        } else if (other.onTargetEnter != null) return false
        if (onVarTransmit != null) {
            if (other.onVarTransmit == null) return false
            if (!onVarTransmit.contentEquals(other.onVarTransmit)) return false
        } else if (other.onVarTransmit != null) return false
        if (onInvTransmit != null) {
            if (other.onInvTransmit == null) return false
            if (!onInvTransmit.contentEquals(other.onInvTransmit)) return false
        } else if (other.onInvTransmit != null) return false
        if (onStatTransmit != null) {
            if (other.onStatTransmit == null) return false
            if (!onStatTransmit.contentEquals(other.onStatTransmit)) return false
        } else if (other.onStatTransmit != null) return false
        if (onTimer != null) {
            if (other.onTimer == null) return false
            if (!onTimer.contentEquals(other.onTimer)) return false
        } else if (other.onTimer != null) return false
        if (onOp != null) {
            if (other.onOp == null) return false
            if (!onOp.contentEquals(other.onOp)) return false
        } else if (other.onOp != null) return false
        if (onMouseRepeat != null) {
            if (other.onMouseRepeat == null) return false
            if (!onMouseRepeat.contentEquals(other.onMouseRepeat)) return false
        } else if (other.onMouseRepeat != null) return false
        if (onClick != null) {
            if (other.onClick == null) return false
            if (!onClick.contentEquals(other.onClick)) return false
        } else if (other.onClick != null) return false
        if (onClickRepeat != null) {
            if (other.onClickRepeat == null) return false
            if (!onClickRepeat.contentEquals(other.onClickRepeat)) return false
        } else if (other.onClickRepeat != null) return false
        if (onRelease != null) {
            if (other.onRelease == null) return false
            if (!onRelease.contentEquals(other.onRelease)) return false
        } else if (other.onRelease != null) return false
        if (onHold != null) {
            if (other.onHold == null) return false
            if (!onHold.contentEquals(other.onHold)) return false
        } else if (other.onHold != null) return false
        if (onDrag != null) {
            if (other.onDrag == null) return false
            if (!onDrag.contentEquals(other.onDrag)) return false
        } else if (other.onDrag != null) return false
        if (onDragComplete != null) {
            if (other.onDragComplete == null) return false
            if (!onDragComplete.contentEquals(other.onDragComplete)) return false
        } else if (other.onDragComplete != null) return false
        if (onScrollWheel != null) {
            if (other.onScrollWheel == null) return false
            if (!onScrollWheel.contentEquals(other.onScrollWheel)) return false
        } else if (other.onScrollWheel != null) return false
        if (onVarTransmitList != null) {
            if (other.onVarTransmitList == null) return false
            if (!onVarTransmitList.contentEquals(other.onVarTransmitList)) return false
        } else if (other.onVarTransmitList != null) return false
        if (onInvTransmitList != null) {
            if (other.onInvTransmitList == null) return false
            if (!onInvTransmitList.contentEquals(other.onInvTransmitList)) return false
        } else if (other.onInvTransmitList != null) return false
        if (onStatTransmitList != null) {
            if (other.onStatTransmitList == null) return false
            if (!onStatTransmitList.contentEquals(other.onStatTransmitList)) return false
        } else if (other.onStatTransmitList != null) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()

    private fun Array<IntArray>.contentsFormat() =
        joinToString(separator = ", ", prefix = "[", postfix = "]") {
            it.joinToString(separator = ", ", prefix = "[", postfix = "]")
        }
}
