package org.rsmod.api.player.protect

import com.github.michaelbull.logging.InlineLogger
import kotlin.math.max
import net.rsprot.protocol.game.outgoing.misc.player.TriggerOnDialogAbort
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.components
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.player.input.CountDialogInput
import org.rsmod.api.player.input.ResumePauseButtonInput
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.output.Camera
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.ClientScripts
import org.rsmod.api.player.output.ClientScripts.mesLayerMode7
import org.rsmod.api.player.output.MapFlag
import org.rsmod.api.player.output.UpdateInventory
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.output.spam
import org.rsmod.api.player.output.updateInvFull
import org.rsmod.api.player.protect.ProtectedAccessLauncher.Companion.withProtectedAccess
import org.rsmod.api.player.stat.PlayerSkillXP
import org.rsmod.api.player.ui.ifChatNpcSpecific
import org.rsmod.api.player.ui.ifChatPlayer
import org.rsmod.api.player.ui.ifChoice
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifCloseSub
import org.rsmod.api.player.ui.ifDoubleobjbox
import org.rsmod.api.player.ui.ifMesbox
import org.rsmod.api.player.ui.ifObjbox
import org.rsmod.api.player.ui.ifOpenMain
import org.rsmod.api.player.ui.ifOpenMainModal
import org.rsmod.api.player.ui.ifOpenSub
import org.rsmod.api.player.ui.ifSetAnim
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.ui.ifSetNpcHead
import org.rsmod.api.player.ui.ifSetPlayerHead
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.api.random.GameRandom
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.player.ProtectedAccessLostException
import org.rsmod.game.entity.shared.PathingEntityCommon
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.inv.Inventory
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.map.Direction
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.mesanim.MesAnimType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.map.CoordGrid
import org.rsmod.objtx.TransactionResultList
import org.rsmod.pathfinder.collision.CollisionFlagMap

private val logger = InlineLogger()

