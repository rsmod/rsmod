package org.runerealm.api.script

import org.runerealm.api.controller.access.StandardConAccess
import org.runerealm.api.controller.events.ControllerAIEvents
import org.runerealm.api.controller.events.ControllerQueueEvents
import org.runerealm.api.controller.events.ControllerTimerEvents
import org.runerealm.events.EventBus
import org.runerealm.game.type.controller.ControllerType
import org.runerealm.game.type.queue.QueueType
import org.runerealm.game.type.timer.TimerType
import org.runerealm.plugin.scripts.ScriptContext

/* Timer functions */
public fun ScriptContext.onAiConTimer(
    type: ControllerType,
    action: ControllerAIEvents.Timer.() -> Unit,
): Unit = onEvent(type.id, action)

public fun ScriptContext.onConTimer(
    type: ControllerType,
    action: suspend StandardConAccess.(ControllerTimerEvents.Type) -> Unit,
): Unit = onConAccessEvent(type.id, action)

public fun ScriptContext.onConTimer(
    type: ControllerType,
    timer: TimerType,
    action: suspend StandardConAccess.(ControllerTimerEvents.Type) -> Unit,
): Unit = onConAccessEvent(EventBus.composeLongKey(type.id, timer.id), action)

/* Queue functions */
public fun ScriptContext.onAiConQueue(
    type: ControllerType,
    action: ControllerAIEvents.Queue<Nothing>.() -> Unit,
): Unit = onEvent(type.id, action)

public fun <T> ScriptContext.onAiConQueueWithArgs(
    type: ControllerType,
    action: ControllerAIEvents.Queue<T>.() -> Unit,
): Unit = onEvent(type.id, action)

public fun ScriptContext.onConQueue(
    type: QueueType,
    action: suspend StandardConAccess.(ControllerQueueEvents.Default<Nothing>) -> Unit,
): Unit = onConAccessEvent(type.id, action)

public fun <T> ScriptContext.onConQueueWithArgs(
    type: QueueType,
    action: suspend StandardConAccess.(ControllerQueueEvents.Default<T>) -> Unit,
): Unit = onConAccessEvent(type.id, action)

public fun ScriptContext.onConQueue(
    type: ControllerType,
    queue: QueueType,
    action: suspend StandardConAccess.(ControllerQueueEvents.Type<Nothing>) -> Unit,
): Unit = onConAccessEvent(EventBus.composeLongKey(type.id, queue.id), action)

public fun <T> ScriptContext.onConQueueWithArgs(
    type: ControllerType,
    queue: QueueType,
    action: suspend StandardConAccess.(ControllerQueueEvents.Type<T>) -> Unit,
): Unit = onConAccessEvent(EventBus.composeLongKey(type.id, queue.id), action)
