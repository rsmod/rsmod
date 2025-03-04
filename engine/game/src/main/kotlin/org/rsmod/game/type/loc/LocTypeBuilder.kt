package org.rsmod.game.type.loc

import org.rsmod.game.type.util.CompactableIntArray
import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.GenericPropertySelector.selectByteArray
import org.rsmod.game.type.util.GenericPropertySelector.selectIntArray
import org.rsmod.game.type.util.GenericPropertySelector.selectParamMap
import org.rsmod.game.type.util.GenericPropertySelector.selectPredicate
import org.rsmod.game.type.util.GenericPropertySelector.selectShortArray
import org.rsmod.game.type.util.MergeableCacheBuilder
import org.rsmod.game.type.util.ParamMap

@DslMarker private annotation class LocBuilderDsl

@LocBuilderDsl
public class LocTypeBuilder(public var internal: String? = null) {
    public var model: CompactableIntArray = CompactableIntArray()
    public var modelShape: CompactableIntArray = CompactableIntArray()
    public var name: String? = null
    public var desc: String? = null
    public var width: Int? = null
    public var length: Int? = null
    public var blockWalk: Int? = null
    public var blockRange: Boolean? = null
    public var active: Int? = null
    public var hillSkew: Int? = null
    public var shareLight: Boolean? = null
    public var occlude: Boolean? = null
    public var anim: Int? = null
    public var hasAlpha: Boolean? = null
    public var wallWidth: Int? = null
    public var ambient: Int? = null
    public var contrast: Int? = null
    public val op: Array<String?> = arrayOfNulls(OP_CAPACITY)
    public var recolS: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var recolD: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var retexS: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var retexD: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var category: Int? = null
    public var mirror: Boolean? = null
    public var shadow: Boolean? = null
    public var resizeX: Int? = null
    public var resizeY: Int? = null
    public var resizeZ: Int? = null
    public var mapscene: Int? = null
    public var forceApproachFlags: Int? = null
    public var offsetX: Int? = null
    public var offsetY: Int? = null
    public var offsetZ: Int? = null
    public var forceDecor: Boolean? = null
    public var breakRouteFinding: Boolean? = null
    public var raiseObject: Int? = null
    public var multiVarBit: Int? = null
    public var multiVarp: Int? = null
    public var multiLocDefault: Int? = null
    public var multiLoc: CompactableIntArray = CompactableIntArray()
    public var bgsoundSound: Int? = null
    public var bgsoundRange: Int? = null
    public var bgsoundSize: Int? = null
    public var bgsoundMinDelay: Int? = null
    public var bgsoundMaxDelay: Int? = null
    public var bgsoundRandomSound: CompactableIntArray = CompactableIntArray()
    public var treeSkew: Int? = null
    public var mapIcon: Int? = null
    public var randomAnimFrame: Boolean? = null
    public var fixLocAnimAfterLocChange: Boolean? = null
    public var paramMap: ParamMap? = null
    public var contentGroup: Int? = null