public class ProtectedAccess(
    public val player: Player,
    private val coroutine: GameCoroutine,
    private val context: ProtectedAccessContext,
) {
    public val random: GameRandom by context::random

    public val coords: CoordGrid
        get() = player.coords

    public val mapClock: Int
        get() = player.currentMapClock

    public val inv: Inventory
        get() = player.inv

    public var actionDelay: Int by player::actionDelay
    public var skillAnimDelay: Int by player::skillAnimDelay
    public var skillSoundDelay: Int by player::skillSoundDelay

    public suspend fun walk(dest: CoordGrid): Unit = move(dest, MoveSpeed.Walk)

    public suspend fun run(dest: CoordGrid): Unit = move(dest, MoveSpeed.Run)

    public suspend fun move(dest: CoordGrid, moveSpeed: MoveSpeed = player.varMoveSpeed) {
        val delay = (player.coords.chebyshevDistance(dest) - 1) / max(1, moveSpeed.steps)
        player.moveSpeed = moveSpeed
        player.routeDestination.clear()
        player.routeDestination.add(dest)
        if (delay > 0) {
            delay(delay)
        }
    }

    public fun telejump(dest: CoordGrid, collision: CollisionFlagMap = context.collision) {
        PathingEntityCommon.telejump(player, collision, dest)
    }

    public fun teleport(dest: CoordGrid, collision: CollisionFlagMap = context.collision) {
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

    public fun faceSquare(target: CoordGrid): Unit = player.faceSquare(target)

    public fun faceDirection(direction: Direction): Unit = player.faceDirection(direction)

    public fun faceLoc(loc: BoundLocInfo): Unit = player.faceLoc(loc)

    public fun faceEntitySquare(target: PathingEntity): Unit =
        player.facePathingEntitySquare(target)

    public fun resetFaceSquare(): Unit = player.resetPendingFaceSquare()

    public fun facePlayer(target: Player): Unit = player.facePlayer(target)

    public fun faceNpc(target: Npc): Unit = player.faceNpc(target)

    public fun resetFaceEntity(): Unit = player.resetFaceEntity()

    public fun invAdd(
        inv: Inventory,
        obj: InvObj,
        slot: Int? = null,
        strict: Boolean = true,
        cert: Boolean = false,
        uncert: Boolean = false,
        updateInv: Boolean = true,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> =
        player.invAdd(
            inv = inv,
            obj = obj,
            slot = slot,
            strict = strict,
            cert = cert,
            uncert = uncert,
            updateInv = updateInv,
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
        updateInv: Boolean = true,
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
            updateInv = updateInv,
            autoCommit = autoCommit,
        )

    public fun statAdvance(
        stat: StatType,
        xp: Double,
        rate: Double = player.xpRate,
        eventBus: EventBus = context.eventBus,
    ): Int = PlayerSkillXP.internalAddXP(player, stat, xp, rate, eventBus)

    public fun timer(timerType: TimerType, cycles: Int) {
        player.timer(timerType, cycles)
    }

    public fun softTimer(timerType: TimerType, cycles: Int) {
        player.softTimer(timerType, cycles)
    }

    public fun clearPendingAction(eventBus: EventBus = context.eventBus) {
        player.clearPendingAction(eventBus)
    }

    public fun logOut() {
        // TODO: impl
    }

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
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun mesbox(
        text: String,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.ifMesbox(text, pauseText, eventBus)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.text_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun objbox(
        obj: ObjType,
        zoom: Int,
        text: String,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.ifObjbox(text, obj.id, zoom, pauseText, eventBus)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.obj_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun objbox(
        obj: InvObj,
        zoomOrCount: Int,
        text: String,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.ifObjbox(text, obj.id, zoomOrCount, pauseText, eventBus)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.obj_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
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
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.double_obj_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
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
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.double_obj_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
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
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.options_dialogue_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
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
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.options_dialogue_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            3 -> result3
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
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
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.options_dialogue_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            3 -> result3
            4 -> result4
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
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
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.options_dialogue_pbutton)
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
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
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
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.player_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun chatNpc(
        npc: Npc,
        text: String,
        mesanim: MesAnimType?,
        lineCount: Int,
        lineHeight: Int,
        faceFar: Boolean = false,
        title: String = npc.name,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        val chatanim = mesanim?.splitGetAnim(lineCount)
        npc.playerFace(player, faceFar = faceFar)
        player.facePathingEntitySquare(npc)
        player.ifChatNpcSpecific(title, npc.type, text, chatanim, pauseText, lineHeight, eventBus)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.npc_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun chatNpcNoTurn(
        npc: Npc,
        text: String,
        mesanim: MesAnimType?,
        lineCount: Int,
        lineHeight: Int,
        title: String = npc.name,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        val chatanim = mesanim?.splitGetAnim(lineCount)
        player.facePathingEntitySquare(npc)
        player.ifChatNpcSpecific(title, npc.type, text, chatanim, pauseText, lineHeight, eventBus)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.npc_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
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
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, components.npc_dialogue_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun countDialog(title: String = constants.cm_count): Int {
        mesLayerMode7(player, title)
        val input = coroutine.pause(CountDialogInput::class)
        return withProtectedAccess(input.count)
    }

    /**
     * Ensures we can still obtain protected access for [player]. If protected access cannot be
     * regained, this function throws a [ProtectedAccessLostException], which will cause the current
     * [withProtectedAccess] lambda block to exit gracefully.
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
     * Syntax sugar alias for [regainProtectedAccess] that allows the input argument
     * ([returnWithProtectedAccess]) to be returned as long as [regainProtectedAccess] does not
     * throw [ProtectedAccessLostException].
     *
     * @throws ProtectedAccessLostException
     * @see [regainProtectedAccess]
     */
    private fun <T> withProtectedAccess(returnWithProtectedAccess: T): T {
        regainProtectedAccess()
        return returnWithProtectedAccess
    }

    /**
     * Helper function to attempt and resume a call-site from a `ResumePauseButtonInput` suspension
     * while ensuring that the [ResumePauseButtonInput.component] is associated with the
     * [expectedComponent].
     *
     * @param expectedComponent the [ComponentType] that had its [IfEvent.PauseButton] bitmask
     *   enabled and what is expected of the player to click in order to "continue."
     * @throws ProtectedAccessLostException if [regainProtectedAccess] throws the exception, or if
     *   [expectedComponent] `isAssociatedWith` returns `false` for
     *   [ResumePauseButtonInput.component].
     * @see [regainProtectedAccess]
     */
    private fun resumePauseButtonWithProtectedAccess(
        input: ResumePauseButtonInput,
        expectedComponent: ComponentType,
    ) {
        if (!expectedComponent.isAssociatedWith(input.component)) {
            logger.debug {
                "Protected-access was lost due to unexpected " +
                    "component `${input.component}` for player: $player"
            }
            throw ProtectedAccessLostException()
        }
        regainProtectedAccess()
    }

    /* Inventory helper functions */
    public fun invTotal(
        inv: Inventory,
        content: ContentGroupType,
        objTypes: ObjTypeList = context.objTypes,
    ): Int = inv.count { it != null && objTypes[it].contentGroup == content.id }

    public fun invTotal(inv: Inventory, obj: ObjType): Int =
        inv.count { it != null && it.id == obj.id }

    public fun invContains(
        inv: Inventory,
        content: ContentGroupType,
        objTypes: ObjTypeList = context.objTypes,
    ): Boolean = inv.any { it != null && objTypes[it].contentGroup == content.id }

    public operator fun Inventory.contains(content: ContentGroupType): Boolean =
        invContains(this, content)

    /* Client script helper functions */
    public fun runClientScript(id: Int, vararg args: Any): Unit = player.runClientScript(id, *args)

    public fun camForceAngle(rate: Int, rate2: Int): Unit =
        ClientScripts.camForceAngle(player, rate, rate2)

    public fun interfaceInvInit(
        inv: Inventory,
        target: ComponentType,
        objRowCount: Int,
        objColCount: Int,
        op1: String? = null,
        op2: String? = null,
        op3: String? = null,
        op4: String? = null,
        op5: String? = null,
        dragType: Int = 0,
        dragComponent: ComponentType? = null,
    ): Unit =
        ClientScripts.interfaceInvInit(
            player = player,
            inv = inv,
            target = target,
            objRowCount = objRowCount,
            objColCount = objColCount,
            op1 = op1,
            op2 = op2,
            op3 = op3,
            op4 = op4,
            op5 = op5,
            dragType = dragType,
            dragComponent = dragComponent,
        )

    public fun toplevelSidebuttonSwitch(side: Int): Unit =
        ClientScripts.toplevelSidebuttonSwitch(player, side)

    /* Cam helper functions */
    public fun camLookAt(dest: CoordGrid, height: Int, rate: Int, rate2: Int): Unit =
        Camera.camLookAt(player, dest, height, rate, rate2)

    public fun camMoveTo(dest: CoordGrid, height: Int, rate: Int, rate2: Int): Unit =
        Camera.camMoveTo(player, dest, height, rate, rate2)

    public fun camReset(): Unit = Camera.camReset(player)

    /* Interface helper functions */
    public fun ifClose(eventBus: EventBus = context.eventBus): Unit = player.ifClose(eventBus)

    public fun ifCloseSub(interf: InterfaceType, eventBus: EventBus = context.eventBus): Unit =
        player.ifCloseSub(interf, eventBus)

    public fun ifOpenMain(interf: InterfaceType, eventBus: EventBus = context.eventBus): Unit =
        player.ifOpenMain(interf, eventBus)

    public fun ifOpenMainModal(
        interf: InterfaceType,
        colour: Int = -1,
        transparency: Int = -1,
        eventBus: EventBus = context.eventBus,
    ): Unit = player.ifOpenMainModal(interf, eventBus, colour, transparency)

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

    /* Inventory helper functions */
    public fun updateInvFull(inv: Inventory): Unit = player.updateInvFull(inv)

    public fun updateInvStopTransmit(inv: Inventory): Unit =
        UpdateInventory.updateInvStopTransmit(player, inv)

    /* Map flag helper functions */
    public fun clearMapFlag(): Unit = player.clearMapFlag()

    public fun setMapFlag(coords: CoordGrid): Unit = MapFlag.setMapFlag(player, coords)

    /* Message game helper functions */
    public fun mes(text: String, type: ChatType = ChatType.GameMessage): Unit =
        player.mes(text, type)

    public fun spam(text: String): Unit = player.spam(text)

    /* Sound helper functions */
    public fun soundSynth(synth: SynthType, loops: Int = 1, delay: Int = 0): Unit =
        player.soundSynth(synth, loops, delay)
}

/** @see [ProtectedAccess.telejump] */
public fun Player.protectedTelejump(collision: CollisionFlagMap, dest: CoordGrid): Boolean =
    withProtectedAccess(this, ProtectedAccessContext.EMPTY_CTX) { telejump(dest, collision) }

/** @see [ProtectedAccess.teleport] */
public fun Player.protectedTeleport(collision: CollisionFlagMap, dest: CoordGrid): Boolean =
    withProtectedAccess(this, ProtectedAccessContext.EMPTY_CTX) { teleport(dest, collision) }

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
    triggerOnDialogAbort()
    cancelActiveCoroutine()
    clearInteraction()
    ifClose(eventBus)
}

private fun Player.triggerOnDialogAbort() {
    // If this is called, we can safely assume the only active coroutine would be from a chatbox
    // related suspension. `delay` suspensions would not allow this function to be reached as the
    // player would be under a delay and the respective packets would be discarded.
    if (activeCoroutine?.isSuspended == true) {
        client.write(TriggerOnDialogAbort)
    }
}
