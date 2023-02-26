package org.rsmod.plugins.api.cache.type.obj

import org.rsmod.plugins.api.cache.type.param.ParamMap

private const val DEFAULT_ID = -1
private const val DEFAULT_NAME = "null"
private const val DEFAULT_HEIGHT = 1
private const val DEFAULT_WIDTH = 1
private const val DEFAULT_BLOCK_PATH = true
private const val DEFAULT_BLOCK_PROJ = true
private const val DEFAULT_INTERACT_TYPE = -1
private const val DEFAULT_OBSTRUCT = false
private const val DEFAULT_CLIP_TYPE = 2
private const val DEFAULT_CLIP_MASK = 0
private const val DEFAULT_VARP = -1
private const val DEFAULT_VARBIT = -1
private const val DEFAULT_ANIMATION = -1
private const val DEFAULT_ROTATED = false
private const val DEFAULT_TRANSFORM = -1
private const val DEFAULT_CONTOURED_GROUND = -1
private const val DEFAULT_NON_FLAT_SHADING = false
private const val DEFAULT_CLIPPED_MODEL = false
private const val DEFAULT_DECOR_DISPLACEMENT = 16
private const val DEFAULT_AMBIENT = 0
private const val DEFAULT_CONTRAST = 0
private const val DEFAULT_CLIPPED = true
private const val DEFAULT_MODEL_SIZE = 128
private const val DEFAULT_MAP_SCENE_ID = -1
private const val DEFAULT_OFFSET = 0
private const val DEFAULT_HOLLOW_FLAG = false
private const val DEFAULT_SUPPORT_ITEM = -1
private const val DEFAULT_AMBIENT_SOUND = -1
private const val DEFAULT_AMBIENT_UNKNOWN_VALUE = 0
private const val DEFAULT_MINIMAP_ICON = -1
private const val DEFAULT_UNKNOWN_INT = 0
private const val DEFAULT_UNKNOWN_BOOL_TRUE = true
private const val DEFAULT_CATEGORY = 0

