package org.rsmod.plugins.api.cache.type.item

private const val DEFAULT_ID = -1
private const val DEFAULT_MODEL = 0
private const val DEFAULT_MODEL_OFFSET = 0
private const val DEFAULT_NAME = "null"
private const val DEFAULT_ZOOM_2D = 2000
private const val DEFAULT_XAN_2D = 0
private const val DEFAULT_YAN_2D = 0
private const val DEFAULT_ZAN_2D = 0
private const val DEFAULT_X_OFF_2D = 0
private const val DEFAULT_Y_OFF_2D = 0
private const val DEFAULT_STACKS = false
private const val DEFAULT_COST = 1
private const val DEFAULT_MEMBERS = false
private const val DEFAULT_EXCHANGEABLE = false
private const val DEFAULT_TEAM_CAPE = 0
private const val DEFAULT_NOTE_LINK = 0
private const val DEFAULT_NOTE_VALUE = 0
private const val DEFAULT_PLACEHOLDER_LINK = 0
private const val DEFAULT_PLACEHOLDER_VALUE = 0
private const val DEFAULT_DROP_OPTION_INDEX = -2
private const val DEFAULT_RESIZE = 128
private const val DEFAULT_AMBIENT = 0
private const val DEFAULT_CONTRAST = 0
private const val DEFAULT_BOUGHT_LINK = 0
private const val DEFAULT_BOUGHT_VALUE = 0
private const val DEFAULT_WEIGHT = 0
private const val DEFAULT_WEAR_POS = -1

