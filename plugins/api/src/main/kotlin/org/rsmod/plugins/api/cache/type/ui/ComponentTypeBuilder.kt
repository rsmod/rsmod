package org.rsmod.plugins.api.cache.type.ui

import org.rsmod.game.model.ui.type.ComponentType

private const val DEFAULT_ID = -1
private const val DEFAULT_SCRIPT_HANDLING = false
private const val DEFAULT_TYPE = -1
private const val DEFAULT_MENU_TYPE = 0
private const val DEFAULT_CONTENT_TYPE = 0
private const val DEFAULT_X_2D = 0
private const val DEFAULT_Y_2D = 0
private const val DEFAULT_WIDTH_2D = 0
private const val DEFAULT_HEIGHT_2D = 0
private const val DEFAULT_OPACITY = 0
private const val DEFAULT_PARENT_ID = -1
private const val DEFAULT_HOVER_SIBLING = -1
private const val DEFAULT_SCROLL_HEIGHT = 0
private const val DEFAULT_SCROLL_WIDTH = 0
private const val DEFAULT_HIDDEN_FLAG = false
private const val DEFAULT_CLICK_MASK = 0
private const val DEFAULT_PITCH_X = 0
private const val DEFAULT_PITCH_Y = 0
private const val DEFAULT_FILLED_FLAG = false
private const val DEFAULT_TEXT_ALIGNMENT_X = 0
private const val DEFAULT_TEXT_ALIGNMENT_Y = 0
private const val DEFAULT_LINE_HEIGHT = 0
private const val DEFAULT_FONT_ID = -1
private const val DEFAULT_SHADOW_TEXT_FLAG = false
private const val DEFAULT_TEXT_COLOR = 0
private const val DEFAULT_SPRITE_ID = -1
private const val DEFAULT_SPRITE_ID2 = 0
private const val DEFAULT_MODEL_ID = -1
private const val DEFAULT_MODEL_TYPE = 1
private const val DEFAULT_MODEL_ZOOM = 100
private const val DEFAULT_ANIMATION_ID = -1
private const val DEFAULT_ROTATION_X = 0
private const val DEFAULT_ROTATION_Y = 0
private const val DEFAULT_ROTATION_Z = 0
private const val DEFAULT_TOOLTIP = "Ok"
private const val DEFAULT_BUTTON_TYPE = 0
private const val DEFAULT_DISABLED_CLICK_THROUGH_FLAG = false
private const val DEFAULT_TEXTURE_ID = 0
private const val DEFAULT_SPRITE_TILING_FLAG = false
private const val DEFAULT_BORDER_THICKNESS = 0
private const val DEFAULT_VERTICAL_FLIP_FLAG = false
private const val DEFAULT_HORIZONTAL_FLIP_FLAG = false
private const val DEFAULT_ORTHOGONAL_FLAG = false
private const val DEFAULT_LINE_DIRECTION = false
private const val DEFAULT_DRAG_RENDER = false
private const val DEFAULT_DRAG_DEAD_ZONE = 0
private const val DEFAULT_DRAG_DEAD_TIME = 0

private val DEFAULT_CS1_INSTRUCTION_COUNT = emptyList<IntArray>()
private val DEFAULT_ACTIONS = emptyList<String?>()

