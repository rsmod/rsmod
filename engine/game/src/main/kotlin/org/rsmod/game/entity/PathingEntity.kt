package org.rsmod.game.entity

import it.unimi.dsi.fastutil.longs.LongArrayList
import kotlin.coroutines.startCoroutine
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.coroutine.suspension.GameCoroutineSimpleCompletion
import org.rsmod.game.entity.player.ProtectedAccessLostException
import org.rsmod.game.entity.shared.PathingEntityCommon
import org.rsmod.game.interact.Interaction
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.map.Direction
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteDestination
import org.rsmod.game.movement.RouteRequest
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.vars.VariableIntMap
import org.rsmod.game.vars.VariableStringMap
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.collision.CollisionStrategy
import org.rsmod.routefinder.util.Rotations

public sealed class PathingEntity {
    public abstract val avatar: PathingEntityAvatar
    public abstract val isBusy: Boolean

    public abstract val collisionStrategy: CollisionStrategy?
    public abstract val blockWalkCollisionFlag: Int?

    public val vars: VariableIntMap = VariableIntMap()
    public val varsString: VariableStringMap = VariableStringMap()

    public var slotId: Int = INVALID_SLOT
    public var currentMapClock: Int = 0

    public var hidden: Boolean = false

    // Important for these variables to start as negative so that the initial [currentMapClock] is
    // greater than they are. Otherwise, the first map clock tick will always have the PathingEntity
    // as _technically_ "delayed" and "moving". This would be fine if not for integration tests,
    // where we do not want the responsibility of setting these to be on the tests themselves.
    public var delay: Int = -1
    public var lastMovement: Int = -1

    public var activeCoroutine: GameCoroutine? = null
    public val routeDestination: RouteDestination = RouteDestination()
    public var routeRequest: RouteRequest? = null
    // Used for setting temporary move speed for single requests, aka ctrl run mode.
    public var tempMoveSpeed: MoveSpeed? = null
    public var currentWaypoint: CoordGrid = CoordGrid.ZERO
    public var moveSpeed: MoveSpeed = MoveSpeed.Stationary
    public var cachedMoveSpeed: MoveSpeed = MoveSpeed.Stationary

    public var lastProcessedZone: ZoneKey = ZoneKey.NULL

    public var faceEntitySlot: Int = -1

    public var pendingFaceSquare: CoordGrid = CoordGrid.NULL
    public var pendingFaceWidth: Int = 1
    public var pendingFaceLength: Int = 1
    public var faceAngle: Int = 0

    public var pendingSequence: EntitySeq = EntitySeq.NULL
    public val pendingSpotanims: LongArrayList = LongArrayList()

    public var interaction: Interaction? = null

    internal var animProtect: Boolean = false

    public val isSlotAssigned: Boolean
        get() = slotId != INVALID_SLOT

    public val isInvisible: Boolean
        get() = hidden

    public val isVisible: Boolean
        get() = !hidden

    public val isValidTarget: Boolean
        get() = isSlotAssigned && isVisible

    public val size: Int
        get() = avatar.size

    public val isDelayed: Boolean
        get() = delay > currentMapClock

    public val isNotDelayed: Boolean
        get() = !isDelayed

    public val isAccessProtected: Boolean
        get() = isBusy || activeCoroutine?.isSuspended == true

    public val hasMovedThisCycle: Boolean
        get() = lastMovement >= currentMapClock

    public val hasMovedPreviousCycle: Boolean
        get() = lastMovement == currentMapClock - 1

    public val cyclesWithoutMovement: Int
        get() = currentMapClock - lastMovement

    /**
     * Returns `true` if the entity has a pending interaction or active route waypoint.
     *
     * _Note: Terrible name, but used for consistency with official naming._
     */
    public val isBusy2: Boolean
        get() = interaction != null || routeDestination.isNotEmpty()

    /**
     * Returns `true` if the entity does not have a pending interaction or an active route waypoint.
     * In other words, returns the inverse of [isBusy2].
     */
    public val isIdle: Boolean
        get() = !isBusy2

    public val isFacingEntity: Boolean
        get() = faceEntitySlot != -1

    public val isFacingPlayer: Boolean
        get() = faceEntitySlot >= PathingEntityCommon.FACE_PLAYER_START_SLOT

