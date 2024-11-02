package org.rsmod.api.script

import org.rsmod.api.player.events.PlayerQueueEvent
import org.rsmod.api.player.events.PlayerTimerEvent
import org.rsmod.api.player.events.SessionStateEvent
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onPlayerInit(action: SessionStateEvent.Initialize.() -> Unit): Unit =
    onEvent(action)

public fun ScriptContext.onPlayerLogIn(action: SessionStateEvent.LogIn.() -> Unit): Unit =
    onEvent(action)

public fun ScriptContext.onPlayerTimer(
    timer: TimerType,
    action: suspend ProtectedAccess.(PlayerTimerEvent.Normal) -> Unit,
): Unit = onProtectedEvent(timer.id, action)

public fun ScriptContext.onPlayerSoftTimer(
    timer: TimerType,
    action: PlayerTimerEvent.Soft.() -> Unit,
): Unit = onEvent(timer.id, action)

public fun ScriptContext.onPlayerQueue(
    queue: QueueType,
    action: suspend ProtectedAccess.(PlayerQueueEvent.Protected) -> Unit,
): Unit = onProtectedEvent(queue.id, action)

public fun ScriptContext.onPlayerSoftQueue(
    queue: QueueType,
    action: PlayerQueueEvent.Soft.() -> Unit,
): Unit = onEvent(queue.id, action)