private val DEFAULT_OPTIONS = emptyArray<String?>()
private val DEFAULT_TRANSFORMS = emptyArray<Int>()
private val DEFAULT_MODELS = emptyArray<Int>()
private val DEFAULT_MODEL_TYPES = emptyArray<Int>()
private val DEFAULT_RECOLOR_SRC = emptyArray<Int>()
private val DEFAULT_RECOLOR_DEST = emptyArray<Int>()
private val DEFAULT_RETEXTURE_SRC = emptyArray<Int>()
private val DEFAULT_RETEXTURE_DEST = emptyArray<Int>()
private val DEFAULT_UNKNOWN_INT_ARRAY = emptyArray<Int>()
private val DEFAULT_PARAMETERS = emptyMap<Int, Any>()

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
public class ObjectTypeBuilder(
    public var id: Int = DEFAULT_ID,
    public var name: String = DEFAULT_NAME,
    public var height: Int = DEFAULT_HEIGHT,
    public var width: Int = DEFAULT_WIDTH,
    public var blockPath: Boolean = DEFAULT_BLOCK_PATH,
    public var blockProjectile: Boolean = DEFAULT_BLOCK_PROJ,
    public var interactType: Int = DEFAULT_INTERACT_TYPE,
    public var obstruct: Boolean = DEFAULT_OBSTRUCT,
    public var clipType: Int = DEFAULT_CLIP_TYPE,
    public var clipMask: Int = DEFAULT_CLIP_MASK,
    public var varp: Int = DEFAULT_VARP,
    public var varbit: Int = DEFAULT_VARBIT,
    public var animation: Int = DEFAULT_ANIMATION,
    public var category: Int = DEFAULT_CATEGORY,
    public var rotated: Boolean = DEFAULT_ROTATED,
    public var options: Array<String?> = DEFAULT_OPTIONS,
    public var transforms: Array<Int> = DEFAULT_TRANSFORMS,
    public var defaultTransform: Int = DEFAULT_TRANSFORM,
    public var models: Array<Int> = DEFAULT_MODELS,
    public var modelTypes: Array<Int> = DEFAULT_MODEL_TYPES,
    public var contouredGround: Int = DEFAULT_CONTOURED_GROUND,
    public var nonFlatShading: Boolean = DEFAULT_NON_FLAT_SHADING,
    public var clippedModel: Boolean = DEFAULT_CLIPPED_MODEL,
    public var decorDisplacement: Int = DEFAULT_DECOR_DISPLACEMENT,
    public var ambient: Int = DEFAULT_AMBIENT,
    public var contrast: Int = DEFAULT_CONTRAST,
    public var recolorSrc: Array<Int> = DEFAULT_RECOLOR_SRC,
    public var recolorDest: Array<Int> = DEFAULT_RECOLOR_DEST,
    public var retextureSrc: Array<Int> = DEFAULT_RETEXTURE_SRC,
    public var retextureDest: Array<Int> = DEFAULT_RETEXTURE_DEST,
    public var clipped: Boolean = DEFAULT_CLIPPED,
    public var resizeX: Int = DEFAULT_MODEL_SIZE,
    public var resizeHeight: Int = DEFAULT_MODEL_SIZE,
    public var resizeY: Int = DEFAULT_MODEL_SIZE,
    public var mapSceneId: Int = DEFAULT_MAP_SCENE_ID,
    public var offsetX: Int = DEFAULT_OFFSET,
    public var offsetHeight: Int = DEFAULT_OFFSET,
    public var offsetY: Int = DEFAULT_OFFSET,
    public var hollow: Boolean = DEFAULT_HOLLOW_FLAG,
    public var supportItems: Int = DEFAULT_SUPPORT_ITEM,
    public var ambientSoundId: Int = DEFAULT_AMBIENT_SOUND,
    public var ambientSoundRadius: Int = DEFAULT_AMBIENT_UNKNOWN_VALUE,
    public var anInt3426: Int = DEFAULT_UNKNOWN_INT,
    public var anInt3427: Int = DEFAULT_UNKNOWN_INT,
    public var aBoolean3429: Boolean = DEFAULT_UNKNOWN_BOOL_TRUE,
    public var anIntArray3428: Array<Int> = DEFAULT_UNKNOWN_INT_ARRAY,
    public var mapIconId: Int = DEFAULT_MINIMAP_ICON,
    public var params: ParamMap? = null
) {

    public val defaultOptions: Boolean
        get() = options === DEFAULT_OPTIONS

    public fun build(): ObjectType {
        check(id != DEFAULT_ID) { "Object type id has not been set." }
        return ObjectType(
            id = id,
            name = name,
            height = height,
            width = width,
            blockPath = blockPath,
            blockProjectile = blockProjectile,
            interactType = interactType,
            obstruct = obstruct,
            clipType = clipType,
            clipMask = clipMask,
            varp = varp,
            varbit = varbit,
            animation = animation,
            category = category,
            rotated = rotated,
            options = options.toList(),
            transforms = transforms.toList(),
            defaultTransform = defaultTransform,
            models = models.toList(),
            modelTypes = modelTypes.toList(),
            contouredGround = contouredGround,
            nonFlatShading = nonFlatShading,
            clippedModel = clippedModel,
            decorDisplacement = decorDisplacement,
            ambient = ambient,
            contrast = contrast,
            recolorSrc = recolorSrc.toList(),
            recolorDest = recolorDest.toList(),
            retextureSrc = retextureSrc.toList(),
            retextureDest = retextureDest.toList(),
            clipped = clipped,
            resizeX = resizeX,
            resizeHeight = resizeHeight,
            resizeY = resizeY,
            mapSceneId = mapSceneId,
            offsetX = offsetX,
            offsetHeight = offsetHeight,
            offsetY = offsetY,
            hollow = hollow,
            supportItems = supportItems,
            ambientSoundId = ambientSoundId,
            ambientSoundRadius = ambientSoundRadius,
            anInt3426 = anInt3426,
            anInt3427 = anInt3427,
            aBoolean3429 = aBoolean3429,
            anIntArray3428 = anIntArray3428.toList(),
            mapIconId = mapIconId,
            params = params
        )
    }

    public operator fun plusAssign(other: ObjectType) {
        if (id == DEFAULT_ID) id = other.id
        if (name == DEFAULT_NAME) name = other.name
        if (height == DEFAULT_HEIGHT) height = other.height
        if (width == DEFAULT_WIDTH) width = other.width
        if (blockPath == DEFAULT_BLOCK_PATH) blockPath = other.blockPath
        if (blockProjectile == DEFAULT_BLOCK_PROJ) blockProjectile = other.blockProjectile
        if (interactType == DEFAULT_INTERACT_TYPE) interactType = other.interactType
        if (obstruct == DEFAULT_OBSTRUCT) obstruct = other.obstruct
        if (clipType == DEFAULT_CLIP_TYPE) clipType = other.clipType
        if (clipMask == DEFAULT_CLIP_MASK) clipMask = other.clipMask
        if (varp == DEFAULT_VARP) varp = other.varp
        if (varbit == DEFAULT_VARBIT) varbit = other.varbit
        if (animation == DEFAULT_ANIMATION) animation = other.animation
        if (category == DEFAULT_CATEGORY) category = other.category
        if (rotated == DEFAULT_ROTATED) rotated = other.rotated
        if (options.contentEquals(DEFAULT_OPTIONS)) options = other.options.toTypedArray()
        if (transforms.contentEquals(DEFAULT_TRANSFORMS)) transforms = other.transforms.toTypedArray()
        if (defaultTransform == DEFAULT_TRANSFORM) defaultTransform = other.defaultTransform
        if (models.contentEquals(DEFAULT_MODELS)) models = other.models.toTypedArray()
        if (modelTypes.contentEquals(DEFAULT_MODEL_TYPES)) modelTypes = other.modelTypes.toTypedArray()
        if (contouredGround == DEFAULT_CONTOURED_GROUND) contouredGround = other.contouredGround
        if (nonFlatShading == DEFAULT_NON_FLAT_SHADING) nonFlatShading = other.nonFlatShading
        if (clippedModel == DEFAULT_CLIPPED_MODEL) clippedModel = other.clippedModel
        if (decorDisplacement == DEFAULT_DECOR_DISPLACEMENT) decorDisplacement = other.decorDisplacement
        if (ambient == DEFAULT_AMBIENT) ambient = other.ambient
        if (contrast == DEFAULT_CONTRAST) contrast = other.contrast
        if (recolorSrc.contentEquals(DEFAULT_RECOLOR_SRC)) recolorSrc = other.recolorSrc.toTypedArray()
        if (recolorDest.contentEquals(DEFAULT_RECOLOR_DEST)) recolorDest = other.recolorDest.toTypedArray()
        if (retextureSrc.contentEquals(DEFAULT_RETEXTURE_SRC)) retextureSrc = other.retextureSrc.toTypedArray()
        if (retextureDest.contentEquals(DEFAULT_RETEXTURE_DEST)) retextureDest = other.retextureDest.toTypedArray()
        if (clipped == DEFAULT_CLIPPED) clipped = other.clipped
        if (resizeX == DEFAULT_MODEL_SIZE) resizeX = other.resizeX
        if (resizeHeight == DEFAULT_MODEL_SIZE) resizeHeight = other.resizeHeight
        if (resizeY == DEFAULT_MODEL_SIZE) resizeY = other.resizeY
        if (mapSceneId == DEFAULT_MAP_SCENE_ID) mapSceneId = other.mapSceneId
        if (offsetX == DEFAULT_OFFSET) offsetX = other.offsetX
        if (offsetHeight == DEFAULT_OFFSET) offsetHeight = other.offsetHeight
        if (offsetY == DEFAULT_OFFSET) offsetY = other.offsetY
        if (hollow == DEFAULT_HOLLOW_FLAG) hollow = other.hollow
        if (supportItems == DEFAULT_SUPPORT_ITEM) supportItems = other.supportItems
        if (ambientSoundId == DEFAULT_AMBIENT_SOUND) ambientSoundId = other.ambientSoundId
        if (ambientSoundRadius == DEFAULT_AMBIENT_UNKNOWN_VALUE) ambientSoundRadius = other.ambientSoundRadius
        if (anInt3426 == DEFAULT_UNKNOWN_INT) anInt3426 = other.anInt3426
        if (anInt3427 == DEFAULT_UNKNOWN_INT) anInt3427 = other.anInt3427
        if (aBoolean3429 == DEFAULT_UNKNOWN_BOOL_TRUE) aBoolean3429 = other.aBoolean3429
        if (anIntArray3428.contentEquals(DEFAULT_UNKNOWN_INT_ARRAY)) {
            anIntArray3428 = other.anIntArray3428.toTypedArray()
        }
        if (mapIconId == DEFAULT_MINIMAP_ICON) mapIconId = other.mapIconId
        if (params == null) params = other.params?.let { ParamMap(it) }
    }
}
