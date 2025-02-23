package org.rsmod.api.player.protect

import com.github.michaelbull.logging.InlineLogger
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.invs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.queues
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invAddAll
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.invtx.invClear
import org.rsmod.api.invtx.invCompress
import org.rsmod.api.invtx.invDel
import org.rsmod.api.invtx.invMoveAll
import org.rsmod.api.invtx.invSwap
import org.rsmod.api.invtx.invTakeFee
import org.rsmod.api.invtx.invTransaction
import org.rsmod.api.invtx.invTransfer
import org.rsmod.api.invtx.select
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.cinematic.CameraMode
import org.rsmod.api.player.cinematic.Cinematic
import org.rsmod.api.player.cinematic.CompassState
import org.rsmod.api.player.cinematic.MinimapState
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.dialogue.Dialogues
import org.rsmod.api.player.input.ResumePCountDialogInput
import org.rsmod.api.player.input.ResumePObjDialogInput
import org.rsmod.api.player.input.ResumePauseButtonInput
import org.rsmod.api.player.interact.HeldInteractions
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.WornInteractions
import org.rsmod.api.player.output.Camera
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.ClientScripts
import org.rsmod.api.player.output.ClientScripts.chatDefaultRestoreInput
import org.rsmod.api.player.output.ClientScripts.mesLayerMode14
import org.rsmod.api.player.output.ClientScripts.mesLayerMode7
import org.rsmod.api.player.output.MapFlag
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.output.UpdateStat
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.output.jingle
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.output.spam
import org.rsmod.api.player.startInvTransmit
import org.rsmod.api.player.stat.PlayerSkillXP
import org.rsmod.api.player.stopInvTransmit
import org.rsmod.api.player.ui.ifChatNpcSpecific
import org.rsmod.api.player.ui.ifChatPlayer
import org.rsmod.api.player.ui.ifChoice
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifCloseSub
import org.rsmod.api.player.ui.ifConfirmDestroy
import org.rsmod.api.player.ui.ifConfirmOverlay
import org.rsmod.api.player.ui.ifConfirmOverlayClose
import org.rsmod.api.player.ui.ifDoubleobjbox
import org.rsmod.api.player.ui.ifMenu
import org.rsmod.api.player.ui.ifMesbox
import org.rsmod.api.player.ui.ifObjbox
import org.rsmod.api.player.ui.ifOpenFullOverlay
import org.rsmod.api.player.ui.ifOpenMain
import org.rsmod.api.player.ui.ifOpenMainModal
import org.rsmod.api.player.ui.ifOpenMainSidePair
import org.rsmod.api.player.ui.ifOpenOverlay
import org.rsmod.api.player.ui.ifOpenSub
import org.rsmod.api.player.ui.ifSetAnim
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.ui.ifSetNpcHead
import org.rsmod.api.player.ui.ifSetObj
import org.rsmod.api.player.ui.ifSetPlayerHead
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.player.vars.VarPlayerIntMapDelegate
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.api.player.worn.HeldEquipResult
import org.rsmod.api.player.worn.WornUnequipResult
import org.rsmod.api.random.GameRandom
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.route.RayCastValidator
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.api.utils.map.BuildAreaUtils
import org.rsmod.api.utils.skills.SkillingSuccessRate
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.events.EventBus
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.player.ProtectedAccessLostException
import org.rsmod.game.entity.util.PathingEntityCommon
import org.rsmod.game.interact.HeldOp
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.inv.Inventory
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.map.Direction
import org.rsmod.game.map.collision.get
import org.rsmod.game.map.collision.isZoneValid
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isType
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.jingle.JingleType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.mesanim.MesAnimType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.seq.UnpackedSeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.ui.Component
import org.rsmod.game.vars.VarPlayerStrMap
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds
import org.rsmod.objtx.TransactionResultList
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag

private val logger = InlineLogger()