    public val isFacingNpc: Boolean
        get() = faceEntitySlot in 0 until PathingEntityCommon.FACE_PLAYER_START_SLOT

    public var coords: CoordGrid
        get() = avatar.coords
        set(value) {
            avatar.coords = value
        }

    public var x: Int
        get() = avatar.coords.x
        set(value) {
            avatar.coords = avatar.coords.copy(x = value)
        }

    public var z: Int
        get() = avatar.coords.z
        set(value) {
            avatar.coords = avatar.coords.copy(z = value)
        }

    public var level: Int
        get() = avatar.coords.level
        set(value) {
            avatar.coords = avatar.coords.copy(level = value)
        }

    public var previousCoords: CoordGrid
        get() = avatar.previousCoords
        set(value) {
            avatar.previousCoords = value
        }

    public fun delay(cycles: Int = 1) {
        this.delay = currentMapClock + cycles
    }

    public fun launch(
        coroutine: GameCoroutine = GameCoroutine(),
        block: suspend GameCoroutine.() -> Unit,
    ): GameCoroutine {
        cancelActiveCoroutine()
        val completion = GameCoroutineSimpleCompletion
        block.startCoroutine(coroutine, completion)
        if (coroutine.isSuspended) {
            activeCoroutine = coroutine
        }
        return coroutine
    }

    public fun advanceActiveCoroutine() {
        try {
            activeCoroutine?.advance()
        } catch (_: ProtectedAccessLostException) {
            /* no-op: not out of the ordinary to occur */
        }
        if (activeCoroutine?.isIdle == true) {
            activeCoroutine = null
        }
    }

    public fun resumeActiveCoroutine(withValue: Any) {
        try {
            activeCoroutine?.resumeWith(withValue)
        } catch (_: ProtectedAccessLostException) {
            /* no-op: not out of the ordinary to occur */
        }
        if (activeCoroutine?.isIdle == true) {
            activeCoroutine = null
        }
    }

    public fun cancelActiveCoroutine() {
        activeCoroutine?.cancel()
        activeCoroutine = null
    }

    public fun abortRoute() {
        routeRequest = null
        tempMoveSpeed = null
        routeDestination.abort()
        currentWaypoint = coords
    }

    public fun clearRouteRecalc() {
        routeDestination.clearRecalc()
    }

    public fun clearInteraction() {
        interaction = null
    }

    public fun resetAnim() {
        pendingSequence = EntitySeq.ZERO
    }

    public abstract fun anim(seq: SeqType, delay: Int = 0, priority: Int = seq.priority)

    public abstract fun spotanim(spot: SpotanimType, delay: Int = 0, height: Int = 0, slot: Int = 0)

    /**
     * Sets the [pendingFaceSquare] for [target] to face as soon as this [PathingEntity] is not
     * moving, whenever that may be.
     */
    public fun faceSquare(target: CoordGrid, targetWidth: Int = 1, targetLength: Int = 1) {
        pendingFaceSquare = target
        pendingFaceWidth = targetWidth
        pendingFaceLength = targetLength
    }

    /** @see [faceSquare] */
    public fun faceDirection(direction: Direction) {
        val target = coords.translate(direction.xOff, direction.zOff)
        faceSquare(target, targetWidth = 1, targetLength = 1)
    }

    /**
     * This function calls [faceSquare] with the coordinates and adjusted dimensions of the provided
     * [loc]. The [loc] parameter is an instance of [BoundLocInfo], which contains precomputed
     * values for width and length adjusted for the loc's angle. This method ensures that the
     * PathingEntity faces the correct angle based on the loc's adjusted dimensions.
     *
     * @see [faceSquare]
     */
    public fun faceLoc(loc: BoundLocInfo) {
        faceSquare(loc.coords, loc.adjustedWidth, loc.adjustedLength)
    }

    /**
     * This function will call [faceSquare] with arguments based on the provided [loc] and its
     * dimensions ([width] and [length]). The dimensions should be the unmodified values from the
     * [org.rsmod.game.type.loc.UnpackedLocType] of the loc. This method automatically adjusts the
     * dimensions according to the loc's angle to ensure that the PathingEntity faces the correct
     * angle.
     *
     * @see [faceSquare]
     */
    public fun faceLoc(loc: LocInfo, width: Int, length: Int) {
        val rotatedWidth = Rotations.rotate(loc.angleId, width, length)
        val rotatedLength = Rotations.rotate(loc.angleId, length, width)
        faceSquare(loc.coords, rotatedWidth, rotatedLength)
    }