private val DEFAULT_CATEGORIES = emptySet<Int>()
private val DEFAULT_GROUND_OPTIONS = arrayOf(null, null, "Take", null, null)
private val DEFAULT_INVENTORY_OPTIONS = arrayOf(null, null, null, null, "Drop")
private val DEFAULT_INT_ARRAY = IntArray(0)
private val DEFAULT_PARAMETERS = emptyMap<Int, Any>()

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class ItemTypeBuilder(
    var id: Int = DEFAULT_ID,
    var name: String = DEFAULT_NAME,
    var model: Int = DEFAULT_MODEL,
    var zoom2d: Int = DEFAULT_ZOOM_2D,
    var xan2d: Int = DEFAULT_XAN_2D,
    var yan2d: Int = DEFAULT_YAN_2D,
    var zan2d: Int = DEFAULT_ZAN_2D,
    var xOff2d: Int = DEFAULT_X_OFF_2D,
    var yOff2d: Int = DEFAULT_Y_OFF_2D,
    var stacks: Boolean = DEFAULT_STACKS,
    var cost: Int = DEFAULT_COST,
    var members: Boolean = DEFAULT_MEMBERS,
    var maleModelOffset: Int = DEFAULT_MODEL_OFFSET,
    var femaleModelOffset: Int = DEFAULT_MODEL_OFFSET,
    var maleModel0: Int = DEFAULT_MODEL,
    var maleModel1: Int = DEFAULT_MODEL,
    var maleModel2: Int = DEFAULT_MODEL,
    var femaleModel0: Int = DEFAULT_MODEL,
    var femaleModel1: Int = DEFAULT_MODEL,
    var femaleModel2: Int = DEFAULT_MODEL,
    var maleHeadModel0: Int = DEFAULT_MODEL,
    var maleHeadModel1: Int = DEFAULT_MODEL,
    var femaleHeadModel0: Int = DEFAULT_MODEL,
    var femaleHeadModel1: Int = DEFAULT_MODEL,
    var categories: Set<Int> = DEFAULT_CATEGORIES,
    var groundOptions: Array<String?> = DEFAULT_GROUND_OPTIONS,
    var inventoryOptions: Array<String?> = DEFAULT_INVENTORY_OPTIONS,
    var recolorSrc: IntArray = DEFAULT_INT_ARRAY,
    var recolorDest: IntArray = DEFAULT_INT_ARRAY,
    var retextureSrc: IntArray = DEFAULT_INT_ARRAY,
    var retextureDest: IntArray = DEFAULT_INT_ARRAY,
    var dropOptionIndex: Int = DEFAULT_DROP_OPTION_INDEX,
    var resizeX: Int = DEFAULT_RESIZE,
    var resizeY: Int = DEFAULT_RESIZE,
    var resizeZ: Int = DEFAULT_RESIZE,
    var ambient: Int = DEFAULT_AMBIENT,
    var contrast: Int = DEFAULT_CONTRAST,
    var exchangeable: Boolean = DEFAULT_EXCHANGEABLE,
    var teamCape: Int = DEFAULT_TEAM_CAPE,
    var noteLink: Int = DEFAULT_NOTE_LINK,
    var noteModel: Int = DEFAULT_NOTE_VALUE,
    var placeholderLink: Int = DEFAULT_PLACEHOLDER_LINK,
    var placeholderModel: Int = DEFAULT_PLACEHOLDER_VALUE,
    var boughtLink: Int = DEFAULT_BOUGHT_LINK,
    var boughtValue: Int = DEFAULT_BOUGHT_VALUE,
    var countItem: IntArray = DEFAULT_INT_ARRAY,
    var countCo: IntArray = DEFAULT_INT_ARRAY,
    var parameters: Map<Int, Any> = DEFAULT_PARAMETERS,
    var weight: Int = DEFAULT_WEIGHT,
    var wearPos1: Int = DEFAULT_WEAR_POS,
    var wearPos2: Int = DEFAULT_WEAR_POS,
    var wearPos3: Int = DEFAULT_WEAR_POS
) {

    val defaultGroundOps: Boolean
        get() = groundOptions === DEFAULT_GROUND_OPTIONS

    val defaultInventoryOps: Boolean
        get() = inventoryOptions === DEFAULT_INVENTORY_OPTIONS

    val defaultCategories: Boolean
        get() = categories === DEFAULT_CATEGORIES

    fun build(): ItemType {
        check(id != DEFAULT_ID) { "Item type id has not been set." }
        return ItemType(
            id = id,
            name = name,
            stacks = stacks,
            cost = cost,
            members = members,
            groundOptions = groundOptions.toList(),
            inventoryOptions = inventoryOptions.toList(),
            exchangeable = exchangeable,
            teamCape = teamCape,
            noteLink = noteLink,
            noteModel = noteModel,
            placeholderLink = placeholderLink,
            placeholderModel = placeholderModel,
            intParameters = parameters.filter { it.value is Int }.mapValues { it.value as Int },
            strParameters = parameters.filter { it.value is String }.mapValues { it.value as String },
            model = model,
            zoom2d = zoom2d,
            xan2d = xan2d,
            yan2d = yan2d,
            zan2d = zan2d,
            xoff2d = xOff2d,
            yoff2d = yOff2d,
            maleModelOffset = maleModelOffset,
            femaleModelOffset = femaleModelOffset,
            maleModel0 = maleModel0,
            maleModel1 = maleModel1,
            maleModel2 = maleModel2,
            femaleModel0 = femaleModel0,
            femaleModel1 = femaleModel1,
            femaleModel2 = femaleModel2,
            maleHeadModel0 = maleHeadModel0,
            maleHeadModel1 = maleHeadModel1,
            femaleHeadModel0 = femaleHeadModel0,
            femaleHeadModel1 = femaleHeadModel1,
            categories = categories,
            recolorSrc = recolorSrc.toList(),
            recolorDest = recolorDest.toList(),
            retextureSrc = retextureSrc.toList(),
            retextureDest = retextureDest.toList(),
            dropOptionIndex = dropOptionIndex,
            resizeX = resizeX,
            resizeY = resizeY,
            resizeZ = resizeZ,
            ambient = ambient,
            contrast = contrast,
            boughtLink = boughtLink,
            boughtValue = boughtValue,
            countItem = countItem.toList(),
            countCo = countCo.toList(),
            weight = weight,
            wearPos1 = wearPos1,
            wearPos2 = wearPos2,
            wearPos3 = wearPos3
        )
    }

    operator fun plusAssign(other: ItemType) {
        if (id == DEFAULT_ID) id = other.id
        if (name == DEFAULT_NAME) name = other.name
        if (model == DEFAULT_MODEL) model = other.model
        if (zoom2d == DEFAULT_ZOOM_2D) zoom2d = other.zoom2d
        if (xan2d == DEFAULT_XAN_2D) xan2d = other.xan2d
        if (yan2d == DEFAULT_YAN_2D) yan2d = other.yan2d
        if (zan2d == DEFAULT_ZAN_2D) zan2d = other.zan2d
        if (xOff2d == DEFAULT_X_OFF_2D) xOff2d = other.xoff2d
        if (yOff2d == DEFAULT_Y_OFF_2D) yOff2d = other.yoff2d
        if (stacks == DEFAULT_STACKS) stacks = other.stacks
        if (cost == DEFAULT_COST) cost = other.cost
        if (members == DEFAULT_MEMBERS) members = other.members
        if (maleModelOffset == DEFAULT_MODEL_OFFSET) maleModelOffset = other.maleModelOffset
        if (femaleModelOffset == DEFAULT_MODEL_OFFSET) femaleModelOffset = other.femaleModelOffset
        if (maleModel0 == DEFAULT_MODEL) maleModel0 = other.maleModel0
        if (maleModel1 == DEFAULT_MODEL) maleModel1 = other.maleModel1
        if (maleModel2 == DEFAULT_MODEL) maleModel2 = other.maleModel2
        if (femaleModel0 == DEFAULT_MODEL) femaleModel0 = other.femaleModel0
        if (femaleModel1 == DEFAULT_MODEL) femaleModel1 = other.femaleModel1
        if (femaleModel2 == DEFAULT_MODEL) femaleModel2 = other.femaleModel2
        if (maleHeadModel0 == DEFAULT_MODEL) maleHeadModel0 = other.maleHeadModel0
        if (maleHeadModel1 == DEFAULT_MODEL) maleHeadModel1 = other.maleHeadModel1
        if (femaleHeadModel0 == DEFAULT_MODEL) femaleHeadModel0 = other.femaleHeadModel0
        if (femaleHeadModel1 == DEFAULT_MODEL) femaleHeadModel1 = other.femaleHeadModel1
        if (categories == DEFAULT_CATEGORIES) categories = other.categories
        if (groundOptions.contentEquals(DEFAULT_GROUND_OPTIONS)) groundOptions = other.groundOptions.toTypedArray()
        if (inventoryOptions.contentEquals(DEFAULT_INVENTORY_OPTIONS)) {
            inventoryOptions = other.inventoryOptions.toTypedArray()
        }
        if (recolorSrc.contentEquals(DEFAULT_INT_ARRAY)) recolorSrc = other.recolorSrc.toIntArray()
        if (recolorDest.contentEquals(DEFAULT_INT_ARRAY)) recolorDest = other.recolorDest.toIntArray()
        if (retextureSrc.contentEquals(DEFAULT_INT_ARRAY)) retextureSrc = other.retextureSrc.toIntArray()
        if (retextureDest.contentEquals(DEFAULT_INT_ARRAY)) retextureDest = other.retextureDest.toIntArray()
        if (dropOptionIndex == DEFAULT_DROP_OPTION_INDEX) dropOptionIndex = other.dropOptionIndex
        if (resizeX == DEFAULT_RESIZE) resizeX = other.resizeX
        if (resizeY == DEFAULT_RESIZE) resizeY = other.resizeY
        if (resizeZ == DEFAULT_RESIZE) resizeZ = other.resizeZ
        if (ambient == DEFAULT_AMBIENT) ambient = other.ambient
        if (contrast == DEFAULT_CONTRAST) contrast = other.contrast
        if (exchangeable == DEFAULT_EXCHANGEABLE) exchangeable = other.exchangeable
        if (teamCape == DEFAULT_TEAM_CAPE) teamCape = other.teamCape
        if (noteLink == DEFAULT_NOTE_LINK) noteLink = other.noteLink
        if (noteModel == DEFAULT_NOTE_VALUE) noteModel = other.noteModel
        if (placeholderLink == DEFAULT_PLACEHOLDER_LINK) placeholderLink = other.placeholderLink
        if (placeholderModel == DEFAULT_PLACEHOLDER_VALUE) placeholderModel = other.placeholderModel
        if (boughtLink == DEFAULT_BOUGHT_LINK) boughtLink = other.boughtLink
        if (boughtValue == DEFAULT_BOUGHT_VALUE) boughtValue = other.boughtValue
        if (countItem.contentEquals(DEFAULT_INT_ARRAY)) countItem = other.countItem.toIntArray()
        if (countCo.contentEquals(DEFAULT_INT_ARRAY)) countCo = other.countCo.toIntArray()
        if (parameters == DEFAULT_PARAMETERS) parameters = other.intParameters + other.strParameters
        if (weight == DEFAULT_WEIGHT) weight = other.weight
        if (wearPos1 == DEFAULT_WEAR_POS) wearPos1 = other.wearPos1
        if (wearPos2 == DEFAULT_WEAR_POS) wearPos2 = other.wearPos2
        if (wearPos3 == DEFAULT_WEAR_POS) wearPos3 = other.wearPos3
    }
}