    public fun build(id: Int): UnpackedLocType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val name = name ?: DEFAULT_NAME
        val desc = desc ?: ""
        val width = width ?: DEFAULT_WIDTH
        val length = length ?: DEFAULT_LENGTH
        val blockWalk = blockWalk ?: DEFAULT_BLOCK_WALK
        val blockRange = blockRange ?: DEFAULT_BLOCK_RANGE
        val active = active ?: DEFAULT_ACTIVE
        val hillSkew = hillSkew ?: DEFAULT_HILL_SKEW
        val shareLight = shareLight == true
        val occlude = occlude == true
        val anim = anim ?: DEFAULT_ANIM
        val wallWidth = wallWidth ?: DEFAULT_WALL_WIDTH
        val ambient = ambient ?: 0
        val contrast = contrast ?: 0
        val category = category ?: DEFAULT_CATEGORY
        val mirror = mirror == true
        val shadow = shadow ?: DEFAULT_SHADOW
        val resizeX = resizeX ?: DEFAULT_RESIZE_X
        val resizeY = resizeY ?: DEFAULT_RESIZE_Y
        val resizeZ = resizeZ ?: DEFAULT_RESIZE_Z
        val mapscene = mapscene ?: DEFAULT_MAP_SCENE
        val forceApproachFlags = forceApproachFlags ?: 0
        val offsetX = offsetX ?: 0
        val offsetY = offsetY ?: 0
        val offsetZ = offsetZ ?: 0
        val forceDecor = forceDecor == true
        val breakRouteFinding = breakRouteFinding == true
        val raiseObject = raiseObject ?: DEFAULT_RAISE_OBJECT
        val multiVarBit = multiVarBit ?: DEFAULT_MULTI_VARBIT
        val multiVarp = multiVarp ?: DEFAULT_MULTI_VARP
        val multiLocDefault = multiLocDefault ?: DEFAULT_MULTI_LOC_DEFAULT
        val bgsoundSound = bgsoundSound ?: DEFAULT_BGSOUND_SOUND
        val bgsoundRange = bgsoundRange ?: 0
        val bgsoundSize = bgsoundSize ?: 0
        val bgsoundMinDelay = bgsoundMinDelay ?: 0
        val bgsoundMaxDelay = bgsoundMaxDelay ?: 0
        val treeSkew = treeSkew ?: 0
        val mapIcon = mapIcon ?: DEFAULT_MAP_ICON
        val randomAnimFrame = randomAnimFrame ?: DEFAULT_RANDOM_ANIM_FRAME
        val fixLocAnimAfterLocChange = fixLocAnimAfterLocChange == true
        val contentGroup = contentGroup ?: DEFAULT_CONTENT_GROUP
        return UnpackedLocType(
            models = model.toIntArray(),
            shapes = modelShape.toByteArray(),
            name = name,
            desc = desc,
            width = width,
            length = length,
            blockWalk = blockWalk,
            blockRange = blockRange,
            active = active,
            hillSkew = hillSkew,
            shareLight = shareLight,
            occlude = occlude,
            anim = anim,
            wallWidth = wallWidth,
            ambient = ambient,
            contrast = contrast,
            op = op.copyOf(),
            recolS = recolS.toShortArray(),
            recolD = recolD.toShortArray(),
            retexS = retexS.toShortArray(),
            retexD = retexD.toShortArray(),
            category = category,
            mirror = mirror,
            shadow = shadow,
            resizeX = resizeX,
            resizeY = resizeY,
            resizeZ = resizeZ,
            mapscene = mapscene,
            forceApproachFlags = forceApproachFlags,
            offsetX = offsetX,
            offsetY = offsetY,
            offsetZ = offsetZ,
            forceDecor = forceDecor,
            breakRouteFinding = breakRouteFinding,
            raiseObject = raiseObject,
            multiVarBit = multiVarBit,
            multiVarp = multiVarp,
            multiLocDefault = multiLocDefault,
            multiLoc = multiLoc.toShortArray(),
            bgsoundSound = bgsoundSound,
            bgsoundRange = bgsoundRange,
            bgsoundSize = bgsoundSize,
            bgsoundMinDelay = bgsoundMinDelay,
            bgsoundMaxDelay = bgsoundMaxDelay,
            bgsoundRandomSounds = bgsoundRandomSound.toShortArray(),
            treeSkew = treeSkew,
            mapIcon = mapIcon,
            randomAnimFrame = randomAnimFrame,
            fixLocAnimAfterLocChange = fixLocAnimAfterLocChange,
            paramMap = paramMap,
            contentGroup = contentGroup,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object : MergeableCacheBuilder<UnpackedLocType> {
        public const val DEFAULT_NAME: String = "null"
        public const val DEFAULT_WIDTH: Int = 1
        public const val DEFAULT_LENGTH: Int = 1
        public const val DEFAULT_BLOCK_WALK: Int = 2
        public const val DEFAULT_BLOCK_RANGE: Boolean = true
        public const val DEFAULT_ACTIVE: Int = -1
        public const val DEFAULT_HILL_SKEW: Int = -1
        public const val DEFAULT_ANIM: Int = -1
        public const val DEFAULT_WALL_WIDTH: Int = 16
        public const val DEFAULT_CATEGORY: Int = -1
        public const val DEFAULT_SHADOW: Boolean = true
        public const val DEFAULT_RESIZE_X: Int = 128
        public const val DEFAULT_RESIZE_Y: Int = 128
        public const val DEFAULT_RESIZE_Z: Int = 128
        public const val DEFAULT_MAP_SCENE: Int = -1
        public const val DEFAULT_RAISE_OBJECT: Int = -1
        public const val DEFAULT_MULTI_VARBIT: Int = -1
        public const val DEFAULT_MULTI_VARP: Int = -1
        public const val DEFAULT_MULTI_LOC_DEFAULT: Int = -1
        public const val DEFAULT_BGSOUND_SOUND: Int = -1
        public const val DEFAULT_MAP_ICON: Int = -1
        public const val DEFAULT_RANDOM_ANIM_FRAME: Boolean = true
        public const val DEFAULT_CONTENT_GROUP: Int = -1

        public const val OP_CAPACITY: Int = 5
        public const val RECOL_CAPACITY: Int = 15

        override fun merge(edit: UnpackedLocType, base: UnpackedLocType): UnpackedLocType {
            val name = select(edit, base, DEFAULT_NAME) { name }
            val desc = selectPredicate(edit.desc, base.desc) { edit.desc.isNotBlank() }
            val width = select(edit, base, DEFAULT_WIDTH) { width }
            val length = select(edit, base, DEFAULT_LENGTH) { length }
            val blockWalk = select(edit, base, DEFAULT_BLOCK_WALK) { blockWalk }
            val blockRange = select(edit, base, DEFAULT_BLOCK_RANGE) { blockRange }
            val active = select(edit, base, DEFAULT_ACTIVE) { active }
            val hillSkew = select(edit, base, DEFAULT_HILL_SKEW) { hillSkew }
            val shareLight = select(edit, base, default = false) { shareLight }
            val occlude = select(edit, base, default = false) { occlude }
            val anim = select(edit, base, DEFAULT_ANIM) { anim }
            val wallWidth = select(edit, base, DEFAULT_WALL_WIDTH) { wallWidth }
            val ambient = select(edit, base, default = 0) { ambient }
            val contrast = select(edit, base, default = 0) { contrast }
            val category = select(edit, base, DEFAULT_CATEGORY) { category }
            val mirror = select(edit, base, default = false) { mirror }
            val shadow = select(edit, base, DEFAULT_SHADOW) { shadow }
            val resizeX = select(edit, base, DEFAULT_RESIZE_X) { resizeX }
            val resizeY = select(edit, base, DEFAULT_RESIZE_Y) { resizeY }
            val resizeZ = select(edit, base, DEFAULT_RESIZE_Z) { resizeZ }
            val mapscene = select(edit, base, DEFAULT_MAP_SCENE) { mapscene }
            val forceApproachFlags = select(edit, base, default = 0) { forceApproachFlags }
            val offsetX = select(edit, base, default = 0) { offsetX }
            val offsetY = select(edit, base, default = 0) { offsetY }
            val offsetZ = select(edit, base, default = 0) { offsetZ }
            val forceDecor = select(edit, base, default = false) { forceDecor }
            val breakRouteFinding = select(edit, base, default = false) { breakRouteFinding }
            val raiseObject = select(edit, base, DEFAULT_RAISE_OBJECT) { raiseObject }
            val multiVarBit = select(edit, base, DEFAULT_MULTI_VARBIT) { multiVarBit }
            val multiVarp = select(edit, base, DEFAULT_MULTI_VARP) { multiVarp }
            val multiLocDefault = select(edit, base, DEFAULT_MULTI_LOC_DEFAULT) { multiLocDefault }
            val bgsoundSound = select(edit, base, DEFAULT_BGSOUND_SOUND) { bgsoundSound }
            val bgsoundRange = select(edit, base, default = 0) { bgsoundRange }
            val bgsoundSize = select(edit, base, default = 0) { bgsoundSize }
            val bgsoundMinDelay = select(edit, base, default = 0) { bgsoundMinDelay }
            val bgsoundMaxDelay = select(edit, base, default = 0) { bgsoundMaxDelay }
            val treeSkew = select(edit, base, default = 0) { treeSkew }
            val mapIcon = select(edit, base, DEFAULT_MAP_ICON) { mapIcon }
            val randomAnimFrame = select(edit, base, DEFAULT_RANDOM_ANIM_FRAME) { randomAnimFrame }
            val fixLocAnimAfterLocChange =
                select(edit, base, default = false) { fixLocAnimAfterLocChange }
            val contentGroup = select(edit, base, DEFAULT_CONTENT_GROUP) { contentGroup }
            val models = selectIntArray(edit, base) { models }
            val shapes = selectByteArray(edit, base) { shapes }
            val op = selectPredicate(edit.op, base.op) { edit.op.any { it != null } }
            val recolS = selectShortArray(edit, base) { recolS }
            val recolD = selectShortArray(edit, base) { recolD }
            val retexS = selectShortArray(edit, base) { retexS }
            val retexD = selectShortArray(edit, base) { retexD }
            val multiLoc = selectShortArray(edit, base) { multiLoc }
            val bgsoundRandomSounds = selectShortArray(edit, base) { bgsoundRandomSounds }
            val paramMap = selectParamMap(edit, base) { paramMap }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedLocType(
                models = models,
                shapes = shapes,
                name = name,
                desc = desc,
                width = width,
                length = length,
                blockWalk = blockWalk,
                blockRange = blockRange,
                active = active,
                hillSkew = hillSkew,
                shareLight = shareLight,
                occlude = occlude,
                anim = anim,
                wallWidth = wallWidth,
                ambient = ambient,
                contrast = contrast,
                op = op,
                recolS = recolS,
                recolD = recolD,
                retexS = retexS,
                retexD = retexD,
                category = category,
                mirror = mirror,
                shadow = shadow,
                resizeX = resizeX,
                resizeY = resizeY,
                resizeZ = resizeZ,
                mapscene = mapscene,
                forceApproachFlags = forceApproachFlags,
                offsetX = offsetX,
                offsetY = offsetY,
                offsetZ = offsetZ,
                forceDecor = forceDecor,
                breakRouteFinding = breakRouteFinding,
                raiseObject = raiseObject,
                multiVarBit = multiVarBit,
                multiVarp = multiVarp,
                multiLocDefault = multiLocDefault,
                multiLoc = multiLoc,
                bgsoundSound = bgsoundSound,
                bgsoundRange = bgsoundRange,
                bgsoundSize = bgsoundSize,
                bgsoundMinDelay = bgsoundMinDelay,
                bgsoundMaxDelay = bgsoundMaxDelay,
                bgsoundRandomSounds = bgsoundRandomSounds,
                treeSkew = treeSkew,
                mapIcon = mapIcon,
                randomAnimFrame = randomAnimFrame,
                fixLocAnimAfterLocChange = fixLocAnimAfterLocChange,
                paramMap = paramMap,
                contentGroup = contentGroup,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
