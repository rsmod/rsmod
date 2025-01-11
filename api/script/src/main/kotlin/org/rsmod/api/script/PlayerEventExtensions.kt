package org.rsmod.api.script

import org.rsmod.api.player.events.PlayerQueueEvents
import org.rsmod.api.player.events.PlayerTimerEvent
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.player.SessionStateEvent
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onPlayerInit(action: SessionStateEvent.Initialize.() -> Unit): Unit =
    onEvent(action)

public fun ScriptContext.onPlayerLogIn(action: SessionStateEvent.LogIn.() -> Unit): Unit =
    onEvent(action)

/* Timer functions */
public fun ScriptContext.onPlayerTimer(
    timer: TimerType,
    action: suspend ProtectedAccess.(PlayerTimerEvent.Normal) -> Unit,
): Unit = onProtectedEvent(timer.id, action)

public fun ScriptContext.onPlayerSoftTimer(
    timer: TimerType,
    action: PlayerTimerEvent.Soft.() -> Unit,
): Unit = onEvent(timer.id, action)

/* Queue functions */
public fun ScriptContext.onPlayerQueue(
    queue: QueueType,
    action: suspend ProtectedAccess.(PlayerQueueEvents.Protected<Nothing>) -> Unit,
): Unit = onProtectedEvent(queue.id, action)

public fun <T> ScriptContext.onPlayerQueueWithArgs(
    queue: QueueType,
    action: suspend ProtectedAccess.(PlayerQueueEvents.Protected<T>) -> Unit,
): Unit = onProtectedEvent(queue.id, action)

public fun ScriptContext.onPlayerSoftQueue(
    queue: QueueType,
    action: PlayerQueueEvents.Soft<Nothing>.() -> Unit,
): Unit = onEvent(queue.id, action)

public fun <T> ScriptContext.onPlayerSoftQueueWithArgs(
    queue: QueueType,
    action: PlayerQueueEvents.Soft<T>.() -> Unit,
): Unit = onEvent(queue.id, action)
