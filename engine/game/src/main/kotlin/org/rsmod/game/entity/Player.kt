package org.rsmod.game.entity

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.longs.LongArrayList
import org.rsmod.annotations.InternalApi
import org.rsmod.game.client.Client
import org.rsmod.game.client.ClientCycle
import org.rsmod.game.client.NoopClient
import org.rsmod.game.client.NoopClientCycle
import org.rsmod.game.entity.player.Appearance
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.game.entity.player.PublicMessage
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.game.entity.util.PathingEntityCommon
import org.rsmod.game.headbar.Headbar
import org.rsmod.game.hero.HeroPoints
import org.rsmod.game.hit.Hitmark
import org.rsmod.game.inv.Inventory
import org.rsmod.game.inv.InventoryMap
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.queue.EngineQueueList
import org.rsmod.game.queue.EngineQueueType
import org.rsmod.game.queue.PlayerQueueList
import org.rsmod.game.queue.QueueCategory
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.shop.Shop
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.timer.PlayerTimerMap
import org.rsmod.game.type.bas.UnpackedBasType
import org.rsmod.game.type.droptrig.DropTriggerType
import org.rsmod.game.type.mod.ModGroup
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.ui.UserInterfaceMap
import org.rsmod.game.vars.VarPlayerIntMap
import org.rsmod.game.vars.VarPlayerStrMap
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.collision.CollisionStrategy
import org.rsmod.routefinder.flag.CollisionFlag

