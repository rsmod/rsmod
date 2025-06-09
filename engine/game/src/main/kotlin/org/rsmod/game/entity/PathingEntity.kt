package org.rsmod.game.entity

import it.unimi.dsi.fastutil.longs.LongArrayList
import kotlin.coroutines.startCoroutine
import org.rsmod.annotations.InternalApi
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.coroutine.suspension.GameCoroutineSimpleCompletion
import org.rsmod.game.entity.player.ProtectedAccessLostException
import org.rsmod.game.entity.util.EntityExactMove
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.game.entity.util.EntityFaceTarget
import org.rsmod.game.hero.HeroPoints
import org.rsmod.game.interact.Interaction
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.map.CardinalDirection
import org.rsmod.game.map.Direction
import org.rsmod.game.map.OrdinalDirection
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteDestination
import org.rsmod.game.movement.RouteRequest
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.walktrig.WalkTriggerPriority
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.collision.CollisionStrategy
import org.rsmod.routefinder.util.Rotations
import org.rsmod.utils.sorting.QuickSort

@OptIn(InternalApi::class)
public sealed class PathingEntity {
    public abstract val avatar: PathingEntityAvatar

    public abstract val collisionStrategy: CollisionStrategy?
    public abstract val blockWalkCollisionFlag: Int?

    public abstract val heroPoints: HeroPoints

    public var slotId: Int = INVALID_SLOT

    /**
     * A copy of the _most up-to-date_ map clock based on the current game tick. This serves as a
     * consistent baseline for delayed actions, such as `queue`, `timer`, and `delay`.
     */
    public var currentMapClock: Int = -1

    /**
     * The map clock value when this entity was last "processed" in the game loop. Unlike
     * [currentMapClock], this value is used for certain flags, such as [isDelayed].
     *
     * ### Why this is needed:
     * Client input processing occurs before suspended coroutines (e.g., delayed actions) are
     * processed. This can lead to scenarios where a player can cancel a suspended coroutine before
     * it resumes.
     *
     * ### Example Problem:
     * 1. **Player Action**:
     *     - Player clicks a ladder on map clock 49.
     *     - The ladder script calls `delay(1); telejump(new_coords);`.
     *     - Player is delayed until map clock 50 (their `delay` is set to 50).
     * 2. **Next Tick**:
     *     - Map clock increments to 50.
     *     - Player clicks away from the ladder.
     *     - The `MoveGameClick` packet handler processes the player's input and checks if they are
     *       delayed.
     *     - Since `delay` is 50 and `currentMapClock` is also 50, the player appears "not delayed."
     *     - The player moves and cancels their coroutine.
     * 3. **Later in the Tick**:
     *     - During player processing, the player's `activeCoroutine` would normally advance.
     *     - However, it has already been cancelled during client input processing by the
     *       `MoveGameClick` handler.
     */
    public var processedMapClock: Int = -1

    public var delay: Int = Int.MIN_VALUE
    public var lastMovement: Int = Int.MIN_VALUE

    /**
     * **Internal flag** indicating whether the entity is considered hidden.
     *
     * **Note:** This does **not** visually hide the entity.
     * - To visually hide a npc, use [`NpcRepository.hide`](#).
     * - To visually hide a player, use [`PlayerRepository.hide`](#).
     */
    @InternalApi(
        "This flag is for internal use only and does not visually hide entities. " +
            "Use `NpcRepository.hide` for npcs and `PlayerRepository.hide` for players."
    )
    public var hidden: Boolean = false

    public var activeCoroutine: GameCoroutine? = null
    public val routeDestination: RouteDestination = RouteDestination()
    public var routeRequest: RouteRequest? = null
    // Used for setting temporary move speed for single requests, aka ctrl run mode.
    public var tempMoveSpeed: MoveSpeed? = null
    public var moveSpeed: MoveSpeed = MoveSpeed.Stationary
    public var cachedMoveSpeed: MoveSpeed = MoveSpeed.Stationary

    public var lastProcessedZone: ZoneKey = ZoneKey.NULL
    public var lastProcessedCoord: CoordGrid = CoordGrid.NULL
    public var pendingTeleport: Boolean = false
    public var pendingTelejump: Boolean = false
    public var pendingStepCount: Int = 0

    public var pendingExactMove: EntityExactMove? = null

    public var faceEntity: EntityFaceTarget = EntityFaceTarget.NULL
    public var lastFaceEntity: Int = Int.MIN_VALUE

    public var pendingFaceAngle: EntityFaceAngle = EntityFaceAngle.NULL