private const val EMPTY_STR = ""
private val EMPTY_STR_ARRAY = emptyArray<String?>()
private val EMPTY_INT_ARRAY = IntArray(0)
private val EMPTY_LISTENER = emptyList<Any>()
private val EMPTY_TRIGGER = emptyList<Int>()

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class ComponentTypeBuilder(
    var id: Int = DEFAULT_ID,
    var hasScripts: Boolean = DEFAULT_SCRIPT_HANDLING,
    var type: Int = DEFAULT_TYPE,
    var menuType: Int = DEFAULT_MENU_TYPE,
    var contentType: Int = DEFAULT_CONTENT_TYPE,
    var originalX: Int = DEFAULT_X_2D,
    var originalY: Int = DEFAULT_Y_2D,
    var originalWidth: Int = DEFAULT_WIDTH_2D,
    var originalHeight: Int = DEFAULT_HEIGHT_2D,
    var opacity: Int = DEFAULT_OPACITY,
    var parentId: Int = DEFAULT_PARENT_ID,
    var hoverSibling: Int = DEFAULT_HOVER_SIBLING,
    var operationType: IntArray = EMPTY_INT_ARRAY,
    var operandRhs: IntArray = EMPTY_INT_ARRAY,
    var instructionCountCs1: List<IntArray> = DEFAULT_CS1_INSTRUCTION_COUNT,
    var scrollWidth: Int = DEFAULT_SCROLL_WIDTH,
    var scrollHeight: Int = DEFAULT_SCROLL_HEIGHT,
    var hidden: Boolean = DEFAULT_HIDDEN_FLAG,
    var itemId: IntArray = EMPTY_INT_ARRAY,
    var itemAmount: IntArray = EMPTY_INT_ARRAY,
    var clickMask: Int = DEFAULT_CLICK_MASK,
    var pitchX: Int = DEFAULT_PITCH_X,
    var pitchY: Int = DEFAULT_PITCH_Y,
    var offsetX: IntArray = EMPTY_INT_ARRAY,
    var offsetY: IntArray = EMPTY_INT_ARRAY,
    var sprites: IntArray = EMPTY_INT_ARRAY,
    var configActions: Array<String?> = EMPTY_STR_ARRAY,
    var filled: Boolean = DEFAULT_FILLED_FLAG,
    var textAlignmentX: Int = DEFAULT_TEXT_ALIGNMENT_X,
    var textAlignmentY: Int = DEFAULT_TEXT_ALIGNMENT_Y,
    var lineHeight: Int = DEFAULT_LINE_HEIGHT,
    var fontId: Int = DEFAULT_FONT_ID,
    var shadowedText: Boolean = DEFAULT_SHADOW_TEXT_FLAG,
    var text: String = EMPTY_STR,
    var altText: String = EMPTY_STR,
    var textColor: Int = DEFAULT_TEXT_COLOR,
    var altTextColor: Int = DEFAULT_TEXT_COLOR,
    var hoverTextColor: Int = DEFAULT_TEXT_COLOR,
    var altHoverTextColor: Int = DEFAULT_TEXT_COLOR,
    var spriteId: Int = DEFAULT_SPRITE_ID,
    var altSpriteId: Int = DEFAULT_SPRITE_ID,
    var spriteId2: Int = DEFAULT_SPRITE_ID2,
    var modelId: Int = DEFAULT_MODEL_ID,
    var altModelId: Int = DEFAULT_MODEL_ID,
    var modelType: Int = DEFAULT_MODEL_TYPE,
    var modelZoom: Int = DEFAULT_MODEL_ZOOM,
    var animationId: Int = DEFAULT_ANIMATION_ID,
    var altAnimationId: Int = DEFAULT_ANIMATION_ID,
    var rotationX: Int = DEFAULT_ROTATION_X,
    var rotationY: Int = DEFAULT_ROTATION_Y,
    var rotationZ: Int = DEFAULT_ROTATION_Z,
    var targetVerb: String = EMPTY_STR,
    var spellName: String = EMPTY_STR,
    var tooltip: String = DEFAULT_TOOLTIP,
    var dynamicX: Int = DEFAULT_X_2D,
    var dynamicY: Int = DEFAULT_Y_2D,
    var dynamicWidth: Int = DEFAULT_WIDTH_2D,
    var buttonType: Int = DEFAULT_BUTTON_TYPE,
    var disabledClickThrough: Boolean = DEFAULT_DISABLED_CLICK_THROUGH_FLAG,
    var textureId: Int = DEFAULT_TEXTURE_ID,
    var spriteTiling: Boolean = DEFAULT_SPRITE_TILING_FLAG,
    var borderThickness: Int = DEFAULT_BORDER_THICKNESS,
    var verticalFlip: Boolean = DEFAULT_VERTICAL_FLIP_FLAG,
    var horizontalFlip: Boolean = DEFAULT_HORIZONTAL_FLIP_FLAG,
    var offsetX2d: Int = DEFAULT_X_2D,
    var offsetY2d: Int = DEFAULT_Y_2D,
    var orthogonal: Boolean = DEFAULT_ORTHOGONAL_FLAG,
    var modelHeight: Int = DEFAULT_HEIGHT_2D,
    var lineWidth: Int = DEFAULT_WIDTH_2D,
    var lineDirection: Boolean = DEFAULT_LINE_DIRECTION,
    var opBase: String = EMPTY_STR,
    var actions: List<String?> = DEFAULT_ACTIONS,
    var dragRender: Boolean = DEFAULT_DRAG_RENDER,
    var dragDeadZone: Int = DEFAULT_DRAG_DEAD_ZONE,
    var dragDeadTime: Int = DEFAULT_DRAG_DEAD_TIME,
    var loadListener: List<Any> = EMPTY_LISTENER,
    var mouseOverListener: List<Any> = EMPTY_LISTENER,
    var mouseLeaveListener: List<Any> = EMPTY_LISTENER,
    var targetEnterListener: List<Any> = EMPTY_LISTENER,
    var targetLeaveListener: List<Any> = EMPTY_LISTENER,
    var varTransmitListener: List<Any> = EMPTY_LISTENER,
    var invTransmitListener: List<Any> = EMPTY_LISTENER,
    var statTransmitListener: List<Any> = EMPTY_LISTENER,
    var timerListener: List<Any> = EMPTY_LISTENER,
    var opListener: List<Any> = EMPTY_LISTENER,
    var mouseRepeatListener: List<Any> = EMPTY_LISTENER,
    var clickStartListener: List<Any> = EMPTY_LISTENER,
    var clickRepeatListener: List<Any> = EMPTY_LISTENER,
    var clickReleaseListener: List<Any> = EMPTY_LISTENER,
    var holdListener: List<Any> = EMPTY_LISTENER,
    var dragStartListener: List<Any> = EMPTY_LISTENER,
    var dragReleaseListener: List<Any> = EMPTY_LISTENER,
    var scrollWheelListener: List<Any> = EMPTY_LISTENER,
    var varTransmitTrigger: List<Int> = EMPTY_TRIGGER,
    var invTransmitTrigger: List<Int> = EMPTY_TRIGGER,
    var statTransmitTrigger: List<Int> = EMPTY_TRIGGER
) {

    fun build() = ComponentType(
        id = id,
        scripts = hasScripts,
        type = type,
        menuType = menuType,
        contentType = contentType,
        originalX = originalX,
        originalY = originalY,
        originalWidth = originalWidth,
        originalHeight = originalHeight,
        opacity = opacity,
        parentId = parentId,
        hoverSibling = hoverSibling,
        operationType = operationType.toList(),
        operandRhs = operandRhs.toList(),
        instructionCountCs1 = instructionCountCs1.toList(),
        scrollHeight = scrollHeight,
        scrollWidth = scrollWidth,
        hidden = hidden,
        itemId = itemId.toList(),
        itemAmount = itemAmount.toList(),
        clickMask = clickMask,
        pitchX = pitchX,
        pitchY = pitchY,
        offsetX = offsetX.toList(),
        offsetY = offsetY.toList(),
        sprites = sprites.toList(),
        configActions = configActions.toList(),
        filled = filled,
        textAlignmentX = textAlignmentX,
        textAlignmentY = textAlignmentY,
        lineHeight = lineHeight,
        fontId = fontId,
        shadowedText = shadowedText,
        text = text,
        altText = altText,
        textColor = textColor,
        altTextColor = altTextColor,
        hoverTextColor = hoverTextColor,
        altHoverTextColor = altHoverTextColor,
        spriteId = spriteId,
        spriteId2 = spriteId2,
        altSpriteId = altSpriteId,
        modelId = modelId,
        altModelId = altModelId,
        modelType = modelType,
        modelZoom = modelZoom,
        animationId = animationId,
        altAnimationId = altAnimationId,
        rotationX = rotationX,
        rotationY = rotationY,
        rotationZ = rotationZ,
        targetVerb = targetVerb,
        spellName = spellName,
        tooltip = tooltip,
        dynamicX = dynamicX,
        dynamicY = dynamicY,
        dynamicWidth = dynamicWidth,
        buttonType = buttonType,
        disabledClickThrough = disabledClickThrough,
        textureId = textureId,
        spriteTiling = spriteTiling,
        borderThickness = borderThickness,
        verticalFlip = verticalFlip,
        horizontalFlip = horizontalFlip,
        offsetX2d = offsetX2d,
        offsetY2d = offsetY2d,
        orthogonal = orthogonal,
        modelHeight = modelHeight,
        lineWidth = lineWidth,
        lineDirection = lineDirection,
        opBase = opBase,
        actions = actions.toList(),
        dragRender = dragRender,
        dragDeadZone = dragDeadZone,
        dragDeadTime = dragDeadTime,
        loadListener = loadListener.toList(),
        mouseOverListener = mouseOverListener.toList(),
        mouseLeaveListener = mouseLeaveListener.toList(),
        targetEnterListener = targetEnterListener.toList(),
        targetLeaveListener = targetLeaveListener.toList(),
        varTransmitListener = varTransmitListener.toList(),
        invTransmitListener = invTransmitListener.toList(),
        statTransmitListener = statTransmitListener.toList(),
        timerListener = timerListener.toList(),
        opListener = opListener.toList(),
        mouseRepeatListener = mouseRepeatListener.toList(),
        clickStartListener = clickStartListener.toList(),
        clickRepeatListener = clickRepeatListener.toList(),
        clickReleaseListener = clickReleaseListener.toList(),
        holdListener = holdListener.toList(),
        dragStartListener = dragStartListener.toList(),
        dragReleaseListener = dragReleaseListener.toList(),
        scrollWheelListener = scrollWheelListener.toList(),
        varTransmitTrigger = varTransmitTrigger.toList(),
        invTransmitTrigger = invTransmitTrigger.toList(),
        statTransmitTrigger = statTransmitTrigger.toList()
    )

    operator fun plusAssign(other: ComponentType) {
        if (id == DEFAULT_ID) id = other.id
        if (hasScripts == DEFAULT_SCRIPT_HANDLING) hasScripts = other.scripts
        if (type == DEFAULT_TYPE) type = other.type
        if (menuType == DEFAULT_MENU_TYPE) menuType = other.menuType
        if (contentType == DEFAULT_CONTENT_TYPE) contentType = other.contentType
        if (originalX == DEFAULT_X_2D) originalX = other.originalX
        if (originalY == DEFAULT_Y_2D) originalY = other.originalY
        if (originalWidth == DEFAULT_WIDTH_2D) originalWidth = other.originalWidth
        if (originalHeight == DEFAULT_HEIGHT_2D) originalHeight = other.originalHeight
        if (opacity == DEFAULT_OPACITY) opacity = other.opacity
        if (parentId == DEFAULT_PARENT_ID) parentId = other.parentId
        if (hoverSibling == DEFAULT_HOVER_SIBLING) hoverSibling = other.hoverSibling
        if (operationType.contentEquals(EMPTY_INT_ARRAY)) operationType = other.operationType.toIntArray()
        if (operandRhs.contentEquals(EMPTY_INT_ARRAY)) operandRhs = other.operandRhs.toIntArray()
        if (instructionCountCs1 == DEFAULT_CS1_INSTRUCTION_COUNT) {
            instructionCountCs1 = other.instructionCountCs1
        }
        if (scrollHeight == DEFAULT_SCROLL_HEIGHT) scrollHeight = other.scrollHeight
        if (scrollWidth == DEFAULT_SCROLL_WIDTH) scrollWidth = other.scrollWidth
        if (hidden == DEFAULT_HIDDEN_FLAG) hidden = other.hidden
        if (itemId.contentEquals(EMPTY_INT_ARRAY)) itemId = other.itemId.toIntArray()
        if (itemAmount.contentEquals(EMPTY_INT_ARRAY)) itemAmount = other.itemAmount.toIntArray()
        if (clickMask == DEFAULT_CLICK_MASK) clickMask = other.clickMask
        if (pitchX == DEFAULT_PITCH_X) pitchX = other.pitchX
        if (pitchY == DEFAULT_PITCH_Y) pitchY = other.pitchY
        if (offsetX.contentEquals(EMPTY_INT_ARRAY)) offsetX = other.offsetX.toIntArray()
        if (offsetY.contentEquals(EMPTY_INT_ARRAY)) offsetY = other.offsetY.toIntArray()
        if (sprites.contentEquals(EMPTY_INT_ARRAY)) sprites = other.sprites.toIntArray()
        if (configActions.contentEquals(EMPTY_STR_ARRAY)) configActions = other.configActions.toTypedArray()
        if (filled == DEFAULT_FILLED_FLAG) filled = other.filled
        if (textAlignmentX == DEFAULT_TEXT_ALIGNMENT_X) textAlignmentX = other.textAlignmentX
        if (textAlignmentY == DEFAULT_TEXT_ALIGNMENT_Y) textAlignmentY = other.textAlignmentY
        if (lineHeight == DEFAULT_LINE_HEIGHT) lineHeight = other.lineHeight
        if (fontId == DEFAULT_FONT_ID) fontId = other.fontId
        if (shadowedText == DEFAULT_SHADOW_TEXT_FLAG) shadowedText = other.shadowedText
        if (text == EMPTY_STR) text = other.text
        if (altText == EMPTY_STR) altText = other.altText
        if (textColor == DEFAULT_TEXT_COLOR) textColor = other.textColor
        if (altTextColor == DEFAULT_TEXT_COLOR) altTextColor = other.altTextColor
        if (hoverTextColor == DEFAULT_TEXT_COLOR) hoverTextColor = other.hoverTextColor
        if (altHoverTextColor == DEFAULT_TEXT_COLOR) altHoverTextColor = other.altHoverTextColor
        if (spriteId == DEFAULT_SPRITE_ID) spriteId = other.spriteId
        if (spriteId2 == DEFAULT_SPRITE_ID2) spriteId2 = other.spriteId2
        if (altSpriteId == DEFAULT_SPRITE_ID) altSpriteId = other.altSpriteId
        if (modelId == DEFAULT_MODEL_ID) modelId = other.modelId
        if (altModelId == DEFAULT_MODEL_ID) altModelId = other.altModelId
        if (modelType == DEFAULT_MODEL_TYPE) modelType = other.modelType
        if (modelZoom == DEFAULT_MODEL_ZOOM) modelZoom = other.modelZoom
        if (animationId == DEFAULT_ANIMATION_ID) animationId = other.animationId
        if (altAnimationId == DEFAULT_ANIMATION_ID) altAnimationId = other.altAnimationId
        if (rotationX == DEFAULT_ROTATION_X) rotationX = other.rotationX
        if (rotationY == DEFAULT_ROTATION_Y) rotationY = other.rotationY
        if (rotationZ == DEFAULT_ROTATION_Z) rotationZ = other.rotationZ
        if (targetVerb == EMPTY_STR) targetVerb = other.targetVerb
        if (spellName == EMPTY_STR) spellName = other.spellName
        if (tooltip == DEFAULT_TOOLTIP) tooltip = other.tooltip
        if (dynamicX == DEFAULT_X_2D) dynamicX = other.dynamicX
        if (dynamicY == DEFAULT_Y_2D) dynamicY = other.dynamicY
        if (dynamicWidth == DEFAULT_WIDTH_2D) dynamicWidth = other.dynamicWidth
        if (buttonType == DEFAULT_BUTTON_TYPE) buttonType = other.buttonType
        if (disabledClickThrough == DEFAULT_DISABLED_CLICK_THROUGH_FLAG) {
            disabledClickThrough = other.disabledClickThrough
        }
        if (textureId == DEFAULT_TEXTURE_ID) textureId = other.textureId
        if (spriteTiling == DEFAULT_SPRITE_TILING_FLAG) spriteTiling = other.spriteTiling
        if (borderThickness == DEFAULT_BORDER_THICKNESS) borderThickness = other.borderThickness
        if (verticalFlip == DEFAULT_VERTICAL_FLIP_FLAG) verticalFlip = other.verticalFlip
        if (horizontalFlip == DEFAULT_HORIZONTAL_FLIP_FLAG) horizontalFlip = other.horizontalFlip
        if (offsetX2d == DEFAULT_X_2D) offsetX2d = other.offsetX2d
        if (offsetY2d == DEFAULT_Y_2D) offsetY2d = other.offsetY2d
        if (orthogonal == DEFAULT_ORTHOGONAL_FLAG) orthogonal = other.orthogonal
        if (modelHeight == DEFAULT_HEIGHT_2D) modelHeight = other.modelHeight
        if (lineWidth == DEFAULT_WIDTH_2D) lineWidth = other.lineWidth
        if (lineDirection == DEFAULT_LINE_DIRECTION) lineDirection = other.lineDirection
        if (opBase == EMPTY_STR) opBase = other.opBase
        if (actions == DEFAULT_ACTIONS) actions = other.actions.toList()
        if (dragRender == DEFAULT_DRAG_RENDER) dragRender = other.dragRender
        if (dragDeadZone == DEFAULT_DRAG_DEAD_ZONE) dragDeadZone = other.dragDeadZone
        if (dragDeadTime == DEFAULT_DRAG_DEAD_TIME) dragDeadTime = other.dragDeadTime
        if (loadListener == EMPTY_LISTENER) loadListener = other.loadListener
        if (mouseOverListener == EMPTY_LISTENER) mouseOverListener = other.mouseOverListener
        if (mouseLeaveListener == EMPTY_LISTENER) mouseLeaveListener = other.mouseLeaveListener
        if (targetEnterListener == EMPTY_LISTENER) targetEnterListener = other.targetEnterListener
        if (targetLeaveListener == EMPTY_LISTENER) targetLeaveListener = other.targetLeaveListener
        if (varTransmitListener == EMPTY_LISTENER) varTransmitListener = other.varTransmitListener
        if (invTransmitListener == EMPTY_LISTENER) invTransmitListener = other.invTransmitListener
        if (statTransmitListener == EMPTY_LISTENER) statTransmitListener = other.statTransmitListener
        if (timerListener == EMPTY_LISTENER) timerListener = other.timerListener
        if (opListener == EMPTY_LISTENER) opListener = other.opListener
        if (mouseRepeatListener == EMPTY_LISTENER) mouseRepeatListener = other.mouseRepeatListener
        if (clickStartListener == EMPTY_LISTENER) clickStartListener = other.clickStartListener
        if (clickRepeatListener == EMPTY_LISTENER) clickRepeatListener = other.clickRepeatListener
        if (clickReleaseListener == EMPTY_LISTENER) clickReleaseListener = other.clickReleaseListener
        if (holdListener == EMPTY_LISTENER) holdListener = other.holdListener
        if (dragStartListener == EMPTY_LISTENER) dragStartListener = other.dragStartListener
        if (dragReleaseListener == EMPTY_LISTENER) dragReleaseListener = other.dragReleaseListener
        if (scrollWheelListener == EMPTY_LISTENER) scrollWheelListener = other.scrollWheelListener
        if (varTransmitTrigger == EMPTY_TRIGGER) varTransmitTrigger = other.varTransmitTrigger
        if (invTransmitTrigger == EMPTY_TRIGGER) invTransmitTrigger = other.invTransmitTrigger
        if (statTransmitTrigger == EMPTY_TRIGGER) statTransmitTrigger = other.statTransmitTrigger
    }
}