    /**
     * This function will call [faceSquare] with arguments based on the provided [target]'s
     * coordinates and size. The [size] is used for both width and length.
     *
     * @see [faceSquare]
     */
    public fun facePathingEntitySquare(target: PathingEntity) {
        faceSquare(target.coords, target.size, target.size)
    }

    public fun resetPendingFaceSquare() {
        pendingFaceSquare = CoordGrid.NULL
        pendingFaceLength = 1
        pendingFaceWidth = 1
    }

    /**
     * Alias for [PathingEntityAvatar.distanceTo].
     *
     * @see [PathingEntityAvatar.distanceTo]
     */
    public fun distanceTo(target: CoordGrid, width: Int = 1, length: Int = 1): Int =
        avatar.distanceTo(Bounds(target, width, length))

    /**
     * Alias for [PathingEntityAvatar.distanceTo].
     *
     * @see [PathingEntityAvatar.distanceTo]
     */
    public fun distanceTo(other: PathingEntity): Int = avatar.distanceTo(other.bounds())

    /**
     * Alias for [PathingEntityAvatar.distanceTo].
     *
     * @see [PathingEntityAvatar.distanceTo]
     */
    public fun distanceTo(other: Controller): Int = avatar.distanceTo(other.bounds())

    /**
     * Alias for [PathingEntityAvatar.distanceTo].
     *
     * @see [PathingEntityAvatar.distanceTo]
     */
    public fun distanceTo(loc: BoundLocInfo): Int = avatar.distanceTo(loc.bounds())

    /**
     * Alias for [PathingEntityAvatar.isWithinDistance].
     *
     * @see [PathingEntityAvatar.isWithinDistance]
     */
    public fun isWithinDistance(
        target: CoordGrid,
        distance: Int,
        width: Int = 1,
        length: Int = 1,
    ): Boolean = avatar.isWithinDistance(Bounds(target, width, length), distance)

    /**
     * Alias for [PathingEntityAvatar.isWithinDistance].
     *
     * @see [PathingEntityAvatar.isWithinDistance]
     */
    public fun isWithinDistance(other: PathingEntity, distance: Int): Boolean =
        avatar.isWithinDistance(other.bounds(), distance)

    /**
     * Alias for [PathingEntityAvatar.isWithinDistance].
     *
     * @see [PathingEntityAvatar.isWithinDistance]
     */
    public fun isWithinDistance(other: Controller, distance: Int): Boolean =
        avatar.isWithinDistance(other.bounds(), distance)

    /**
     * Alias for [PathingEntityAvatar.isWithinDistance].
     *
     * @see [PathingEntityAvatar.isWithinDistance]
     */
    public fun isWithinDistance(loc: BoundLocInfo, distance: Int): Boolean =
        avatar.isWithinDistance(loc.bounds(), distance)

    public fun isWithinArea(southWest: CoordGrid, northEast: CoordGrid): Boolean {
        require(southWest.level == northEast.level && southWest.level == level)
        return x in southWest.x..northEast.x && z in southWest.z..northEast.z
    }

    public fun bounds(): Bounds = avatar.bounds()

    public fun addBlockWalkCollision(collision: CollisionFlagMap, base: CoordGrid) {
        val collisionFlag = blockWalkCollisionFlag ?: return
        for (z in 0 until size) {
            for (x in 0 until size) {
                collision.add(base.x + x, base.z + z, base.level, collisionFlag)
            }
        }
    }

    public fun removeBlockWalkCollision(collision: CollisionFlagMap, base: CoordGrid) {
        val collisionFlag = blockWalkCollisionFlag ?: return
        for (z in 0 until size) {
            for (x in 0 until size) {
                collision.remove(base.x + x, base.z + z, base.level, collisionFlag)
            }
        }
    }

    /**
     * This method is responsible for cleaning up any ongoing tasks that the entity may be
     * responsible for. This includes things such as coroutines; ex: [activeCoroutine]. If these
     * coroutines are not cancelled properly they may linger in memory and run indefinitely.
     */
    public fun destroy() {
        cancelActiveCoroutine()
    }

    public companion object {
        public const val INVALID_SLOT: Int = -1
        public const val DEFAULT_AP_RANGE: Int = 10
    }
}
