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

/* Walk trigger functions */
public fun ScriptContext.onPlayerWalkTrigger(
    trigger: WalkTriggerType,
    action: PlayerMovementEvent.WalkTrigger.() -> Unit,
): Unit = onEvent(trigger.id, action)