public class ProtectedAccess(
    public val player: Player,
    private val coroutine: GameCoroutine,
    private val context: ProtectedAccessContext,
) {
    public val random: GameRandom by context::random

    public val coords: CoordGrid by player::coords
    public val mapClock: Int by player::currentMapClock

    public val inv: Inventory by player::inv
    public val worn: Inventory by player::worn
    public val bank: Inventory by lazy { inv(invs.bank) }
    public val tempInv: Inventory by lazy { inv(invs.tempinv) }

    public val vars: VarPlayerIntMapDelegate by lazy { VarPlayerIntMapDelegate.from(player) }
    public val strVars: VarPlayerStrMap by player::strVars

    public var actionDelay: Int by player::actionDelay
    public var skillAnimDelay: Int by player::skillAnimDelay
    public var refaceDelay: Int by player::refaceDelay

    private var opHeldCallCount = 0

    public fun walk(dest: CoordGrid) {
        player.abortRoute()
        player.routeDestination.add(dest)
    }

    /**
     * Queues a route towards [dest] and delays the player (suspending this call-site) based on the
     * distance to [dest] and the "step rate" of [MoveSpeed.Walk].
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     */
    public suspend fun playerWalk(dest: CoordGrid): Unit = playerMove(dest, MoveSpeed.Walk)

    /**
     * Queues a route towards [dest] and delays the player (suspending this call-site) based on the
     * distance to [dest] and the "step rate" of [MoveSpeed.Run].
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     */
    public suspend fun playerRun(dest: CoordGrid): Unit = playerMove(dest, MoveSpeed.Run)

    /**
     * Queues a route to [dest] and delays the player (suspending this call-site) based on the
     * distance to [dest] and the "step rate" of [moveSpeed].
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     */
    public suspend fun playerMove(dest: CoordGrid, moveSpeed: MoveSpeed = player.varMoveSpeed) {
        if (coords != dest) {
            walk(dest)
            player.moveSpeed = moveSpeed
        }

        val distance = coords.chebyshevDistance(dest)
        if (moveSpeed == MoveSpeed.Run && distance <= 3) {
            return
        }

        val distanceDelay = (distance - 1) / max(1, moveSpeed.steps)
        if (distanceDelay > 0) {
            delay(distanceDelay)
        }
    }

    /**
     * Similar to [playerWalk], but ensures a minimum delay of 1 cycle, regardless of distance.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [playerWalk]
     */
    public suspend fun playerWalkWithMinDelay(dest: CoordGrid): Unit =
        playerMoveWithMinDelay(dest, MoveSpeed.Walk)

    /**
     * Similar to [playerRun], but ensures a minimum delay of 1 cycle, regardless of distance.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [playerRun]
     */
    public suspend fun playerRunWithMinDelay(dest: CoordGrid): Unit =
        playerMoveWithMinDelay(dest, MoveSpeed.Run)

    /**
     * Similar to [playerMove], but ensures a minimum delay of 1 cycle, regardless of distance.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [playerMove]
     */
    public suspend fun playerMoveWithMinDelay(
        dest: CoordGrid,
        moveSpeed: MoveSpeed = player.varMoveSpeed,
    ) {
        if (coords != dest) {
            walk(dest)
            player.moveSpeed = moveSpeed
        }

        val distanceDelay = (coords.chebyshevDistance(dest) - 1) / max(1, moveSpeed.steps)
        delay(max(1, distanceDelay))
    }

    public fun telejump(dest: CoordGrid, collision: CollisionFlagMap = context.collision) {
        if (!collision.isZoneValid(dest)) {
            player.clearMapFlag()
            mes("Invalid teleport!", ChatType.Engine)
            return
        }
        PathingEntityCommon.telejump(player, collision, dest)
    }

    public fun teleport(dest: CoordGrid, collision: CollisionFlagMap = context.collision) {
        if (!collision.isZoneValid(dest)) {
            player.clearMapFlag()
            mes("Invalid teleport!", ChatType.Engine)
            return
        }
        PathingEntityCommon.teleport(player, collision, dest)
    }

    public fun anim(seq: SeqType, delay: Int = 0) {
        player.anim(seq, delay)
    }

    public fun resetAnim() {
        player.resetAnim()
    }

    public fun animProtect(animProtect: Boolean) {
        PathingEntityCommon.setAnimProtect(player, animProtect)
    }

    public fun resetSpotanim() {
        player.resetSpotanim()
    }

    public fun spotanim(spot: SpotanimType, delay: Int = 0, height: Int = 0, slot: Int = 0) {
        player.spotanim(spot, delay, height, slot)
    }

    public fun say(text: String) {
        player.say(text)
    }

    public fun transmog(npcType: NpcType, npcTypeList: NpcTypeList = context.npcTypes) {
        player.transmog = npcTypeList[npcType]
    }

    public fun resetTransmog() {
        player.transmog = null
    }

    public fun rebuildAppearance() {
        player.rebuildAppearance()
    }

    public fun isWithinDistance(
        target: CoordGrid,
        distance: Int,
        width: Int = 1,
        length: Int = 1,
    ): Boolean = player.isWithinDistance(target, distance, width, length)

    public fun isWithinDistance(other: PathingEntity, distance: Int): Boolean =
        player.isWithinDistance(other, distance)

    public fun isWithinDistance(loc: BoundLocInfo, distance: Int): Boolean =
        player.isWithinDistance(loc, distance)

    public fun isWithinArea(southWest: CoordGrid, northEast: CoordGrid): Boolean =
        player.isWithinArea(southWest, northEast)

    public fun distanceTo(target: CoordGrid, width: Int = 1, length: Int = 1): Int =
        player.distanceTo(target, width, length)

    public fun distanceTo(other: PathingEntity): Int = player.distanceTo(other)

    public fun distanceTo(loc: BoundLocInfo): Int = player.distanceTo(loc)

    public fun apRange(distance: Int) {
        val interaction = player.interaction ?: return
        interaction.apRange = distance
        interaction.apRangeCalled = true
    }

    public fun isWithinApRange(loc: BoundLocInfo, distance: Int): Boolean {
        if (!isWithinDistance(loc, distance)) {
            apRange(distance)
            return false
        }
        return true
    }

    public fun isWithinApRange(target: PathingEntity, distance: Int): Boolean {
        if (!isWithinDistance(target, distance)) {
            apRange(distance)
            return false
        }
        return true
    }

    public fun mapFindSquareLineOfWalk(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
        validator: RayCastValidator = RayCastValidator(context.collision),
    ): CoordGrid? {
        val squares = validatedLineOfWalkSquares(centre, minRadius, maxRadius, validator)
        return squares.firstOrNull()
    }

    public fun mapFindSquareLineOfSight(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
        validator: RayCastValidator = RayCastValidator(context.collision),
    ): CoordGrid? {
        val squares = validatedLineOfSightSquares(centre, minRadius, maxRadius, validator)
        return squares.firstOrNull()
    }

    public fun validatedLineOfWalkSquares(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
        validator: RayCastValidator = RayCastValidator(context.collision),
    ): Sequence<CoordGrid> {
        val squares = shuffledSquares(centre, minRadius, maxRadius)
        return squares.filter {
            validator.hasLineOfWalk(centre, it, extraFlag = CollisionFlag.BLOCK_PLAYERS)
        }
    }

    public fun validatedLineOfSightSquares(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
        validator: RayCastValidator = RayCastValidator(context.collision),
    ): Sequence<CoordGrid> {
        val squares = shuffledSquares(centre, minRadius, maxRadius)
        return squares.filter {
            validator.hasLineOfSight(centre, it, extraFlag = CollisionFlag.BLOCK_PLAYERS)
        }
    }

    public fun validatedSquares(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
        collision: CollisionFlagMap = context.collision,
    ): Sequence<CoordGrid> {
        val squares = shuffledSquares(centre, minRadius, maxRadius)
        return squares.filter {
            val flag = collision[coords]
            flag and CollisionFlag.BLOCK_WALK == 0
        }
    }

    private fun shuffledSquares(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
    ): Sequence<CoordGrid> {
        require(minRadius <= maxRadius) {
            "`minRadius` must be less than or equal to `maxRadius`. " +
                "(centre=$centre, minRadius=$minRadius, maxRadius=$maxRadius)"
        }
        val base = centre.translate(-maxRadius, -maxRadius)
        val bounds = Bounds(base, 2 * maxRadius + 1, 2 * maxRadius + 1)
        return bounds.shuffled().filter { centre.chebyshevDistance(it) >= minRadius }
    }

    public fun lineOfWalk(
        from: CoordGrid,
        to: CoordGrid,
        validator: RayCastValidator = RayCastValidator(context.collision),
    ): Boolean = validator.hasLineOfWalk(from, to, extraFlag = CollisionFlag.BLOCK_PLAYERS)

    public fun lineOfSight(
        from: CoordGrid,
        to: CoordGrid,
        validator: RayCastValidator = RayCastValidator(context.collision),
    ): Boolean = validator.hasLineOfSight(from, to, extraFlag = CollisionFlag.BLOCK_PLAYERS)

    /**
     * Returns `true` if there is a valid line of walk from [from] to **every** coordinate occupied
     * by [bounds]; otherwise, returns `false`.
     */
    public fun lineOfWalk(
        from: CoordGrid,
        bounds: Bounds,
        validator: RayCastValidator = RayCastValidator(context.collision),
    ): Boolean {
        val squares = bounds.asSequence()
        return squares.all {
            validator.hasLineOfWalk(from, it, extraFlag = CollisionFlag.BLOCK_PLAYERS)
        }
    }

    /**
     * Returns `true` if there is a valid line of sight from [from] to **every** coordinate occupied
     * by [bounds]; otherwise, returns `false`.
     */
    public fun lineOfSight(
        from: CoordGrid,
        bounds: Bounds,
        validator: RayCastValidator = RayCastValidator(context.collision),
    ): Boolean {
        val squares = bounds.asSequence()
        return squares.all {
            validator.hasLineOfSight(from, it, extraFlag = CollisionFlag.BLOCK_PLAYERS)
        }
    }

    public fun opLoc1(
        loc: BoundLocInfo,
        interactions: LocInteractions = context.locInteractions,
    ): Unit = interactions.interact(player, loc, InteractionOp.Op1)

    public fun opLoc2(
        loc: BoundLocInfo,
        interactions: LocInteractions = context.locInteractions,
    ): Unit = interactions.interact(player, loc, InteractionOp.Op2)

    public fun opLoc3(
        loc: BoundLocInfo,
        interactions: LocInteractions = context.locInteractions,
    ): Unit = interactions.interact(player, loc, InteractionOp.Op3)

    public fun opLoc4(
        loc: BoundLocInfo,
        interactions: LocInteractions = context.locInteractions,
    ): Unit = interactions.interact(player, loc, InteractionOp.Op4)

    /**
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [checkOpHeldCallLimit]
     */
    public suspend fun opHeld1(
        invSlot: Int,
        inv: Inventory = player.inv,
        interactions: HeldInteractions = context.heldInteractions,
    ) {
        checkOpHeldCallLimit()
        interactions.interact(this, inv, invSlot, HeldOp.Op1)
    }

    /**
     * Note: If you wish to directly equip an obj bypassing `onOpHeld2` scripts you will need to
     * call [invEquip] instead.
     *
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [checkOpHeldCallLimit]
     * @see [HeldOp.Op2]
     */
    public suspend fun opHeld2(
        invSlot: Int,
        inv: Inventory = player.inv,
        interactions: HeldInteractions = context.heldInteractions,
    ) {
        checkOpHeldCallLimit()
        interactions.interact(this, inv, invSlot, HeldOp.Op2)
    }

    /**
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [checkOpHeldCallLimit]
     */
    public suspend fun opHeld3(
        invSlot: Int,
        inv: Inventory = player.inv,
        interactions: HeldInteractions = context.heldInteractions,
    ) {
        checkOpHeldCallLimit()
        interactions.interact(this, inv, invSlot, HeldOp.Op3)
    }

    /**
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [checkOpHeldCallLimit]
     */
    public suspend fun opHeld4(
        invSlot: Int,
        inv: Inventory = player.inv,
        interactions: HeldInteractions = context.heldInteractions,
    ) {
        checkOpHeldCallLimit()
        interactions.interact(this, inv, invSlot, HeldOp.Op4)
    }

    /**
     * Note: If you wish to directly drop an obj bypassing any custom scripts you will need to call
     * [invDrop] instead.
     *
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [HeldOp.Op5]
     * @see [checkOpHeldCallLimit]
     */
    public suspend fun opHeld5(
        invSlot: Int,
        inv: Inventory = player.inv,
        interactions: HeldInteractions = context.heldInteractions,
    ) {
        checkOpHeldCallLimit()
        interactions.interact(this, inv, invSlot, HeldOp.Op5)
    }

    /**
     * Note: This function will bypass any `onOpHeld2` scripts attached to the respective obj and
     * will attempt to directly equip it instead. Use [opHeld2] if you wish to avoid this behavior.
     *
     * @return [HeldEquipResult] with result of the attempt to equip respective obj.
     * @see [HeldInteractions.equip]
     */
    public fun invEquip(
        invSlot: Int,
        inv: Inventory = player.inv,
        interactions: HeldInteractions = context.heldInteractions,
    ): HeldEquipResult = interactions.equip(this, inv, invSlot)

    /**
     * Note: This function will bypass any `onOpHeld5` scripts attached to the respective obj and
     * will attempt to directly drop it instead. Use [opHeld5] if you wish to avoid this behavior.
     *
     * @see [HeldInteractions.drop]
     */
    public suspend fun invDrop(
        invSlot: Int,
        inv: Inventory = player.inv,
        interactions: HeldInteractions = context.heldInteractions,
    ) {
        checkOpHeldCallLimit()
        interactions.drop(this, inv, invSlot)
    }

    /**
     * Note: This function will bypass any `onOpWorn1` scripts attached to the respective obj and
     * will attempt to directly unequip it instead.
     *
     * @return [WornUnequipResult] with result of the attempt to unequip respective obj.
     * @see [WornInteractions.unequip]
     */
    public fun wornUnequip(
        wornSlot: Int,
        into: Inventory = player.inv,
        from: Inventory = player.worn,
        interactions: WornInteractions = context.wornInteractions,
    ): WornUnequipResult = interactions.unequip(this, from, wornSlot, into)

    public fun faceSquare(target: CoordGrid): Unit = player.faceSquare(target)

    public fun faceDirection(direction: Direction): Unit = player.faceDirection(direction)

    public fun faceLoc(loc: BoundLocInfo): Unit = player.faceLoc(loc)

    public fun faceEntitySquare(target: PathingEntity): Unit =
        player.facePathingEntitySquare(target)

    public fun stopAction(eventBus: EventBus = context.eventBus) {
        player.clearPendingAction(eventBus)
        player.resetFaceEntity()
        player.clearMapFlag()
        player.abortRoute()
    }

    public fun invTransmit(inv: Inventory): Unit = player.startInvTransmit(inv)

    public fun invStopTransmit(inv: Inventory): Unit = player.stopInvTransmit(inv)

    public fun invAdd(
        inv: Inventory,
        obj: InvObj,
        slot: Int? = null,
        strict: Boolean = true,
        cert: Boolean = false,
        uncert: Boolean = false,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> =
        player.invAdd(
            inv = inv,
            obj = obj.id,
            count = obj.count,
            vars = obj.vars,
            slot = slot,
            strict = strict,
            cert = cert,
            uncert = uncert,
            autoCommit = autoCommit,
        )

    public fun invAdd(
        inv: Inventory,
        type: ObjType,
        count: Int = 1,
        vars: Int = 0,
        slot: Int? = null,
        strict: Boolean = true,
        cert: Boolean = false,
        uncert: Boolean = false,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> =
        player.invAdd(
            inv = inv,
            type = type,
            count = count,
            vars = vars,
            slot = slot,
            strict = strict,
            cert = cert,
            uncert = uncert,
            autoCommit = autoCommit,
        )

    /**
     * Attempts to add exactly [count] of [obj] into [inv]. If the inventory cannot fit the items,
     * they will instead be dropped on the floor, with [player] as the "owner," and this function
     * will return `false`. If the items are successfully placed in [inv], it returns `true`.
     */
    public fun invAddOrDrop(
        repo: ObjRepository,
        obj: ObjType,
        count: Int = 1,
        coords: CoordGrid = this.coords,
        inv: Inventory = this.inv,
    ): Boolean = player.invAddOrDrop(repo, obj, count, coords = coords, inv = inv)

    public fun invDel(
        inv: Inventory,
        type: ObjType,
        count: Int = 1,
        slot: Int? = null,
        strict: Boolean = true,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> =
        player.invDel(
            inv = inv,
            type = type,
            count = count,
            slot = slot,
            strict = strict,
            autoCommit = autoCommit,
        )

    /**
     * This transaction will remove the first found inv obj associated with [replace] based on their
     * slot.
     *
     * If [count] amount of the inv obj could not be deleted, this transaction will fail.
     *
     * _Note: This function will add the [replacement] obj in the first empty and valid slot._
     */
    public fun invReplace(
        inv: Inventory,
        replace: ObjType,
        count: Int,
        replacement: ObjType,
        vars: Int = 0,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        return player.invTransaction(inv, autoCommit) {
            val fromInv = select(inv)
            delete {
                this.from = fromInv
                this.obj = replace.id
                this.strictCount = count
            }
            insert {
                this.into = fromInv
                this.obj = replacement.id
                this.strictCount = count
                this.vars = vars
            }
        }
    }

    /**
     * This transaction will remove the inv obj occupying slot [slot], resulting in failure if there
     * is no obj in said `slot`, or if there are any other implicit transaction errors.
     *
     * _Note: This function will add the new [replacement] obj in the first empty and valid slot. If
     * you wish to add the item into [slot] instead, use [invReplaceSlot]._
     *
     * @see [invReplaceSlot]
     */
    public fun invReplace(
        inv: Inventory,
        slot: Int,
        count: Int,
        replacement: ObjType,
        vars: Int = 0,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        // The transaction will implicitly fail if the obj is null - no verification is required
        // at this level.
        val deleteObj = inv[slot]
        return player.invTransaction(inv, autoCommit) {
            val fromInv = select(inv)
            delete {
                this.from = fromInv
                this.obj = deleteObj?.id
                this.strictCount = count
                this.strictSlot = slot
            }
            insert {
                this.into = fromInv
                this.obj = replacement.id
                this.strictCount = count
                this.vars = vars
            }
        }
    }

    /**
     * This transaction will remove the inv obj occupying slot [slot], resulting in failure if there
     * is no obj in said `slot`, or if there are any other implicit transaction errors.
     *
     * _Note: This function will strictly add the new [replacement] obj in the [slot] slot. If you
     * wish for the obj to take the first available slot instead, use [invReplace]._
     *
     * @see [invReplace]
     */
    public fun invReplaceSlot(
        inv: Inventory,
        slot: Int,
        count: Int,
        replacement: ObjType,
        vars: Int = 0,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        // The transaction will implicitly fail if the obj is null - no verification is required
        // at this level.
        val deleteObj = inv[slot]
        return player.invTransaction(inv, autoCommit) {
            val fromInv = select(inv)
            delete {
                this.from = fromInv
                this.obj = deleteObj?.id
                this.strictCount = count
                this.strictSlot = slot
            }
            insert {
                this.into = fromInv
                this.obj = replacement.id
                this.strictSlot = slot
                this.strictCount = count
                this.vars = vars
            }
        }
    }

    public fun invMoveToSlot(
        from: Inventory,
        into: Inventory,
        fromSlot: Int,
        intoSlot: Int,
        strict: Boolean = true,
    ): TransactionResultList<InvObj> {
        val resolvedInto = if (from === into) null else into
        return player.invSwap(
            from = from,
            into = resolvedInto,
            fromSlot = fromSlot,
            intoSlot = intoSlot,
            strict = strict,
        )
    }

    public fun invMoveFromSlot(
        from: Inventory,
        into: Inventory,
        fromSlot: Int,
        count: Int = 1,
        intoSlot: Int? = null,
        strict: Boolean = true,
        cert: Boolean = false,
        uncert: Boolean = false,
        placehold: Boolean = false,
    ): TransactionResultList<InvObj> =
        player.invTransfer(
            from = from,
            into = into,
            count = count,
            fromSlot = fromSlot,
            intoSlot = intoSlot,
            strict = strict,
            cert = cert,
            uncert = uncert,
            placehold = placehold,
        )

    public fun invMoveInv(
        from: Inventory,
        into: Inventory,
        untransform: Boolean = false,
        intoStartSlot: Int = 0,
        intoCapacity: Int? = null,
        keepSlots: Set<Int>? = null,
    ): TransactionResultList<InvObj> =
        player.invMoveAll(
            from = from,
            into = into,
            untransform = untransform,
            intoStartSlot = intoStartSlot,
            intoCapacity = intoCapacity,
            keepSlots = keepSlots,
        )

    public fun invMoveAll(
        into: Inventory,
        objs: Iterable<InvObj>,
        startSlot: Int? = null,
        strict: Boolean = true,
        cert: Boolean = false,
        uncert: Boolean = false,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> =
        player.invAddAll(
            inv = into,
            objs = objs,
            startSlot = startSlot,
            strict = strict,
            cert = cert,
            uncert = uncert,
            autoCommit = autoCommit,
        )

    public fun invCompress(inventory: Inventory): TransactionResultList<InvObj> =
        player.invCompress(inventory)

    public fun invClear(inventory: Inventory) {
        player.invClear(inventory)
    }

    public fun objExamine(
        inventory: Inventory,
        slot: Int,
        marketPrices: MarketPrices = context.marketPrices,
        objTypes: ObjTypeList = context.objTypes,
    ) {
        val obj = inventory[slot] ?: return resendSlot(inventory, 0)
        val normalized = objTypes.normalize(objTypes[obj])
        player.objExamine(normalized, obj.count, marketPrices[normalized] ?: 0)
    }

    public fun stat(stat: StatType): Int {
        return player.statMap.getCurrentLevel(stat).toInt()
    }

    public fun statBase(stat: StatType): Int {
        return player.statMap.getBaseLevel(stat).toInt()
    }

    public fun statAdvance(
        stat: StatType,
        xp: Double,
        rate: Double = player.xpRate,
        eventBus: EventBus = context.eventBus,
    ): Int {
        val startLevel = player.statMap.getBaseLevel(stat)
        val addedXp = PlayerSkillXP.internalAddXP(player, stat, xp, rate, eventBus)
        val endLevel = player.statMap.getBaseLevel(stat)
        if (startLevel != endLevel) {
            // TODO: Engine queue for changestat
        }
        return addedXp
    }

    /**
     * #### Warning
     * Increases the player's stat level based on their **current** level. Use [statBoost] if you
     * wish to increase levels based on the **base** level instead.
     *
     * Note: This function ensures that the player's stat level does not exceed `255`.
     *
     * @throws IllegalArgumentException if [constant] is negative (use `statSub` instead), or if
     *   [percent] is not within the range `0..100`.
     */
    public fun statAdd(stat: StatType, constant: Int, percent: Int) {
        require(constant >= 0) { "Constant `$constant` must be positive. Use `statSub` instead." }
        require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

        val current = player.statMap.getCurrentLevel(stat).toInt()
        val calculated = current + (constant + (current * percent) / 100)
        val cappedLevel = min(255, calculated)

        player.statMap.setCurrentLevel(stat, cappedLevel.toByte())
        updateStat(stat)

        if (cappedLevel != current) {
            // TODO: Engine queue for changestat
        }
    }

    /**
     * Increases the player's stat level based on their **base** level.
     *
     * Note: This function ensures that the player's stat level does not exceed `255`.
     *
     * @throws IllegalArgumentException if [constant] is negative (use `statDrain` if required), or
     *   if [percent] is not within range of `0` to `100`.
     */
    public fun statBoost(stat: StatType, constant: Int, percent: Int) {
        require(constant >= 0) { "Constant `$constant` must be positive. Use `statDrain` instead." }
        require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

        val base = player.statMap.getBaseLevel(stat).toInt()
        val boost = constant + (base * percent) / 100

        val current = player.statMap.getCurrentLevel(stat).toInt()
        val cappedBoost = min(base + boost, current + boost) - current

        statAdd(stat, cappedBoost, 0)
    }

    /**
     * #### Warning
     * Decreases the player's stat level based on their **current** level. Use [statDrain] if you
     * wish to decrease levels based on the **base** level instead.
     *
     * Note: This function ensures that the player's stat level does not fall below `0`.
     *
     * @throws IllegalArgumentException if [constant] is negative, or if [percent] is not within the
     *   range `0..100`.
     */
    public fun statSub(stat: StatType, constant: Int, percent: Int) {
        require(constant >= 0) { "Constant `$constant` must be positive." }
        require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

        val current = player.statMap.getCurrentLevel(stat).toInt()
        val calculated = current - (constant + (current * percent) / 100)
        val cappedLevel = max(0, calculated)

        player.statMap.setCurrentLevel(stat, cappedLevel.toByte())
        updateStat(stat)

        if (cappedLevel != current) {
            // TODO: Engine queue for changestat
        }
    }

    /**
     * Decreases the player's stat level based on their **base** level.
     *
     * Note: This function ensures that the player's stat level does not fall below `0`.
     *
     * @throws IllegalArgumentException if [constant] is negative (use `statAdd` if required), or if
     *   [percent] is not within range of `0` to `100`.
     */
    public fun statDrain(stat: StatType, constant: Int, percent: Int) {
        require(constant >= 0) { "Constant `$constant` must be positive." }
        require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

        val base = player.statMap.getBaseLevel(stat).toInt()
        val drain = constant + (base * percent) / 100

        val current = player.statMap.getCurrentLevel(stat).toInt()
        val cappedDrain = current - min(base - drain, current - drain)

        statSub(stat, cappedDrain, 0)
    }

    /**
     * Restores the player's stat level towards their **base** level.
     *
     * This function increases the player's stat level by a combination of a constant value and a
     * percentage of their **current** level. The restored level will never exceed the player's base
     * level and will not decrease their current level.
     *
     * Note: This function is commonly used to recover from temporary stat reductions or provide
     * partial stat restoration.
     *
     * #### Example
     * If a player's base level for a stat is `99` and their current level is `80`, calling
     * `statHeal(stat, constant = 10, percent = 20)` will restore the stat by `10 + (80 * 20%) =
     * 26`, but it will be capped at the base level of `99`.
     *
     * @throws IllegalArgumentException if [constant] is negative, or if [percent] is not within the
     *   range `0..100`.
     */
    public fun statHeal(stat: StatType, constant: Int, percent: Int) {
        require(constant >= 0) { "Constant `$constant` must be positive." }
        require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

        val base = player.statMap.getBaseLevel(stat).toInt()
        val current = player.statMap.getCurrentLevel(stat).toInt()
        val calculated = current + (constant + (current * percent) / 100)
        val cappedLevel = calculated.coerceIn(current, base)

        player.statMap.setCurrentLevel(stat, cappedLevel.toByte())
        updateStat(stat)

        if (cappedLevel != current) {
            // TODO: Engine queue for changestat
        }
    }

    private fun updateStat(stat: StatType) {
        val currXp = player.statMap.getXP(stat)
        val currLvl = player.statMap.getCurrentLevel(stat).toInt()
        UpdateStat.update(player, stat, currXp, currLvl, currLvl)
    }

    public fun rollSuccessRate(low: Int, high: Int, level: Int, maxLevel: Int): Boolean {
        val rate = SkillingSuccessRate.successRate(low, high, level, maxLevel)
        return rate > random.randomDouble()
    }

    public fun rollSuccessRate(low: Int, high: Int, stat: StatType, invisibleBoost: Int): Boolean {
        val visibleLevel = player.statMap.getCurrentLevel(stat).toInt()
        val level = visibleLevel.coerceIn(1, stat.maxLevel) + invisibleBoost
        return rollSuccessRate(low, high, level, stat.maxLevel)
    }

    public fun rollSuccessRate(
        low: Int,
        high: Int,
        stat: StatType,
        invisibleLevels: InvisibleLevels,
    ): Boolean {
        val invisibleBoost = invisibleLevels.get(player, stat)
        return rollSuccessRate(low, high, stat, invisibleBoost)
    }

    public fun timer(timerType: TimerType, cycles: Int) {
        player.timer(timerType, cycles)
    }

    public fun softTimer(timerType: TimerType, cycles: Int) {
        player.softTimer(timerType, cycles)
    }

    public fun weakQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        player.weakQueue(queue, cycles, args)
    }

    public fun softQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        player.softQueue(queue, cycles, args)
    }

    public fun queue(queue: QueueType, cycles: Int, args: Any? = null) {
        player.queue(queue, cycles, args)
    }

    public fun strongQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        player.strongQueue(queue, cycles, args)
    }

    public fun longQueueAccelerate(queue: QueueType, cycles: Int, args: Any? = null) {
        player.longQueueAccelerate(queue, cycles, args)
    }

    public fun longQueueDiscard(queue: QueueType, cycles: Int, args: Any? = null) {
        player.longQueueDiscard(queue, cycles, args)
    }

    public fun clearPendingAction(eventBus: EventBus = context.eventBus) {
        player.clearPendingAction(eventBus)
    }

    public fun publish(event: UnboundEvent, eventBus: EventBus = context.eventBus) {
        eventBus.publish(event)
    }

    public fun publish(event: KeyedEvent, eventBus: EventBus = context.eventBus) {
        eventBus.publish(event)
    }

    public suspend fun <T : SuspendEvent<ProtectedAccess>> publish(
        event: T,
        eventBus: EventBus = context.eventBus,
    ): Boolean = eventBus.publish(this, event)

    public fun logOut() {
        // TODO: impl
    }

    public suspend fun startDialogue(
        npc: Npc,
        faceFar: Boolean = false,
        dialogues: Dialogues = context.dialogues,
        conversation: suspend Dialogue.() -> Unit,
    ): Unit = dialogues.start(this, npc, faceFar, conversation)

    public suspend fun startDialogue(
        dialogues: Dialogues = context.dialogues,
        conversation: suspend Dialogue.() -> Unit,
    ): Unit = dialogues.start(this, conversation)

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun delay(cycles: Int = 1) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        player.delay(cycles)
        coroutine.pause { player.isNotDelayed }
        regainProtectedAccess()
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun arriveDelay() {
        if (!player.hasMovedPreviousCycle) {
            return
        }
        delay()
        regainProtectedAccess()
    }

    /**
     * Delays the player for a number of ticks equal to the time duration of [seq].
     *
     * @param seq The seq type whose tick duration determines the delay.
     * @throws IllegalStateException if [UnpackedSeqType.tickDuration] is `0`.
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun delay(seq: SeqType, seqTypes: SeqTypeList = context.seqTypes) {
        val ticks = seqTypes[seq].tickDuration
        check(ticks > 0) { "Seq tick duration must be positive: ${seqTypes[seq]}" }
        delay(cycles = ticks)
    }

    /**
     * Delays the player for up to `10` cycles or until the `MapBuildComplete` packet is received,
     * whichever happens first.
     *
     * _Note: This function does not delay or suspend if the player's **current** coords do not
     * require rebuilding their build area._
     *
     * @see [BuildAreaUtils.requiresNewBuildArea]
     * @see [delay]
     */
    public suspend fun loadDelay() {
        val requiresBuildAreaRebuild = BuildAreaUtils.requiresNewBuildArea(player)
        if (!requiresBuildAreaRebuild) {
            return
        }
        for (i in 0 until 10) {
            if (player.lastMapBuildComplete >= mapClock) {
                break
            }
            delay(1)
        }
    }

    /**
     * Suspends the [coroutine] until the player receives the respective [input].
     *
     * _Note: This does not `delay` the player._
     *
     * @param input the expected input type to suspend on, provided as a [KClass].
     * @return the input value of type [T] once received.
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun <T : Any> await(input: KClass<T>): T {
        val modal = player.ui.getModalOrNull(components.main_modal)
        val value = coroutine.pause(input)
        return resumeWithMainModalProtectedAccess(value, modal)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun mesbox(
        text: String,
        lineHeight: Int,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.ifMesbox(text, pauseText, lineHeight, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.text_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun objbox(
        obj: ObjType,
        zoom: Int,
        text: String,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.ifObjbox(text, obj.id, zoom, pauseText, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.obj_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun objbox(
        obj: InvObj,
        zoomOrCount: Int,
        text: String,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.ifObjbox(text, obj.id, zoomOrCount, pauseText, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.obj_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun doubleobjbox(
        obj1: ObjType,
        zoom1: Int,
        obj2: ObjType,
        zoom2: Int,
        text: String,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.ifDoubleobjbox(text, obj1.id, zoom1, obj2.id, zoom2, pauseText, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.double_obj_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun doubleobjbox(
        obj1: InvObj,
        zoom1: Int,
        obj2: InvObj,
        zoom2: Int,
        text: String,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.ifDoubleobjbox(text, obj1.id, zoom1, obj2.id, zoom2, pauseText, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.double_obj_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun <T> choice2(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        title: String = constants.cm_options,
        eventBus: EventBus = context.eventBus,
    ): T {
        player.ifChoice(title, "$choice1|$choice2", choiceCountInclusive = 2, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.options_dialogue_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun <T> choice3(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        choice3: String,
        result3: T,
        title: String = constants.cm_options,
        eventBus: EventBus = context.eventBus,
    ): T {
        player.ifChoice(title, "$choice1|$choice2|$choice3", choiceCountInclusive = 3, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.options_dialogue_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            3 -> result3
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun <T> choice4(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        choice3: String,
        result3: T,
        choice4: String,
        result4: T,
        title: String = constants.cm_options,
        eventBus: EventBus = context.eventBus,
    ): T {
        player.ifChoice(
            title,
            "$choice1|$choice2|$choice3|$choice4",
            choiceCountInclusive = 4,
            eventBus,
        )
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.options_dialogue_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            3 -> result3
            4 -> result4
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun <T> choice5(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        choice3: String,
        result3: T,
        choice4: String,
        result4: T,
        choice5: String,
        result5: T,
        title: String = constants.cm_options,
        eventBus: EventBus = context.eventBus,
    ): T {
        player.ifChoice(
            title,
            "$choice1|$choice2|$choice3|$choice4|$choice5",
            choiceCountInclusive = 5,
            eventBus,
        )
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.options_dialogue_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            3 -> result3
            4 -> result4
            5 -> result5
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatPlayer(
        text: String,
        mesanim: MesAnimType?,
        lineCount: Int,
        lineHeight: Int,
        title: String = player.displayName,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        val chatanim = mesanim?.splitGetAnim(lineCount)
        player.ifChatPlayer(title, text, chatanim, pauseText, lineHeight, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.player_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatNpc(
        title: String,
        npc: Npc,
        text: String,
        mesanim: MesAnimType?,
        lineCount: Int,
        lineHeight: Int,
        faceFar: Boolean = false,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        npc.playerFace(player, faceFar = faceFar)
        player.facePathingEntitySquare(npc)

        val chatanim = mesanim?.splitGetAnim(lineCount)
        player.ifChatNpcSpecific(title, npc.type, text, chatanim, pauseText, lineHeight, eventBus)

        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.npc_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatNpcNoTurn(
        title: String,
        npc: Npc,
        text: String,
        mesanim: MesAnimType?,
        lineCount: Int,
        lineHeight: Int,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.facePathingEntitySquare(npc)

        val chatanim = mesanim?.splitGetAnim(lineCount)
        player.ifChatNpcSpecific(title, npc.type, text, chatanim, pauseText, lineHeight, eventBus)

        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.npc_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatNpcSpecific(
        title: String,
        type: NpcType,
        text: String,
        mesanim: MesAnimType?,
        lineCount: Int,
        lineHeight: Int,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        val chatanim = mesanim?.splitGetAnim(lineCount)
        player.ifChatNpcSpecific(title, type, text, chatanim, pauseText, lineHeight, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.npc_dialogue_pbutton)
    }

    /**
     * **Note:** The returned integer will _always_ be positive. To allow negative values, use
     * [numberDialog] instead.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun countDialog(title: String = constants.cm_count): Int {
        mesLayerMode7(player, title)
        val modal = player.ui.getModalOrNull(components.main_modal)
        val input = coroutine.pause(ResumePCountDialogInput::class)
        return resumeWithMainModalProtectedAccess(input.count.absoluteValue, modal)
    }

    /**
     * A version of [countDialog] that allows the returned value to be negative.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun numberDialog(title: String): Int {
        mesLayerMode7(player, title)
        val modal = player.ui.getModalOrNull(components.main_modal)
        val input = coroutine.pause(ResumePCountDialogInput::class)
        return resumeWithMainModalProtectedAccess(input.count, modal)
    }

    /**
     * @param stockMarketRestriction If `true` the search will be restricted to only objs that can
     *   be found in the grand exchange.
     * @param enumRestriction If an enum (with key of `ObjType` and value of `Boolean`) is provided,
     *   the search is restricted to only the entries in said enum.
     * @param showLastSearched If `true` the search will present the last selected obj.
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun objDialog(
        title: String = constants.cm_obj,
        stockMarketRestriction: Boolean = true,
        enumRestriction: EnumType<ObjType, Boolean>? = null,
        showLastSearched: Boolean = false,
    ): UnpackedObjType {
        mesLayerMode14(player, title, stockMarketRestriction, enumRestriction, showLastSearched)
        val modal = player.ui.getModalOrNull(components.main_modal)
        val input = coroutine.pause(ResumePObjDialogInput::class)
        return resumeWithMainModalProtectedAccess(input.obj, modal)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun confirmDestroy(
        obj: ObjType,
        count: Int,
        header: String,
        text: String,
        eventBus: EventBus = context.eventBus,
    ): Boolean {
        player.ifConfirmDestroy(header, text, obj.id, count, eventBus)
        val modal = player.ui.getModalOrNull(components.chat_dialogue_target)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.destroy_obj_dialogue_pbutton)
        return when (input.subcomponent) {
            0 -> false
            1 -> true
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun confirmOverlay(
        target: ComponentType,
        title: String,
        text: String,
        cancel: String,
        confirm: String,
        eventBus: EventBus = context.eventBus,
    ): Boolean {
        player.ifConfirmOverlay(target, title, text, cancel, confirm, eventBus)
        val modal = player.ui.getModalOrNull(components.main_modal)
        val input = coroutine.pause(ResumePCountDialogInput::class)
        val confirmed = resumeWithMainModalProtectedAccess(input.count != 0, modal)
        player.ifConfirmOverlayClose(eventBus)
        return confirmed
    }

    /**
     * @param hotkeys if `true` the menu interface will allow number keys to be used to select a
     *   listed choice.
     * @return the selected choice subcomponent id ranging from `0` to `127`.
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @throws IllegalArgumentException if [choices] has more than `127` elements.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun menu(
        title: String,
        hotkeys: Boolean,
        choices: List<String>,
        eventBus: EventBus = context.eventBus,
    ): Int {
        require(choices.size < 128) { "Can only have up to 127 `choices`. (size=${choices.size})" }
        player.ifMenu(title, choices.joinToString("|"), hotkeys, eventBus)
        val modal = player.ui.getModalOrNull(components.main_modal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        chatDefaultRestoreInput(player)
        return resumeWithMainModalProtectedAccess(input.subcomponent.absoluteValue, modal)
    }

    /**
     * @param hotkeys if `true` the menu interface will allow number keys to be used to select a
     *   listed choice.
     * @return the selected choice subcomponent id ranging from `0` to `127`.
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @throws IllegalArgumentException if [choices] has more than `127` elements.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun menu(
        title: String,
        vararg choices: String,
        hotkeys: Boolean = false,
        eventBus: EventBus = context.eventBus,
    ): Int = menu(title, hotkeys, choices.toList(), eventBus)

    /**
     * Ensures we can still obtain protected access for [player]. If protected access cannot be
     * regained, this function throws a [ProtectedAccessLostException], which will cause the current
     * `withProtectedAccess` lambda block to exit gracefully.
     *
     * The thrown exception will be suppressed by [Player.advanceActiveCoroutine] and/or
     * [Player.resumeActiveCoroutine], so it will not interrupt the server or the player. While
     * losing protected access is handled gracefully, it is something callers should be aware of
     * when using a suspend function in this [ProtectedAccess] scope.
     *
     * For example:
     * ```
     * player.withProtectedAccess {
     *  val input = countDialog()
     *  // While this lambda is suspended waiting for countDialog input, something has added a
     *  // delay to `player.`
     *
     *  // Input was somehow passed along and returns the number 5 to the suspending `countDialog`
     *  // call. However, the player is "delayed" and thus `Player.isAccessProtected` will return
     *  // true, causing `regainProtectedAccess` to throw `ProtectedAccessLostException`, and this
     *  // lambda to exit gracefully.
     *
     *  // This message will never be sent to the player
     *  player.mes("Your input is: $input.")
     *  // Nor will anything below this...
     *  player.invAdd(objs.jug_empty, input)
     * }
     * ```
     *
     * @throws ProtectedAccessLostException
     * @see [Player.isAccessProtected]
     */
    @Throws(ProtectedAccessLostException::class)
    private fun regainProtectedAccess() {
        if (player.isAccessProtected) {
            logger.debug { "Protected-access could not be re-obtained for player: $player" }
            throw ProtectedAccessLostException()
        }
    }

    /**
     * Helper function to attempt and resume a call-site from a `ResumePauseButtonInput` suspension
     * while ensuring that the [ResumePauseButtonInput.component] is associated with the
     * [expectedComponent] and that the same [expectedModal] is still opened after the suspension.
     *
     * @param expectedComponent the [ComponentType] that had its [IfEvent.PauseButton] bitmask
     *   enabled and what is expected of the player to click in order to "continue."
     * @param expectedModal the [Component] that is expected to be the player's active modal.
     * @throws ProtectedAccessLostException if the player is `delayed`, their active coroutine does
     *   not match this scope's [coroutine], the current modal does not match [expectedModal], or if
     *   [expectedComponent] `isType` returns `false` for [ResumePauseButtonInput.component].
     * @see [resumeWithModalProtectedAccess]
     */
    private fun resumePauseButtonWithProtectedAccess(
        input: ResumePauseButtonInput,
        expectedModal: Component?,
        expectedComponent: ComponentType,
    ) {
        if (!expectedComponent.isType(input.component)) {
            logger.debug {
                "Protected-access was lost due to unexpected component: " +
                    "player=$player, " +
                    "received=${input.component.internalNameGet ?: input}, " +
                    "expected=${expectedComponent.internalNameGet ?: expectedComponent}"
            }
            throw ProtectedAccessLostException()
        }
        resumeWithModalProtectedAccess(null, expectedModal, components.chat_dialogue_target)
    }

    /**
     * Helper function to attempt and resume a call-site from a suspension point while ensuring that
     * the [expectedModal] remained open.
     *
     * @param returnWithProtectedAccess the value to be returned after verifying protected access
     *   conditions are met.
     * @param expectedModal the [Component] that is expected to be the player's active modal.
     * @throws ProtectedAccessLostException if the player is `delayed`, their active coroutine does
     *   not match this scope's [coroutine], or if the current modal does not match [expectedModal].
     */
    private fun <T> resumeWithModalProtectedAccess(
        returnWithProtectedAccess: T,
        expectedModal: Component?,
        modalTarget: ComponentType,
    ): T {
        if (player.isDelayed) {
            logger.debug { "Protected-access was lost due to delay: player=$player" }
            throw ProtectedAccessLostException()
        }

        if (player.activeCoroutine !== coroutine) {
            logger.debug {
                "Protected-access was lost due to coroutine mismatch: " +
                    "player=$player, scopeCoroutine=$coroutine"
            }
            throw ProtectedAccessLostException()
        }

        val currentModal = player.ui.getModalOrNull(modalTarget)
        if (currentModal != expectedModal) {
            logger.debug {
                "Protected-access was lost due to unexpected modal: " +
                    "player=$player, " +
                    "received=$currentModal, " +
                    "expected=$expectedModal"
            }
            throw ProtectedAccessLostException()
        }

        return returnWithProtectedAccess
    }

    /**
     * Helper function that calls [resumeWithModalProtectedAccess] with the `modalTarget` set to
     * `components.main_modal`.
     *
     * @see [resumeWithModalProtectedAccess]
     */
    private fun <T> resumeWithMainModalProtectedAccess(
        returnWithProtectedAccess: T,
        expectedModal: Component?,
    ): T {
        return resumeWithModalProtectedAccess(
            returnWithProtectedAccess,
            expectedModal,
            components.main_modal,
        )
    }

    /* Loc helper functions (lc=loc config) */
    public fun <T : Any> lcParam(
        type: LocType,
        param: ParamType<T>,
        locTypes: LocTypeList = context.locTypes,
    ): T = locTypes[type].param(param)

    public fun <T : Any> lcParamOrNull(
        type: LocType,
        param: ParamType<T>,
        locTypes: LocTypeList = context.locTypes,
    ): T? = locTypes[type].paramOrNull(param)

    public fun <T : Any> locParam(
        loc: LocInfo,
        param: ParamType<T>,
        locTypes: LocTypeList = context.locTypes,
    ): T = locTypes[loc].param(param)

    public fun <T : Any> locParamOrNull(
        loc: LocInfo,
        param: ParamType<T>,
        locTypes: LocTypeList = context.locTypes,
    ): T? = locTypes[loc].paramOrNull(param)

    public fun <T : Any> locParam(
        loc: BoundLocInfo,
        param: ParamType<T>,
        locTypes: LocTypeList = context.locTypes,
    ): T = locTypes[loc].param(param)

    public fun <T : Any> locParamOrNull(
        loc: BoundLocInfo,
        param: ParamType<T>,
        locTypes: LocTypeList = context.locTypes,
    ): T? = locTypes[loc].paramOrNull(param)

    /* Npc helper functions (nc=npc config) */
    public fun <T : Any> ncParam(
        type: NpcType,
        param: ParamType<T>,
        npcTypes: NpcTypeList = context.npcTypes,
    ): T = npcTypes[type].param(param)

    public fun <T : Any> ncParamOrNull(
        type: NpcType,
        param: ParamType<T>,
        npcTypes: NpcTypeList = context.npcTypes,
    ): T? = npcTypes[type].paramOrNull(param)

    public fun npcPlayerFaceClose(npc: Npc, target: Player = this.player) {
        npc.playerFaceClose(target)
    }

    public fun npcPlayerFace(npc: Npc, target: Player = this.player) {
        npc.playerFace(target)
    }

    public fun npcPlayerEscape(npc: Npc, target: Player = this.player) {
        npc.playerEscape(target)
    }

    public fun npcResetMode(npc: Npc) {
        npc.resetMode()
    }

    public fun npcTransmog(npc: Npc, into: NpcType, npcTypes: NpcTypeList = context.npcTypes) {
        npc.transmog(npcTypes[into])
    }

    public fun npcVisType(
        npc: Npc,
        interactions: NpcInteractions = context.npcInteractions,
    ): UnpackedNpcType {
        val currentType = npc.visType
        val multiNpc = interactions.multiNpc(currentType, player.vars)
        return multiNpc ?: currentType
    }

    /**
     * Retrieves the [param] value for the base [npc].
     *
     * _Note: This retrieves the parameter from the npc's **base** type, ignoring any multinpc or
     * transmogrification effects._
     *
     * @throws IllegalStateException if npc type does not have an associated value for [param] and
     *   [param] does not have a [ParamType.default] value.
     */
    public fun <T : Any> npcParam(npc: Npc, param: ParamType<T>): T = npc.type.param(param)

    /**
     * Retrieves the [param] value for the base [npc], or returns `null` if the npc's type lacks an
     * associated value for [param] and [param] does not have a [ParamType.default] value.
     *
     * _Note: This retrieves the parameter from the npc's **base** type, ignoring any multinpc or
     * transmogrification effects._
     */
    public fun <T : Any> npcParamOrNull(npc: Npc, param: ParamType<T>): T? =
        npc.type.paramOrNull(param)

    /* Obj helper functions (oc=obj config) */
    public fun ocCert(type: ObjType, objTypes: ObjTypeList = context.objTypes): UnpackedObjType =
        objTypes.cert(objTypes[type])

    public fun ocUncert(type: ObjType, objTypes: ObjTypeList = context.objTypes): UnpackedObjType =
        objTypes.uncert(objTypes[type])

    public fun <T : Any> ocParam(
        obj: InvObj?,
        type: ParamType<T>,
        objTypes: ObjTypeList = context.objTypes,
    ): T? = if (obj == null) null else objTypes[obj].paramOrNull(type)

    public fun ocIsContentType(
        obj: InvObj?,
        content: ContentGroupType,
        objTypes: ObjTypeList = context.objTypes,
    ): Boolean = obj != null && objTypes[obj].contentGroup == content.id

    public fun ocIsType(obj: InvObj?, type: ObjType): Boolean = obj.isType(type)

    public fun ocIsType(obj: InvObj?, type: ObjType, vararg others: ObjType): Boolean =
        obj.isType(type) || others.any(obj::isType)

    public fun ocTradable(obj: InvObj, objTypes: ObjTypeList = context.objTypes): Boolean =
        objTypes[obj].tradeable

    /* Seq helper functions */
    /** Returns the total time duration of [seq] in _**client frames**_. */
    public fun seqLength(seq: SeqType, seqTypes: SeqTypeList = context.seqTypes): Int =
        seqTypes[seq].totalDelay

    /** Returns the total time duration of [seq] in _**server ticks**_. */
    public fun seqTicks(seq: SeqType, seqTypes: SeqTypeList = context.seqTypes): Int =
        seqTypes[seq].tickDuration

    /* Inventory helper functions */
    public fun invTakeFee(fee: Int, inv: Inventory = this.inv): Boolean =
        player.invTakeFee(fee, inv)

    public fun invCoinTotal(inv: Inventory = this.inv): Int = invTotal(inv, objs.coins)

    public fun invTotal(
        inv: Inventory,
        content: ContentGroupType,
        objTypes: ObjTypeList = context.objTypes,
    ): Int {
        var count = 0
        for (obj in inv) {
            val filtered = obj ?: continue
            val type = objTypes[filtered]
            if (type.isContentType(content)) {
                count += filtered.count
            }
        }
        return count
    }

    public fun invTotal(
        inv: Inventory,
        obj: ObjType,
        objTypes: ObjTypeList = context.objTypes,
    ): Int = inv.count(objTypes[obj])

    public fun invContains(
        inv: Inventory,
        content: ContentGroupType,
        objTypes: ObjTypeList = context.objTypes,
    ): Boolean = inv.any { it != null && objTypes[it].contentGroup == content.id }

    public operator fun Inventory.contains(content: ContentGroupType): Boolean =
        invContains(this, content)

    public fun inv(inv: InvType, invTypes: InvTypeList = context.invTypes): Inventory {
        val type = invTypes[inv]
        return player.invMap.getOrPut(type)
    }

    /* Client script helper functions */
    public fun runClientScript(id: Int, vararg args: Any): Unit = player.runClientScript(id, *args)

    public fun camForceAngle(rate: Int, rate2: Int): Unit =
        ClientScripts.camForceAngle(player, rate, rate2)

    public fun interfaceInvInit(
        inv: Inventory,
        target: ComponentType,
        objRowCount: Int,
        objColCount: Int,
        dragType: Int = 0,
        dragComponent: ComponentType? = null,
        op1: String? = null,
        op2: String? = null,
        op3: String? = null,
        op4: String? = null,
        op5: String? = null,
    ): Unit =
        ClientScripts.interfaceInvInit(
            player = player,
            inv = inv,
            target = target,
            objRowCount = objRowCount,
            objColCount = objColCount,
            dragType = dragType,
            dragComponent = dragComponent,
            op1 = op1,
            op2 = op2,
            op3 = op3,
            op4 = op4,
            op5 = op5,
        )

    public fun toplevelSidebuttonSwitch(side: Int): Unit =
        ClientScripts.toplevelSidebuttonSwitch(player, side)

    /* Cam helper functions */
    public fun camLookAt(dest: CoordGrid, height: Int, rate: Int, rate2: Int): Unit =
        Camera.camLookAt(player, dest, height, rate, rate2)

    public fun camMoveTo(dest: CoordGrid, height: Int, rate: Int, rate2: Int): Unit =
        Camera.camMoveTo(player, dest, height, rate, rate2)

    public fun camReset(): Unit = Camera.camReset(player)

    /* Cinematic helper functions */
    public fun camModeClose(): Unit = Cinematic.setCameraMode(player, CameraMode.Close)

    public fun camModeFar(): Unit = Cinematic.setCameraMode(player, CameraMode.Far)

    public fun camModeFixed(): Unit = Cinematic.setCameraMode(player, CameraMode.Fixed)

    public fun camModeReset(): Unit = Cinematic.setCameraMode(player, CameraMode.Normal)

    public fun compassHideOps(): Unit = Cinematic.setCompassState(player, CompassState.HideOps)

    public fun compassUnknown2(): Unit = Cinematic.setCompassState(player, CompassState.Unknown2)

    public fun compassReset(): Unit = Cinematic.setCompassState(player, CompassState.Normal)

    public fun minimapHideFull(): Unit = Cinematic.setMinimapState(player, MinimapState.Disabled)

    public fun minimapNoOps(): Unit = Cinematic.setMinimapState(player, MinimapState.MinimapNoOp)

    public fun minimapHideMap(): Unit =
        Cinematic.setMinimapState(player, MinimapState.MinimapHidden)

    public fun minimapHideCompass(): Unit =
        Cinematic.setMinimapState(player, MinimapState.CompassHidden)

    public fun minimapNoOpsHideCompass(): Unit =
        Cinematic.setMinimapState(player, MinimapState.MinimapNoOpCompassHidden)

    public fun minimapReset(): Unit = Cinematic.setMinimapState(player, MinimapState.Normal)

    public fun hideTopLevel(): Unit = Cinematic.setHideToplevel(player, hide = true)

    public fun showTopLevel(): Unit = Cinematic.setHideToplevel(player, hide = false)

    public fun clearHealthHud(): Unit = Cinematic.clearHealthHud(player)

    public fun hideHealthHud(): Unit = Cinematic.setHideHealthHud(player, hide = true)

    public fun showHealthHud(): Unit = Cinematic.setHideHealthHud(player, hide = false)

    public fun tempDisableAcceptAid(): Unit = Cinematic.disableAcceptAid(player)

    public fun restoreLastAcceptAid(): Unit = Cinematic.restoreAcceptAid(player)

    public fun hideEntityOps(): Unit = Cinematic.setHideEntityOps(player, hide = true)

    public fun showEntityOps(): Unit = Cinematic.setHideEntityOps(player, hide = false)

    public fun closeTopLevelTabs(eventBus: EventBus = context.eventBus): Unit =
        Cinematic.closeToplevelTabs(player, eventBus)

    public fun closeTopLevelTabsLenient(eventBus: EventBus = context.eventBus): Unit =
        Cinematic.closeToplevelTabsLenient(player, eventBus)

    public fun openTopLevelTabs(eventBus: EventBus = context.eventBus): Unit =
        Cinematic.openTopLevelTabs(player, eventBus)

    public fun fadeOverlay(
        startColour: Int,
        startTransparency: Int,
        endColour: Int,
        endTransparency: Int,
        clientDuration: Int,
        eventBus: EventBus = context.eventBus,
    ): Unit =
        Cinematic.fadeOverlay(
            player,
            startColour,
            startTransparency,
            endColour,
            endTransparency,
            clientDuration,
            eventBus,
        )

    public fun closeFadeOverlay(cycles: Int = 3) {
        longQueueDiscard(queues.fade_overlay_close, cycles)
    }

    /* Interface helper functions */
    public fun ifClose(eventBus: EventBus = context.eventBus): Unit = player.ifClose(eventBus)

    public fun ifCloseSub(interf: InterfaceType, eventBus: EventBus = context.eventBus): Unit =
        player.ifCloseSub(interf, eventBus)

    /**
     * Difference with [ifOpenMainModal] is that this function will **not** send
     * `toplevel_mainmodal_open` (script 2524) before opening the interface.
     */
    public fun ifOpenMain(interf: InterfaceType, eventBus: EventBus = context.eventBus): Unit =
        player.ifOpenMain(interf, eventBus)

    public fun ifOpenMainSidePair(
        main: InterfaceType,
        side: InterfaceType,
        colour: Int = -1,
        transparency: Int = -1,
        eventBus: EventBus = context.eventBus,
    ): Unit = player.ifOpenMainSidePair(main, side, colour, transparency, eventBus)

    /**
     * Difference with [ifOpenMain] is that this function will send `toplevel_mainmodal_open`
     * (script 2524) before opening the interface in the main modal position.
     */
    public fun ifOpenMainModal(
        interf: InterfaceType,
        colour: Int = -1,
        transparency: Int = -1,
        eventBus: EventBus = context.eventBus,
    ): Unit = player.ifOpenMainModal(interf, eventBus, colour, transparency)

    public fun ifOpenOverlay(interf: InterfaceType, eventBus: EventBus = context.eventBus): Unit =
        player.ifOpenOverlay(interf, eventBus)

    public fun ifOpenOverlay(
        interf: InterfaceType,
        target: ComponentType,
        eventBus: EventBus = context.eventBus,
    ): Unit = player.ifOpenSub(interf, target, IfSubType.Overlay, eventBus)

    public fun ifOpenFullOverlay(
        interf: InterfaceType,
        eventBus: EventBus = context.eventBus,
    ): Unit = player.ifOpenFullOverlay(interf, eventBus)

    public fun ifOpenSub(
        interf: InterfaceType,
        target: ComponentType,
        type: IfSubType,
        eventBus: EventBus = context.eventBus,
    ): Unit = player.ifOpenSub(interf, target, type, eventBus)

    public fun ifSetAnim(target: ComponentType, seq: SeqType?): Unit = player.ifSetAnim(target, seq)

    public fun ifSetEvents(target: ComponentType, range: IntRange, vararg event: IfEvent): Unit =
        player.ifSetEvents(target, range, *event)

    public fun ifSetNpcHead(target: ComponentType, npc: NpcType): Unit =
        player.ifSetNpcHead(target, npc)

    public fun ifSetPlayerHead(target: ComponentType): Unit = player.ifSetPlayerHead(target)

    public fun ifSetText(target: ComponentType, text: String): Unit = player.ifSetText(target, text)

    public fun ifSetObj(target: ComponentType, obj: ObjType, zoom: Int): Unit =
        player.ifSetObj(target, obj, zoom)

    /* Map flag helper functions */
    public fun setMapFlag(coords: CoordGrid): Unit = MapFlag.setMapFlag(player, coords)

    /* Message game helper functions */
    public fun mes(text: String): Unit = player.mes(text, ChatType.GameMessage)

    public fun mes(text: String, type: ChatType): Unit = player.mes(text, type)

    public fun spam(text: String): Unit = player.spam(text)

    /* Sound helper functions */
    public fun soundSynth(synth: SynthType, loops: Int = 1, delay: Int = 0): Unit =
        player.soundSynth(synth, loops, delay)

    public fun jingle(jingle: JingleType): Unit = player.jingle(jingle)

    /**
     * Increments [opHeldCallCount] counter and throws [IllegalStateException] if the call count
     * exceeds a certain threshold. This is done as a counter-measure to avoid [StackOverflowError]
     * under a specific scenario of `opHeld` calls.
     *
     * This occurs when an `opHeldN` function is called from an `onOpHeldN` script that comes from
     * the same inv slot obj. Assuming there are no `delay` or similar suspending calls in between
     * each script, this leads to infinite recursion:
     * ```
     * onOpHeldN -> opHeldN -> onOpHeldN -> opHeldN -> ...
     * ```
     *
     * @throws IllegalStateException
     */
    private fun checkOpHeldCallLimit() {
        if (opHeldCallCount++ >= 25) {
            throw IllegalStateException("Detected `opHeld` infinite recursion: $this")
        }
    }

    override fun toString(): String = "ProtectedAccess(player=$player, coroutine=$coroutine)"
}

private fun <T> lazy(init: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, init)

private fun MesAnimType.splitGetAnim(lines: Int) =
    when (lines) {
        1 -> len1
        2 -> len2
        3 -> len3
        else -> len4
    }

/**
 * This function should only be called directly under specific circumstances. Prefer calling
 * [org.rsmod.api.player.protect.ProtectedAccess.clearPendingAction] instead.
 */
public fun Player.clearPendingAction(eventBus: EventBus) {
    ifClose(eventBus)
    cancelActiveCoroutine()
    clearInteraction()
    clearRouteRecalc()
}
