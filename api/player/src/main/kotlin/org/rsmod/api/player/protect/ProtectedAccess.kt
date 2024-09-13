package org.rsmod.api.player.protect

import com.github.michaelbull.logging.InlineLogger
import kotlin.math.max
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.components
import org.rsmod.api.player.clearPendingAction
import org.rsmod.api.player.ifChatNpcSpecific
import org.rsmod.api.player.ifChatPlayer
import org.rsmod.api.player.ifChoice
import org.rsmod.api.player.ifClose
import org.rsmod.api.player.ifMesbox
import org.rsmod.api.player.ifOpenMain
import org.rsmod.api.player.ifOpenMainModal
import org.rsmod.api.player.ifSetText
import org.rsmod.api.player.mes
import org.rsmod.api.player.stat.PlayerSkillXP
import org.rsmod.api.player.ui.input.CountDialogInput
import org.rsmod.api.player.ui.input.ResumePauseButtonInput
import org.rsmod.api.player.util.ClientScripts.mesLayerMode7
import org.rsmod.api.player.varMoveSpeed
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.player.ProtectedAccessLostException
import org.rsmod.game.entity.shared.PathingEntityCommon
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.mesanim.MesAnimType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.stat.StatType
import org.rsmod.map.CoordGrid
import org.rsmod.pathfinder.collision.CollisionFlagMap

private val logger = InlineLogger()

public class ProtectedAccess(
    public val player: Player,
    public val coroutine: GameCoroutine,
    private val context: ProtectedAccessContext,
) {
    public fun clearPendingAction(eventBus: EventBus = context.eventBus) {
        player.clearPendingAction(eventBus)
    }

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

    // TODO: Resolve priority
    public fun anim(seq: SeqType, delay: Int = 0, priority: Int = 0) {
        player.anim(seq, delay, priority)
    }

    public fun ifClose(eventBus: EventBus = context.eventBus) {
        player.ifClose(eventBus)
    }

    public fun apRange(dist: Int) {
        val interaction = player.interaction ?: return
        interaction.apRange = dist
        interaction.apRangeCalled = true
    }

    public fun statAdvance(
        stat: StatType,
        xp: Double,
        rate: Double = player.xpRate,
        eventBus: EventBus = context.eventBus,
    ): Int = PlayerSkillXP.internalAddXP(player, stat, xp, rate, eventBus)

    public fun logOut() {
        // TODO: impl
    }

    public fun ifOpenMainModal(
        interf: InterfaceType,
        colour: Int = -1,
        transparency: Int = -1,
        eventBus: EventBus = context.eventBus,
    ): Unit = player.ifOpenMainModal(interf, eventBus, colour, transparency)

    public fun ifOpenMain(interf: InterfaceType, eventBus: EventBus = context.eventBus): Unit =
        player.ifOpenMain(interf, eventBus)

    public fun ifSetText(target: ComponentType, text: String): Unit = player.ifSetText(target, text)

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
        val faceMode = if (faceFar) NpcMode.PlayerFace else NpcMode.PlayerFaceClose
        npc.mode = faceMode
        npc.facePlayer(player)
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
        player.mesLayerMode7(title)
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
}

/** @see [ProtectedAccess] */
public fun Player.withProtectedAccess(
    context: ProtectedAccessContext,
    busyText: String? = constants.dm_busy,
    block: suspend ProtectedAccess.() -> Unit,
): Boolean {
    if (isAccessProtected) {
        busyText?.let { mes(it) }
        return false
    }
    val coroutine = GameCoroutine()
    launch(coroutine) {
        val protectedAccess = ProtectedAccess(this@withProtectedAccess, this, context)
        block(protectedAccess)
    }
    return true
}

/** @see [ProtectedAccess.telejump] */
public fun Player.protectedTelejump(collision: CollisionFlagMap, dest: CoordGrid): Boolean =
    withProtectedAccess(ProtectedAccessContext.EMPTY_CTX) { telejump(dest, collision) }

/** @see [ProtectedAccess.teleport] */
public fun Player.protectedTeleport(collision: CollisionFlagMap, dest: CoordGrid): Boolean =
    withProtectedAccess(ProtectedAccessContext.EMPTY_CTX) { teleport(dest, collision) }

private fun MesAnimType.splitGetAnim(lines: Int) =
    when (lines) {
        1 -> len1
        2 -> len2
        3 -> len3
        else -> len4
    }
