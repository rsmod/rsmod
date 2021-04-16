package org.rsmod.game.model.item.type

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
    var noteValue: Int = DEFAULT_NOTE_VALUE,
    var placeholderLink: Int = DEFAULT_PLACEHOLDER_LINK,
    var placeholderValue: Int = DEFAULT_PLACEHOLDER_VALUE,
    var boughtLink: Int = DEFAULT_BOUGHT_LINK,
    var boughtValue: Int = DEFAULT_BOUGHT_VALUE,
    var countItem: IntArray = DEFAULT_INT_ARRAY,
    var countCo: IntArray = DEFAULT_INT_ARRAY,
    var parameters: Map<Int, Any> = DEFAULT_PARAMETERS
) {

    val defaultGroundOps: Boolean
        get() = groundOptions === DEFAULT_GROUND_OPTIONS

    val defaultInventoryOps: Boolean
        get() = inventoryOptions === DEFAULT_INVENTORY_OPTIONS

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
            noteValue = noteValue,
            placeholderLink = placeholderLink,
            placeholderValue = placeholderValue,
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
            countCo = countCo.toList()
        )
    }

    operator fun plusAssign(type: ItemType) {
        if (id == DEFAULT_ID) id = type.id
        if (name == DEFAULT_NAME) name = type.name
        if (model == DEFAULT_MODEL) model = type.model
        if (zoom2d == DEFAULT_ZOOM_2D) zoom2d = type.zoom2d
        if (xan2d == DEFAULT_XAN_2D) xan2d = type.xan2d
        if (yan2d == DEFAULT_YAN_2D) yan2d = type.yan2d
        if (zan2d == DEFAULT_ZAN_2D) zan2d = type.zan2d
        if (xOff2d == DEFAULT_X_OFF_2D) xOff2d = type.xoff2d
        if (yOff2d == DEFAULT_Y_OFF_2D) yOff2d = type.yoff2d
        if (stacks == DEFAULT_STACKS) stacks = type.stacks
        if (cost == DEFAULT_COST) cost = type.cost
        if (members == DEFAULT_MEMBERS) members = type.members
        if (maleModelOffset == DEFAULT_MODEL_OFFSET) maleModelOffset = type.maleModelOffset
        if (femaleModelOffset == DEFAULT_MODEL_OFFSET) femaleModelOffset = type.femaleModelOffset
        if (maleModel0 == DEFAULT_MODEL) maleModel0 = type.maleModel0
        if (maleModel1 == DEFAULT_MODEL) maleModel1 = type.maleModel1
        if (maleModel2 == DEFAULT_MODEL) maleModel2 = type.maleModel2
        if (femaleModel0 == DEFAULT_MODEL) femaleModel0 = type.femaleModel0
        if (femaleModel1 == DEFAULT_MODEL) femaleModel1 = type.femaleModel1
        if (femaleModel2 == DEFAULT_MODEL) femaleModel2 = type.femaleModel2
        if (maleHeadModel0 == DEFAULT_MODEL) maleHeadModel0 = type.maleHeadModel0
        if (maleHeadModel1 == DEFAULT_MODEL) maleHeadModel1 = type.maleHeadModel1
        if (femaleHeadModel0 == DEFAULT_MODEL) femaleHeadModel0 = type.femaleHeadModel0
        if (femaleHeadModel1 == DEFAULT_MODEL) femaleHeadModel1 = type.femaleHeadModel1
        if (groundOptions.contentEquals(DEFAULT_GROUND_OPTIONS)) groundOptions = type.groundOptions.toTypedArray()
        if (inventoryOptions.contentEquals(DEFAULT_INVENTORY_OPTIONS)) {
            inventoryOptions = type.inventoryOptions.toTypedArray()
        }
        if (recolorSrc.contentEquals(DEFAULT_INT_ARRAY)) recolorSrc = type.recolorSrc.toIntArray()
        if (recolorDest.contentEquals(DEFAULT_INT_ARRAY)) recolorDest = type.recolorDest.toIntArray()
        if (retextureSrc.contentEquals(DEFAULT_INT_ARRAY)) retextureSrc = type.retextureSrc.toIntArray()
        if (retextureDest.contentEquals(DEFAULT_INT_ARRAY)) retextureDest = type.retextureDest.toIntArray()
        if (dropOptionIndex == DEFAULT_DROP_OPTION_INDEX) dropOptionIndex = type.dropOptionIndex
        if (resizeX == DEFAULT_RESIZE) resizeX = type.resizeX
        if (resizeY == DEFAULT_RESIZE) resizeY = type.resizeY
        if (resizeZ == DEFAULT_RESIZE) resizeZ = type.resizeZ
        if (ambient == DEFAULT_AMBIENT) ambient = type.ambient
        if (contrast == DEFAULT_CONTRAST) contrast = type.contrast
        if (exchangeable == DEFAULT_EXCHANGEABLE) exchangeable = type.exchangeable
        if (teamCape == DEFAULT_TEAM_CAPE) teamCape = type.teamCape
        if (noteLink == DEFAULT_NOTE_LINK) noteLink = type.noteLink
        if (noteValue == DEFAULT_NOTE_VALUE) noteValue = type.noteValue
        if (placeholderLink == DEFAULT_PLACEHOLDER_LINK) placeholderLink = type.placeholderLink
        if (placeholderValue == DEFAULT_PLACEHOLDER_VALUE) placeholderValue = type.placeholderValue
        if (boughtLink == DEFAULT_BOUGHT_LINK) boughtLink = type.boughtLink
        if (boughtValue == DEFAULT_BOUGHT_VALUE) boughtValue = type.boughtValue
        if (countItem.contentEquals(DEFAULT_INT_ARRAY)) countItem = type.countItem.toIntArray()
        if (countCo.contentEquals(DEFAULT_INT_ARRAY)) countCo = type.countCo.toIntArray()
        if (parameters == DEFAULT_PARAMETERS) parameters = type.intParameters + type.strParameters
    }
}
