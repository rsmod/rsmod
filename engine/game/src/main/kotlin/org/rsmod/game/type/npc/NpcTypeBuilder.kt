package org.rsmod.game.type.npc

import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.npc.NpcPatrol
import org.rsmod.game.map.Direction
import org.rsmod.game.movement.BlockWalk
import org.rsmod.game.movement.MoveRestrict
import org.rsmod.game.type.util.CompactableIntArray
import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.GenericPropertySelector.selectIntArray
import org.rsmod.game.type.util.GenericPropertySelector.selectParamMap
import org.rsmod.game.type.util.GenericPropertySelector.selectPredicate
import org.rsmod.game.type.util.GenericPropertySelector.selectShortArray
import org.rsmod.game.type.util.MergeableCacheBuilder
import org.rsmod.game.type.util.ParamMap

@DslMarker private annotation class NpcBuilderDsl

@NpcBuilderDsl
public class NpcTypeBuilder(public var internal: String? = null) {
    public var name: String? = null
    public var desc: String? = null
    public var size: Int? = null
    public var models: CompactableIntArray = CompactableIntArray()
    public var readyAnim: Int? = null
    public var walkAnim: Int? = null
    public var turnBackAnim: Int? = null
    public var turnLeftAnim: Int? = null
    public var turnRightAnim: Int? = null
    public var category: Int? = null
    public val op: Array<String?> = arrayOfNulls(OP_CAPACITY)
    public var recolS: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var recolD: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var retexS: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var retexD: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var head: CompactableIntArray = CompactableIntArray()
    public var minimap: Boolean? = null
    public var vislevel: Int? = null
    public var resizeH: Int? = null
    public var resizeV: Int? = null
    public var alwaysOnTop: Boolean? = null
    public var ambient: Int? = null
    public var contrast: Int? = null
    public var headIconGraphic: CompactableIntArray = CompactableIntArray()
    public var headIconIndex: CompactableIntArray = CompactableIntArray()
    public var turnSpeed: Int? = null
    public var multiVarp: Int? = null
    public var multiVarBit: Int? = null
    public var multiNpc: CompactableIntArray = CompactableIntArray()
    public var multiNpcDefault: Int? = null
    public var active: Boolean? = null
    public var rotationFlag: Boolean? = null
    public var follower: Boolean? = null
    public var lowPriorityOps: Boolean? = null
    public var overlayHeight: Int? = null
    public var runAnim: Int? = null
    public var runTurnBackAnim: Int? = null
    public var runTurnLeftAnim: Int? = null
    public var runTurnRightAnim: Int? = null
    public var crawlAnim: Int? = null
    public var crawlTurnBackAnim: Int? = null
    public var crawlTurnLeftAnim: Int? = null
    public var crawlTurnRightAnim: Int? = null
    public var paramMap: ParamMap? = null
    public var moveRestrict: MoveRestrict? = null
    public var defaultMode: NpcMode? = null
    public var blockWalk: BlockWalk? = null
    public var patrol: NpcPatrol? = null
    public var respawnRate: Int? = null
    public var maxRange: Int? = null
    public var wanderRange: Int? = null
    public var attackRange: Int? = null
    public var huntRange: Int? = null
    public var huntMode: Int? = null
    public var giveChase: Boolean? = null
    public var attack: Int? = null
    public var strength: Int? = null
    public var defence: Int? = null
    public var hitpoints: Int? = null
    public var ranged: Int? = null
    public var magic: Int? = null
    public var timer: Int? = null
    public var respawnDir: Direction? = null
    public var contentGroup: Int? = null

