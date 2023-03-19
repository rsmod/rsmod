package org.rsmod.plugins.api.cache.type.npc

import org.rsmod.plugins.api.cache.type.param.ParamMap

private const val DEFAULT_ID = -1
private const val DEFAULT_NAME = "null"
private const val DEFAULT_SIZE = 1
private const val DEFAULT_LEVEL = -1
private const val DEFAULT_INTERACT = true
private const val DEFAULT_MINIMAP_VISIBLE = true
private const val DEFAULT_CLICKABLE = true
private const val DEFAULT_ROTATION = 32
private const val DEFAULT_ANIMATION = -1
private const val DEFAULT_RESIZE = 128
private const val DEFAULT_CONTRAST = 0
private const val DEFAULT_AMBIENT = 0
private const val DEFAULT_RENDER_PRIORITY = false
private const val DEFAULT_VARP = -1
private const val DEFAULT_VARBIT = -1
private const val DEFAULT_TRANSFORM = -1
private const val DEFAULT_CATEGORY = -1

private val DEFAULT_OPTIONS = emptyArray<String?>()
private val DEFAULT_TRANSFORMS = emptyArray<Int>()
private val DEFAULT_MODELS = emptyArray<Int>()
private val DEFAULT_HEAD_MODELS = emptyArray<Int>()
private val DEFAULT_RECOLOR_SRC = emptyArray<Int>()
private val DEFAULT_RECOLOR_DEST = emptyArray<Int>()
private val DEFAULT_RETEXTURE_SRC = emptyArray<Int>()
private val DEFAULT_RETEXTURE_DEST = emptyArray<Int>()
private val DEFAULT_HEAD_ICONS = emptyArray<Int>()

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
public class NpcTypeBuilder(
    public var id: Int = DEFAULT_ID,
    public var name: String = DEFAULT_NAME,
    public var size: Int = DEFAULT_SIZE,
    public var level: Int = DEFAULT_LEVEL,
    public var category: Int = DEFAULT_CATEGORY,
    public var interact: Boolean = DEFAULT_INTERACT,
    public var minimapVisible: Boolean = DEFAULT_MINIMAP_VISIBLE,
    public var clickable: Boolean = DEFAULT_CLICKABLE,
    public var renderPriority: Boolean = DEFAULT_RENDER_PRIORITY,
    public var options: Array<String?> = DEFAULT_OPTIONS,
    public var readyAnim: Int = DEFAULT_ANIMATION,
    public var walkAnim: Int = DEFAULT_ANIMATION,
    public var walkLeftAnim: Int = DEFAULT_ANIMATION,
    public var walkRightAnim: Int = DEFAULT_ANIMATION,
    public var walkBackAnim: Int = DEFAULT_ANIMATION,
    public var turnLeftAnim: Int = DEFAULT_ANIMATION,
    public var turnRightAnim: Int = DEFAULT_ANIMATION,
    public var varp: Int = DEFAULT_VARP,
    public var varbit: Int = DEFAULT_VARBIT,
    public var transforms: Array<Int> = DEFAULT_TRANSFORMS,
    public var defaultTransform: Int = DEFAULT_TRANSFORM,
    public var rotation: Int = DEFAULT_ROTATION,
    public var headIconGroups: Array<Int> = DEFAULT_HEAD_ICONS,
    public var headIconIndexes: Array<Int> = DEFAULT_HEAD_ICONS,
    public var models: Array<Int> = DEFAULT_MODELS,
    public var headModels: Array<Int> = DEFAULT_HEAD_MODELS,
    public var recolorSrc: Array<Int> = DEFAULT_RECOLOR_SRC,
    public var recolorDest: Array<Int> = DEFAULT_RECOLOR_DEST,
    public var retextureSrc: Array<Int> = DEFAULT_RETEXTURE_SRC,
    public var retextureDest: Array<Int> = DEFAULT_RETEXTURE_DEST,
    public var resizeX: Int = DEFAULT_RESIZE,
    public var resizeY: Int = DEFAULT_RESIZE,
    public var contrast: Int = DEFAULT_CONTRAST,
    public var ambient: Int = DEFAULT_AMBIENT,
    public var isPet: Boolean = false,
    public var params: ParamMap? = null
) {

    public val defaultOptions: Boolean get() = options === DEFAULT_OPTIONS

    public fun build(): NpcType {
        check(id != DEFAULT_ID) { "Npc type id has not been set." }
        return NpcType(
            id = id,
            name = name,
            size = size,
            level = level,
            category = category,
            interact = interact,
            minimapVisible = minimapVisible,
            clickable = clickable,
            renderPriority = renderPriority,
            options = options.toList(),
            readyAnim = readyAnim,
            walkAnim = walkAnim,
            walkLeftAnim = walkLeftAnim,
            walkRightAnim = walkRightAnim,
            walkBackAnim = walkBackAnim,
            turnLeftAnim = turnLeftAnim,
            turnRightAnim = turnRightAnim,
            varp = varp,
            varbit = varbit,
            transforms = transforms.toList(),
            defaultTransform = defaultTransform,
            rotation = rotation,
            headIconGroups = headIconGroups.toList(),
            headIconIndexes = headIconIndexes.toList(),
            models = models.toList(),
            headModels = headModels.toList(),
            recolorSrc = recolorSrc.toList(),
            recolorDest = recolorDest.toList(),
            retextureSrc = retextureSrc.toList(),
            retextureDest = retextureDest.toList(),
            resizeX = resizeX,
            resizeY = resizeY,
            contrast = contrast,
            ambient = ambient,
            isPet = isPet,
            params = params
        )
    }

    public operator fun plusAssign(other: NpcType) {
        if (id == DEFAULT_ID) id = other.id
        if (name == DEFAULT_NAME) name = other.name
        if (size == DEFAULT_SIZE) size = other.size
        if (level == DEFAULT_LEVEL) level = other.level
        if (category == DEFAULT_CATEGORY) category = other.category
        if (interact == DEFAULT_INTERACT) interact = other.interact
        if (minimapVisible == DEFAULT_MINIMAP_VISIBLE) minimapVisible = other.minimapVisible
        if (clickable == DEFAULT_CLICKABLE) clickable = other.clickable
        if (renderPriority == DEFAULT_RENDER_PRIORITY) renderPriority = other.renderPriority
        if (options.contentEquals(DEFAULT_OPTIONS)) options = other.options.toTypedArray()
        if (readyAnim == DEFAULT_ANIMATION) readyAnim = other.readyAnim
        if (walkAnim == DEFAULT_ANIMATION) walkAnim = other.walkAnim
        if (walkLeftAnim == DEFAULT_ANIMATION) walkLeftAnim = other.walkLeftAnim
        if (walkRightAnim == DEFAULT_ANIMATION) walkRightAnim = other.walkRightAnim
        if (walkBackAnim == DEFAULT_ANIMATION) walkBackAnim = other.walkBackAnim
        if (turnLeftAnim == DEFAULT_ANIMATION) turnLeftAnim = other.turnLeftAnim
        if (turnRightAnim == DEFAULT_ANIMATION) turnRightAnim = other.turnRightAnim
        if (varp == DEFAULT_VARP) varp = other.varp
        if (varbit == DEFAULT_VARBIT) varbit = other.varbit
        if (transforms.contentEquals(DEFAULT_TRANSFORMS)) transforms = other.transforms.toTypedArray()
        if (defaultTransform == DEFAULT_TRANSFORM) defaultTransform = other.defaultTransform
        if (rotation == DEFAULT_ROTATION) rotation = other.rotation
        if (headIconGroups.contentEquals(DEFAULT_HEAD_ICONS)) headIconGroups = other.headIconGroups.toTypedArray()
        if (headIconIndexes.contentEquals(DEFAULT_HEAD_ICONS)) headIconIndexes = other.headIconIndexes.toTypedArray()
        if (models.contentEquals(DEFAULT_MODELS)) models = other.models.toTypedArray()
        if (headModels.contentEquals(DEFAULT_HEAD_MODELS)) headModels = other.headModels.toTypedArray()
        if (recolorSrc.contentEquals(DEFAULT_RECOLOR_SRC)) recolorSrc = other.recolorSrc.toTypedArray()
        if (recolorDest.contentEquals(DEFAULT_RECOLOR_DEST)) recolorDest = other.recolorDest.toTypedArray()
        if (retextureSrc.contentEquals(DEFAULT_RETEXTURE_SRC)) retextureSrc = other.retextureSrc.toTypedArray()
        if (retextureDest.contentEquals(DEFAULT_RETEXTURE_DEST)) retextureDest = other.retextureDest.toTypedArray()
        if (resizeX == DEFAULT_RESIZE) resizeX = other.resizeX
        if (resizeY == DEFAULT_RESIZE) resizeY = other.resizeY
        if (contrast == DEFAULT_CONTRAST) contrast = other.contrast
        if (ambient == DEFAULT_AMBIENT) ambient = other.ambient
        if (!isPet) isPet = other.isPet
        if (params == null) params = other.params?.let { ParamMap(it) }
    }
}
