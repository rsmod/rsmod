package org.rsmod.api.script

import org.rsmod.api.player.events.PlayerMovementEvent
import org.rsmod.api.player.events.PlayerQueueEvents
import org.rsmod.api.player.events.PlayerTimerEvent
import org.rsmod.api.player.events.interact.PlayerTEvents
import org.rsmod.api.player.events.interact.PlayerUContentEvents
import org.rsmod.api.player.events.interact.PlayerUEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.player.SessionStateEvent
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onPlayerInit(action: SessionStateEvent.Initialize.() -> Unit): Unit =
    onEvent(action)

public fun ScriptContext.onPlayerLogin(action: SessionStateEvent.Login.() -> Unit): Unit =
    onEvent(action)

public fun ScriptContext.onPlayerLogout(action: SessionStateEvent.Logout.() -> Unit): Unit =
    onEvent(action)

/* Op functions */
public fun ScriptContext.onOpPlayerT(
    component: ComponentType,
    action: suspend ProtectedAccess.(PlayerTEvents.Op) -> Unit,
): Unit = onProtectedEvent(component.packed, action)

public fun ScriptContext.onOpPlayerU(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(PlayerUContentEvents.Op) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpPlayerU(
    type: ObjType,
    action: suspend ProtectedAccess.(PlayerUEvents.Op) -> Unit,
): Unit = onProtectedEvent(type.id, action)

/* Ap functions */
public fun ScriptContext.onApPlayerT(
    component: ComponentType,
    action: suspend ProtectedAccess.(PlayerTEvents.Ap) -> Unit,
): Unit = onProtectedEvent(component.packed, action)

public fun ScriptContext.onApPlayerU(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(PlayerUContentEvents.Ap) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApPlayerU(
    type: ObjType,
    action: suspend ProtectedAccess.(PlayerUEvents.Ap) -> Unit,
): Unit = onProtectedEvent(type.id, action)

/* Timer functions */
/**
 * Registers an event listener for a **normal player timer**.
 *
 * Normal timers are published as [PlayerTimerEvent.Normal] and executed inside [ProtectedAccess].
 *
 * **Notes:**
 * - Normal timers are processed only while `player.isAccessProtected == false`.
 * - If the handler clears the timer (via [ProtectedAccess.clearTimer]), it is not rescheduled.
 *
 * @param timer The timer type to listen for.
 * @param action The suspended action to execute when the timer fires.
 *
 * @see onPlayerSoftTimer For timers published while `player.isAccessProtected == true`.
 * @see ProtectedAccess.timer To schedule a normal timer.
 * @see ProtectedAccess.clearTimer To stop a timer.
 *
 * ```kotlin
 * onPlayerTimer(timers.poison) {
 *     val damage = calculatePoisonDamage()
 *     if (damage <= 0) {
 *         clearTimer(timers.poison)
 *         return@onPlayerTimer
 *     }
 *     player.applyDamage(damage)
 * }
 * ```
 */
public fun ScriptContext.onPlayerTimer(
    timer: TimerType,
    action: suspend ProtectedAccess.(PlayerTimerEvent.Normal) -> Unit,
): Unit = onProtectedEvent(timer.id, action)

/**
 * Registers an event listener for a **soft player timer**.
 *
 * Soft timers are published as [PlayerTimerEvent.Soft]. The handler does not run in
 * [ProtectedAccess]; it runs as a plain event.
 *
 * **Notes:**
 * - Soft timers are processed even while `player.isAccessProtected == true`.
 * - If the handler clears the timer (via [org.rsmod.game.entity.Player.clearSoftTimer]), it is not
 *   rescheduled.
 *
 * @param timer The timer type to listen for.
 * @param action The action to execute when the timer fires.
 *
 * @see onPlayerTimer For timers executed inside [ProtectedAccess].
 * @see ProtectedAccess.softTimer To schedule a soft timer.
 * @see ProtectedAccess.clearSoftTimer To stop a soft timer.
 *
 * ```kotlin
 * onPlayerSoftTimer(timers.freeze_immunity) {
 *     player.message("You are no longer frozen.")
 * }
 * ```
 */
public fun ScriptContext.onPlayerSoftTimer(
    timer: TimerType,
    action: PlayerTimerEvent.Soft.() -> Unit,
): Unit = onEvent(timer.id, action)

/* Queue functions */
/**
 * Registers an event listener for a **player queue** (normal or strong).
 *
 * Both [ProtectedAccess.queue] and [ProtectedAccess.strongQueue] publish
 * [PlayerQueueEvents.Protected] and execute inside [ProtectedAccess].
 *
 * **Notes:**
 * - Normal/strong queues are published only when `player.isAccessProtected == false`.
 * - If at least one strong queue exists at the start of queue processing,
 *   `player.ifClose(eventBus)` is invoked before any queues are published.
 *
 * @param queue The queue type to listen for.
 * @param action The suspended action to execute.
 *
 * @see onPlayerQueueWithArgs For queues that pass custom arguments.
 * @see onPlayerSoftQueue For soft queues published while `player.isAccessProtected == true`.
 * @see ProtectedAccess.queue To schedule a normal queue.
 * @see ProtectedAccess.strongQueue To schedule a strong queue.
 * @see ProtectedAccess.weakQueue To schedule a weak queue.
 *
 * ```kotlin
 * onPlayerQueue(queues.apply_damage) {
 *     val damage = queue.args as Int
 *     player.applyDamage(damage)
 *     player.message("You take $damage damage!")
 * }
 *
 * strongQueue(queues.apply_damage, cycles = 2, args = 15)
 * ```
 */
public fun ScriptContext.onPlayerQueue(
    queue: QueueType,
    action: suspend ProtectedAccess.(PlayerQueueEvents.Protected<Nothing>) -> Unit,
): Unit = onProtectedEvent(queue.id, action)

/**
 * Registers an event listener for a **player queue with typed arguments**.
 *
 * Same as [onPlayerQueue] but provides type-safe access to custom arguments passed when
 * scheduling the queue.
 *
 * @param T The type of arguments passed to the queue.
 * @param queue The queue type to listen for.
 * @param action The suspended action to execute.
 *
 * @see onPlayerQueue For queue behavior notes.
 *
 * ```kotlin
 * data class DamageArgs(val amount: Int, val source: String)
 *
 * onPlayerQueueWithArgs<DamageArgs>(queues.apply_damage) { event ->
 *     val args = event.args
 *     player.applyDamage(args.amount)
 *     player.message("Hit by ${args.source} for ${args.amount}!")
 * }
 *
 * // Schedule with arguments:
 * strongQueue(queues.apply_damage, cycles = 2, args = DamageArgs(15, "fireball"))
 * ```
 */
public fun <T> ScriptContext.onPlayerQueueWithArgs(
    queue: QueueType,
    action: suspend ProtectedAccess.(PlayerQueueEvents.Protected<T>) -> Unit,
): Unit = onProtectedEvent(queue.id, action)

/**
 * Registers an event listener for a **soft player queue**.
 *
 * Soft queues are published as [PlayerQueueEvents.Soft]. The handler does not run in
 * [ProtectedAccess]; it runs as a plain event.
 *
 * **Notes:**
 * - Soft queues are published even while `player.isAccessProtected == true`.
 * - When processed, `player.ifClose(eventBus)` is invoked.
 *
 * @param queue The queue type to listen for.
 * @param action The action to execute.
 *
 * @see onPlayerSoftQueueWithArgs For soft queues with typed arguments.
 * @see onPlayerQueue For queues executed inside [ProtectedAccess].
 * @see ProtectedAccess.softQueue To schedule a soft queue.
 *
 * ```kotlin
 * onPlayerSoftQueue(queues.change_window_mode) {
 *     player.message("Window mode changed.")
 * }
 *
 * softQueue(queues.change_window_mode, cycles = 1)
 * ```
 */
public fun ScriptContext.onPlayerSoftQueue(
    queue: QueueType,
    action: PlayerQueueEvents.Soft<Nothing>.() -> Unit,
): Unit = onEvent(queue.id, action)

/**
 * Registers an event listener for a **soft player queue with typed arguments**.
 *
 * Same as [onPlayerSoftQueue] but provides type-safe access to custom arguments.
 *
 * @param T The type of arguments passed to the queue.
 * @param queue The queue type to listen for.
 * @param action The action to execute.
 *
 * @see onPlayerSoftQueue For soft queue behavior notes.
 *
 * ```kotlin
 * data class WindowMode(val mode: Int)
 *
 * onPlayerSoftQueueWithArgs<WindowMode>(queues.change_window) {
 *     val mode = args.mode
 *     player.message("Window mode: $mode")
 * }
 * ```
 */
public fun <T> ScriptContext.onPlayerSoftQueueWithArgs(
    queue: QueueType,
    action: PlayerQueueEvents.Soft<T>.() -> Unit,
): Unit = onEvent(queue.id, action)

/* Walk trigger functions */
public fun ScriptContext.onPlayerWalkTrigger(
    trigger: WalkTriggerType,
    action: PlayerMovementEvent.WalkTrigger.() -> Unit,
): Unit = onEvent(trigger.id, action)