public class Player(
    public var client: Client<Any, Any> = NoopClient,
    public var clientCycle: ClientCycle = NoopClientCycle,
    override val avatar: PlayerAvatar = PlayerAvatar(),
) : PathingEntity() {
    init {
        pendingFaceAngle = EntityFaceAngle.ZERO
        pendingSequence = EntitySeq.ZERO
    }

    public override val isBusy: Boolean
        get() = isDelayed || ui.modals.isNotEmpty()

    override val collisionStrategy: CollisionStrategy = CollisionStrategy.Normal

    override val blockWalkCollisionFlag: Int = CollisionFlag.BLOCK_NPCS

    override val heroPoints: HeroPoints = HeroPoints(size = 16)

    public val vars: VarPlayerIntMap = VarPlayerIntMap()
    public val strVars: VarPlayerStrMap = VarPlayerStrMap()

    public val ui: UserInterfaceMap = UserInterfaceMap()
    public val statMap: PlayerStatMap = PlayerStatMap()
    public val timerMap: PlayerTimerMap = PlayerTimerMap()
    public val softTimerMap: PlayerTimerMap = PlayerTimerMap()
    public val queueList: PlayerQueueList = PlayerQueueList()
    public val weakQueueList: PlayerQueueList = PlayerQueueList()
    public val engineQueueList: EngineQueueList = EngineQueueList()

    /**
     * A unique identifier that should be generated when the player's account is created and then
     * remain persistent forever.
     */
    public var uuid: Long? = null

    /**
     * A unique identifier, typically the same as [uuid], but differs under certain conditions, such
     * as when the player is part of a group. It is specifically used to control visibility for
     * "hidden" entities, like objs that are only visible to certain players.
     */
    public var observerUUID: Long? = null

    /**
     * A dynamic identifier that is unique within the current session but not globally persistent.
     * It is used for things such as interactions and temporary tracking.
     *
     * This uid is derived from [slotId] and [uuid]. As such, it **must not be used for anything
     * that requires persistence**.
     */
    public var uid: PlayerUid = PlayerUid.NULL
        private set

    public var username: String = ""
    public var displayName: String by avatar::name

    public var buildArea: CoordGrid = CoordGrid.NULL
    public val visibleZoneKeys: IntList = IntArrayList()
    public var lastMapBuildComplete: Int = Int.MIN_VALUE

    public var modGroup: ModGroup? = null
    public var xpRate: Double = 1.0

    public var publicMessage: PublicMessage? = null
    public var pendingSay: String? = null
    public val activeHitmarks: LongArrayList = LongArrayList()
    public val activeHeadbars: LongArrayList = LongArrayList()

    public var regionUid: Int? = null

    // The last coordinates the player has occupied, outside the region working area.
    public var lastKnownNormalCoord: CoordGrid = CoordGrid(0, 50, 50, 0, 0)

    public val invMap: InventoryMap = InventoryMap()
    public val transmittedInvs: IntArraySet = IntArraySet()
    public val transmittedInvAddQueue: IntArraySet = IntArraySet()
    public var openedShop: Shop? = null

    /* Cache for commonly-accessed Invs */
    public lateinit var inv: Inventory
    public lateinit var worn: Inventory

    public var preventLogoutMessage: String? = null
    public var preventLogoutUntil: Int? = null

    public var actionDelay: Int = -1
    public var skillAnimDelay: Int = -1
    public var refaceDelay: Int = -1

    public var lootDropDuration: Int? = null

    public val appearance: Appearance = Appearance()
    public var bas: UnpackedBasType? by appearance::bas
    public var transmog: UnpackedNpcType? by appearance::transmog
    public var skullIcon: Int? by appearance::skullIcon
    public var overheadIcon: Int? by appearance::overheadIcon
    public val combatLevel: Int by appearance::combatLevel

    /**
     * Drop triggers enable extensibility for inv obj drop prevention.
     *
     * They are best suited for controlled, enclosed environments such as minigames, raids,
     * instances, or other scenarios tied to a specific area. The drop trigger is reset only when
     * the player drops an inventory obj or when explicitly cleared via [clearDropTrigger] or
     * [clearAnyDropTrigger].
     *
     * If there is no clear mechanism to reset the trigger - such as an `exit` function for a
     * minigame - there is no guarantee that the drop trigger will not become "out-of-date" until
     * the player drops an inventory object.
     *
     * _Note: Use the [Player.dropTrigger] function to set this value._
     */
    public var dropTrigger: DropTriggerType? = null
        private set

    public val isAccessProtected: Boolean
        get() = isBusy || activeCoroutine?.isSuspended == true

    public val isModalButtonProtected: Boolean
        get() = isDelayed || activeCoroutine?.isSuspended == true

    private val hasPendingQueue: Boolean
        get() = queueList.isNotEmpty || weakQueueList.isNotEmpty || engineQueueList.isNotEmpty

    public val canProcessMovement: Boolean
        get() = !isHaltMovementRequired()

    private fun isHaltMovementRequired(): Boolean {
        // It seems that only "old" interactions (active for > 1 cycle) will bypass this movement
        // restriction. This can be tested by starting an interaction and opening a modal on the
        // same cycle while having a pending queue. In that case, movement will be stopped until
        // the modal is closed. However, if you start an interaction and wait a cycle before
        // opening the modal, the player will not be halted.
        val hasOngoingInteraction = interaction != null && hasMovedPreviousCycle
        return !hasOngoingInteraction && ui.modals.isNotEmpty() && hasPendingQueue
    }

    @InternalApi
    public fun assignUid() {
        check(slotId != INVALID_SLOT) { "`slotId` must be set before assigning a uid." }
        val uuid = checkNotNull(uuid) { "`uuid` must be set before assigning a uid." }
        this.uid = PlayerUid(slotId, uuid)
    }

    @InternalApi
    public fun clearUid() {
        this.uid = PlayerUid.NULL
    }

    public fun walk(dest: CoordGrid) {
        abortRoute()
        moveSpeed = MoveSpeed.Walk
        routeDestination.add(dest)
    }

    public fun timer(timer: TimerType, cycles: Int) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        timerMap[timer] = currentMapClock + cycles
    }

    public fun softTimer(timer: TimerType, cycles: Int) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        softTimerMap[timer] = currentMapClock + cycles
    }

    public fun weakQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        weakQueueList.add(queue, QueueCategory.Weak, cycles, args)
    }

    public fun clearWeakQueue(queue: QueueType) {
        weakQueueList.removeAll(queue)
    }

    public fun softQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        queueList.add(queue, QueueCategory.Soft, cycles, args)
    }

    public fun queue(queue: QueueType, cycles: Int, args: Any? = null) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        queueList.add(queue, QueueCategory.Normal, cycles, args)
    }

    public fun strongQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        queueList.add(queue, QueueCategory.Strong, cycles, args)
    }

    public fun longQueueAccelerate(queue: QueueType, cycles: Int, args: Any? = null) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        queueList.add(queue, QueueCategory.LongAccelerate, cycles, args)
    }

    public fun longQueueDiscard(queue: QueueType, cycles: Int, args: Any? = null) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        queueList.add(queue, QueueCategory.LongDiscard, cycles, args)
    }

    public fun clearQueue(queue: QueueType) {
        queueList.removeAll(queue)
    }

    public fun changeStat(stat: StatType) {
        engineQueueList.add(EngineQueueType.ChangeStat, args = stat)
    }

    override fun anim(seq: SeqType, delay: Int, priority: Int) {
        PathingEntityCommon.anim(this, seq, delay, priority)
    }

    override fun spotanim(spot: SpotanimType, delay: Int, height: Int, slot: Int) {
        PathingEntityCommon.spotanim(this, spot, delay, height, slot)
    }

    public fun facePlayer(target: Player): Unit = PathingEntityCommon.facePlayer(this, target)

    public fun faceNpc(target: Npc): Unit = PathingEntityCommon.faceNpc(this, target)

    public fun resetFaceEntity(): Unit = PathingEntityCommon.resetFaceEntity(this)

    public fun say(text: String) {
        this.pendingSay = text
    }

    public fun showHeadbar(headbar: Headbar) {
        activeHeadbars.add(headbar.packed)
    }

    public fun showHitmark(hitmark: Hitmark) {
        activeHitmarks.add(hitmark.packed)
    }

    public fun rebuildAppearance() {
        appearance.rebuild = true
    }

    /**
     * @throws [IllegalStateException] if a [dropTrigger] is already set. This ensures that
     *   previously set drop triggers cannot be replaced unexpectedly or removed without explicit
     *   action.
     *
     * Features that set a drop trigger are responsible for clearing it using [clearDropTrigger] or
     * [clearAnyDropTrigger]. If a [dropTrigger] is still set, it may indicate the player exited
     * through unintended means, and an error will be thrown to prevent silent failures.
     */
    public fun dropTrigger(trigger: DropTriggerType) {
        check(dropTrigger == null) {
            "Previous `dropTrigger` must be removed before " +
                "setting a new trigger: oldTrigger=$dropTrigger, newTrigger=$trigger"
        }
        dropTrigger = trigger
    }

    /**
     * Clears [dropTrigger] as long as it matches [trigger], otherwise throws
     * [IllegalStateException].
     */
    public fun clearDropTrigger(trigger: DropTriggerType) {
        check(dropTrigger == trigger) {
            "Current `dropTrigger` does not match input: " +
                "currentTrigger=$dropTrigger, clearTrigger=$trigger"
        }
        dropTrigger = null
    }

    public fun clearAnyDropTrigger() {
        dropTrigger = null
    }

    override fun toString(): String =
        "Player(" +
            "username=$username, " +
            "displayName=$displayName, " +
            "coords=$coords, " +
            "coroutine=$activeCoroutine" +
            ")"
}