    public fun build(id: Int): UnpackedNpcType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val name = name ?: DEFAULT_NAME
        val desc = desc ?: ""
        val size = size ?: DEFAULT_SIZE
        val readyAnim = readyAnim ?: DEFAULT_ANIM
        val walkAnim = walkAnim ?: DEFAULT_ANIM
        val turnBackAnim = turnBackAnim ?: DEFAULT_ANIM
        val turnLeftAnim = turnLeftAnim ?: DEFAULT_ANIM
        val turnRightAnim = turnRightAnim ?: DEFAULT_ANIM
        val category = category ?: DEFAULT_CATEGORY
        val minimap = minimap ?: DEFAULT_MINIMAP
        val vislevel = vislevel ?: DEFAULT_VISLEVEL
        val resizeH = resizeH ?: DEFAULT_RESIZE_H
        val resizeV = resizeV ?: DEFAULT_RESIZE_V
        val alwaysOnTop = alwaysOnTop == true
        val ambient = ambient ?: 0
        val contrast = contrast ?: 0
        val turnSpeed = turnSpeed ?: DEFAULT_TURN_SPEED
        val multiVarp = multiVarp ?: DEFAULT_MULTI_VARP
        val multiVarBit = multiVarBit ?: DEFAULT_MULTI_VARBIT
        val multiNpcDefault = multiNpcDefault ?: DEFAULT_MULTI_DEFAULT
        val active = active ?: DEFAULT_ACTIVE
        val rotationFlag = rotationFlag ?: DEFAULT_ROTATION_FLAG
        val follower = follower == true
        val lowPriorityOps = lowPriorityOps == true
        val overlayHeight = overlayHeight ?: DEFAULT_OVERLAY_HEIGHT
        val runAnim = runAnim ?: DEFAULT_ANIM
        val runTurnBackAnim = runTurnBackAnim ?: DEFAULT_ANIM
        val runTurnLeftAnim = runTurnLeftAnim ?: DEFAULT_ANIM
        val runTurnRightAnim = runTurnRightAnim ?: DEFAULT_ANIM
        val crawlAnim = crawlAnim ?: DEFAULT_ANIM
        val crawlTurnBackAnim = crawlTurnBackAnim ?: DEFAULT_ANIM
        val crawlTurnLeftAnim = crawlTurnLeftAnim ?: DEFAULT_ANIM
        val crawlTurnRightAnim = crawlTurnRightAnim ?: DEFAULT_ANIM
        val moveRestrict = moveRestrict ?: DEFAULT_MOVE_RESTRICT
        val defaultMode = defaultMode ?: DEFAULT_MODE
        val blockWalk = blockWalk ?: DEFAULT_BLOCK_WALK
        val respawnRate = respawnRate ?: DEFAULT_RESPAWN_RATE
        val maxRange = maxRange ?: DEFAULT_MAX_RANGE
        val wanderRange = wanderRange ?: DEFAULT_WANDER_RANGE
        val attackRange = attackRange ?: DEFAULT_ATTACK_RANGE
        val huntRange = huntRange ?: DEFAULT_HUNT_RANGE
        val huntMode = huntMode ?: DEFAULT_HUNT_MODE
        val giveChase = giveChase ?: DEFAULT_GIVE_CHASE
        val attack = attack ?: DEFAULT_STAT_LEVEL
        val strength = strength ?: DEFAULT_STAT_LEVEL
        val defence = defence ?: DEFAULT_STAT_LEVEL
        val hitpoints = hitpoints ?: DEFAULT_HITPOINTS
        check(hitpoints > 0) { "Hitpoints cannot be less than `1`. (id=$id, hitpoints=$hitpoints)" }
        val ranged = ranged ?: DEFAULT_STAT_LEVEL
        val magic = magic ?: DEFAULT_STAT_LEVEL
        val timer = timer ?: DEFAULT_TIMER
        val respawnDir = respawnDir ?: DEFAULT_RESPAWN_DIR
        check(patrol == null || patrol?.isNotEmpty() == true) {
            "`patrol` must be either null, or have at least one element."
        }
        val contentGroup = contentGroup ?: DEFAULT_CONTENT_GROUP
        return UnpackedNpcType(
            name = name,
            desc = desc,
            models = models.toIntArray(),
            size = size,
            readyAnim = readyAnim,
            walkAnim = walkAnim,
            turnBackAnim = turnBackAnim,
            turnLeftAnim = turnLeftAnim,
            turnRightAnim = turnRightAnim,
            category = category,
            op = op,
            recolS = recolS.toShortArray(),
            recolD = recolD.toShortArray(),
            retexS = retexS.toShortArray(),
            retexD = retexD.toShortArray(),
            head = head.toShortArray(),
            minimap = minimap,
            vislevel = vislevel,
            resizeH = resizeH,
            resizeV = resizeV,
            alwaysOnTop = alwaysOnTop,
            ambient = ambient,
            contrast = contrast,
            headIconGraphic = headIconGraphic.toIntArray(),
            headIconIndex = headIconIndex.toIntArray(),
            turnSpeed = turnSpeed,
            multiVarp = multiVarp,
            multiVarBit = multiVarBit,
            multiNpcDefault = multiNpcDefault,
            multiNpc = multiNpc.toShortArray(),
            active = active,
            rotationFlag = rotationFlag,
            follower = follower,
            lowPriorityOps = lowPriorityOps,
            overlayHeight = overlayHeight,
            runAnim = runAnim,
            runTurnBackAnim = runTurnBackAnim,
            runTurnLeftAnim = runTurnLeftAnim,
            runTurnRightAnim = runTurnRightAnim,
            crawlAnim = crawlAnim,
            crawlTurnBackAnim = crawlTurnBackAnim,
            crawlTurnLeftAnim = crawlTurnLeftAnim,
            crawlTurnRightAnim = crawlTurnRightAnim,
            paramMap = paramMap,
            moveRestrict = moveRestrict,
            defaultMode = defaultMode,
            blockWalk = blockWalk,
            respawnRate = respawnRate,
            maxRange = maxRange,
            wanderRange = wanderRange,
            attackRange = attackRange,
            huntRange = huntRange,
            huntMode = huntMode,
            giveChase = giveChase,
            attack = attack,
            strength = strength,
            defence = defence,
            hitpoints = hitpoints,
            ranged = ranged,
            magic = magic,
            timer = timer,
            respawnDir = respawnDir,
            patrol = patrol,
            contentGroup = contentGroup,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object : MergeableCacheBuilder<UnpackedNpcType> {
        public const val DEFAULT_NAME: String = "null"
        public const val DEFAULT_SIZE: Int = 1
        public const val DEFAULT_ANIM: Int = -1
        public const val DEFAULT_CATEGORY: Int = -1
        public const val DEFAULT_MINIMAP: Boolean = true
        public const val DEFAULT_VISLEVEL: Int = -1
        public const val DEFAULT_RESIZE_H: Int = 128
        public const val DEFAULT_RESIZE_V: Int = 128
        public const val DEFAULT_TURN_SPEED: Int = 32
        public const val DEFAULT_MULTI_VARBIT: Int = -1
        public const val DEFAULT_MULTI_VARP: Int = -1
        public const val DEFAULT_MULTI_DEFAULT: Int = -1
        public const val DEFAULT_ACTIVE: Boolean = true
        public const val DEFAULT_ROTATION_FLAG: Boolean = true
        public const val DEFAULT_OVERLAY_HEIGHT: Int = -1
        public const val DEFAULT_RESPAWN_RATE: Int = 100
        public const val DEFAULT_MAX_RANGE: Int = 7
        public const val DEFAULT_WANDER_RANGE: Int = 5
        public const val DEFAULT_ATTACK_RANGE: Int = 1
        public const val DEFAULT_HUNT_RANGE: Int = 5
        public const val DEFAULT_HUNT_MODE: Int = -1
        public const val DEFAULT_GIVE_CHASE: Boolean = true
        public const val DEFAULT_HITPOINTS: Int = 10
        public const val DEFAULT_STAT_LEVEL: Int = 1
        public const val DEFAULT_TIMER: Int = -1
        public const val DEFAULT_CONTENT_GROUP: Int = -1

        public const val OP_CAPACITY: Int = 5
        public const val RECOL_CAPACITY: Int = 15

        public val DEFAULT_MODE: NpcMode = NpcMode.Wander
        public val DEFAULT_BLOCK_WALK: BlockWalk = BlockWalk.Npc
        public val DEFAULT_MOVE_RESTRICT: MoveRestrict = MoveRestrict.Normal
        public val DEFAULT_RESPAWN_DIR: Direction = Direction.South

        override fun merge(edit: UnpackedNpcType, base: UnpackedNpcType): UnpackedNpcType {
            val name = select(edit, base, DEFAULT_NAME) { name }
            val desc = selectPredicate(edit.desc, base.desc) { edit.desc.isNotBlank() }
            val models = selectIntArray(edit, base) { models }
            val size = select(edit, base, DEFAULT_SIZE) { size }
            val readyAnim = select(edit, base, DEFAULT_ANIM) { readyAnim }
            val walkAnim = select(edit, base, DEFAULT_ANIM) { walkAnim }
            val turnBackAnim = select(edit, base, DEFAULT_ANIM) { turnBackAnim }
            val turnLeftAnim = select(edit, base, DEFAULT_ANIM) { turnLeftAnim }
            val turnRightAnim = select(edit, base, DEFAULT_ANIM) { turnRightAnim }
            val category = select(edit, base, DEFAULT_CATEGORY) { category }
            val op = selectPredicate(edit.op, base.op) { edit.op.any { it != null } }
            val recolS = selectShortArray(edit, base) { recolS }
            val recolD = selectShortArray(edit, base) { recolD }
            val retexS = selectShortArray(edit, base) { retexS }
            val retexD = selectShortArray(edit, base) { retexD }
            val head = selectShortArray(edit, base) { head }
            val minimap = select(edit, base, DEFAULT_MINIMAP) { minimap }
            val vislevel = select(edit, base, DEFAULT_VISLEVEL) { vislevel }
            val resizeH = select(edit, base, DEFAULT_RESIZE_H) { resizeH }
            val resizeV = select(edit, base, DEFAULT_RESIZE_V) { resizeV }
            val alwaysOnTop = select(edit, base, default = false) { alwaysOnTop }
            val ambient = select(edit, base, default = 0) { ambient }
            val contrast = select(edit, base, default = 0) { contrast }
            val headIconGraphic = selectIntArray(edit, base) { headIconGraphic }
            val headIconIndex = selectIntArray(edit, base) { headIconIndex }
            val turnSpeed = select(edit, base, DEFAULT_TURN_SPEED) { turnSpeed }
            val multiVarp = select(edit, base, DEFAULT_MULTI_VARP) { multiVarp }
            val multiVarBit = select(edit, base, DEFAULT_MULTI_VARBIT) { multiVarBit }
            val multiDefault = select(edit, base, DEFAULT_MULTI_DEFAULT) { multiNpcDefault }
            val multiNpc = selectShortArray(edit, base) { multiNpc }
            val active = select(edit, base, DEFAULT_ACTIVE) { active }
            val rotationFlag = select(edit, base, DEFAULT_ROTATION_FLAG) { rotationFlag }
            val follower = select(edit, base, default = false) { follower }
            val lowPriorityOps = select(edit, base, default = false) { lowPriorityOps }
            val overlayHeight = select(edit, base, DEFAULT_OVERLAY_HEIGHT) { overlayHeight }
            val runAnim = select(edit, base, DEFAULT_ANIM) { runAnim }
            val runTurnBackAnim = select(edit, base, DEFAULT_ANIM) { runTurnBackAnim }
            val runTurnLeftAnim = select(edit, base, DEFAULT_ANIM) { runTurnLeftAnim }
            val runTurnRightAnim = select(edit, base, DEFAULT_ANIM) { runTurnRightAnim }
            val crawlAnim = select(edit, base, DEFAULT_ANIM) { crawlAnim }
            val crawlTurnBackAnim = select(edit, base, DEFAULT_ANIM) { crawlTurnBackAnim }
            val crawlTurnLeftAnim = select(edit, base, DEFAULT_ANIM) { crawlTurnLeftAnim }
            val crawlTurnRightAnim = select(edit, base, DEFAULT_ANIM) { crawlTurnRightAnim }
            val paramMap = selectParamMap(edit, base) { paramMap }
            val moveRestrict = select(edit, base, DEFAULT_MOVE_RESTRICT) { moveRestrict }
            val defaultMode = select(edit, base, DEFAULT_MODE) { defaultMode }
            val blockWalk = select(edit, base, DEFAULT_BLOCK_WALK) { blockWalk }
            val respawnRate = select(edit, base, DEFAULT_RESPAWN_RATE) { respawnRate }
            val maxRange = select(edit, base, DEFAULT_MAX_RANGE) { maxRange }
            val wanderRange = select(edit, base, DEFAULT_WANDER_RANGE) { wanderRange }
            val attackRange = select(edit, base, DEFAULT_ATTACK_RANGE) { attackRange }
            val huntRange = select(edit, base, DEFAULT_HUNT_RANGE) { huntRange }
            val huntMode = select(edit, base, DEFAULT_HUNT_MODE) { huntMode }
            val giveChase = select(edit, base, DEFAULT_GIVE_CHASE) { giveChase }
            val attack = select(edit, base, DEFAULT_STAT_LEVEL) { attack }
            val strength = select(edit, base, DEFAULT_STAT_LEVEL) { strength }
            val defence = select(edit, base, DEFAULT_STAT_LEVEL) { defence }
            val hitpoints = select(edit, base, DEFAULT_HITPOINTS) { hitpoints }
            val ranged = select(edit, base, DEFAULT_STAT_LEVEL) { ranged }
            val magic = select(edit, base, DEFAULT_STAT_LEVEL) { magic }
            val timer = select(edit, base, DEFAULT_TIMER) { timer }
            val respawnDir = select(edit, base, DEFAULT_RESPAWN_DIR) { respawnDir }
            val patrol = select(edit, base, default = null) { patrol }
            val contentGroup = select(edit, base, DEFAULT_CONTENT_GROUP) { contentGroup }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedNpcType(
                name = name,
                desc = desc,
                models = models,
                size = size,
                readyAnim = readyAnim,
                walkAnim = walkAnim,
                turnBackAnim = turnBackAnim,
                turnLeftAnim = turnLeftAnim,
                turnRightAnim = turnRightAnim,
                category = category,
                op = op,
                recolS = recolS,
                recolD = recolD,
                retexS = retexS,
                retexD = retexD,
                head = head,
                minimap = minimap,
                vislevel = vislevel,
                resizeH = resizeH,
                resizeV = resizeV,
                alwaysOnTop = alwaysOnTop,
                ambient = ambient,
                contrast = contrast,
                headIconGraphic = headIconGraphic,
                headIconIndex = headIconIndex,
                turnSpeed = turnSpeed,
                multiVarp = multiVarp,
                multiVarBit = multiVarBit,
                multiNpcDefault = multiDefault,
                multiNpc = multiNpc,
                active = active,
                rotationFlag = rotationFlag,
                follower = follower,
                lowPriorityOps = lowPriorityOps,
                overlayHeight = overlayHeight,
                runAnim = runAnim,
                runTurnBackAnim = runTurnBackAnim,
                runTurnLeftAnim = runTurnLeftAnim,
                runTurnRightAnim = runTurnRightAnim,
                crawlAnim = crawlAnim,
                crawlTurnBackAnim = crawlTurnBackAnim,
                crawlTurnLeftAnim = crawlTurnLeftAnim,
                crawlTurnRightAnim = crawlTurnRightAnim,
                paramMap = paramMap,
                moveRestrict = moveRestrict,
                defaultMode = defaultMode,
                blockWalk = blockWalk,
                respawnRate = respawnRate,
                maxRange = maxRange,
                wanderRange = wanderRange,
                attackRange = attackRange,
                huntRange = huntRange,
                huntMode = huntMode,
                giveChase = giveChase,
                attack = attack,
                strength = strength,
                defence = defence,
                hitpoints = hitpoints,
                ranged = ranged,
                magic = magic,
                timer = timer,
                respawnDir = respawnDir,
                patrol = patrol,
                contentGroup = contentGroup,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