    public var pendingFaceSquare: CoordGrid = CoordGrid.NULL
    public var pendingFaceWidth: Int = 1
    public var pendingFaceLength: Int = 1

    public var pendingSequence: EntitySeq = EntitySeq.NULL
    public val pendingSpotanims: LongArrayList = LongArrayList()

    public var interaction: Interaction? = null

    /**
     * The currently active [WalkTriggerType].
     *
     * _Note: Use the [PathingEntity.walkTrigger] function to set this value._
     */
    public var walkTrigger: WalkTriggerType? = null
        private set

    internal var animProtect: Boolean = false

    public val isSlotAssigned: Boolean
        get() = slotId != INVALID_SLOT

    public val isInvisible: Boolean
        get() = hidden

    public val isVisible: Boolean
        get() = !hidden

    public val size: Int
        get() = avatar.size

    public val hasMovedThisCycle: Boolean
        get() = lastMovement >= processedMapClock

    public val hasMovedPreviousCycle: Boolean
        get() = lastMovement == processedMapClock - 1

    public val cyclesWithoutMovement: Int
        get() = processedMapClock - lastMovement

    public val isFacingEntity: Boolean
        get() = faceEntity != EntityFaceTarget.NULL

    public val isFacingPlayer: Boolean
        get() = faceEntity.isPlayer

    public val isFacingNpc: Boolean
        get() = faceEntity.isNpc

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
        routeDestination.clear()
    }

    public fun clearInteraction() {
        interaction = null
    }

    public abstract fun anim(seq: SeqType, delay: Int = 0, priority: Int = seq.priority)

    public abstract fun resetAnim()

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

    public fun calculateDirection(target: PathingEntity): Direction {
        return Direction.between(bounds(), target.bounds())
    }

    public fun calculateCardinalDirection(target: PathingEntity): CardinalDirection {
        return Direction.cardinalBetween(bounds(), target.bounds())
    }

    public fun calculateOrdinalDirection(target: PathingEntity): OrdinalDirection {
        return Direction.ordinalBetween(bounds(), target.bounds())
    }

    public fun calculateAngle(target: CoordGrid, width: Int, length: Int): Int? =
        when (target) {
            CoordGrid.ZERO -> 0
            coords -> null
            else -> {
                val targetBounds = Bounds(target, width, length)
                Direction.angleBetween(bounds(), targetBounds)
            }
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
     * Attempts to set [walkTrigger] to [trigger], ensuring that walk trigger priority rules are
     * respected.
     *
     * If the existing walk trigger cannot be overwritten due to its priority, the update is
     * **rejected**, and this function returns `false`. Otherwise, the trigger is updated, and
     * `true` is returned.
     *
     * **See:** Documentation in [WalkTriggerPriority] for priority rules.
     *
     * @see [WalkTriggerPriority.None]
     * @see [WalkTriggerPriority.Low]
     * @see [WalkTriggerPriority.High]
     */
    public fun walkTrigger(trigger: WalkTriggerType): Boolean {
        val previous = walkTrigger?.priority
        if (!trigger.priority.canOverwrite(previous)) {
            return false
        }
        walkTrigger = trigger
        return true
    }

    public fun clearWalkTrigger() {
        walkTrigger = null
    }

    public fun heroPoints(source: Player, points: Int) {
        if (points <= 0) {
            return
        }
        val uuid = source.uuid ?: error("Unexpected null uuid for player: $source")
        heroPoints.add(uuid, points)
    }

    public fun findHero(playerList: PlayerList): Player? {
        val heroes = heroPoints.toMutableList()
        if (heroes.isEmpty()) {
            return null
        }

        QuickSort.alternating(heroes) { a, b -> b.points - a.points }

        val hero = heroes[0]
        return playerList.firstOrNull { it.uuid == hero.uuid }
    }

    @InternalApi
    public fun clearHeroPoints() {
        heroPoints.clear()
    }

    @InternalApi
    public fun hasInteraction(): Boolean {
        return interaction != null
    }

    /**
     * This method is responsible for cleaning up any ongoing tasks that the entity may be
     * responsible for. This includes things such as coroutines; ex: [activeCoroutine]. If these
     * coroutines are not canceled properly, they may linger in memory and run indefinitely.
     */
    public fun destroy() {
        cancelActiveCoroutine()
    }

    public companion object {
        public const val INVALID_SLOT: Int = -1
        public const val DEFAULT_AP_RANGE: Int = 10
    }
}
