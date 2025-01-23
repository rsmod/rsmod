package org.rsmod.api.script

import org.rsmod.api.controller.events.ControllerAIEvents
import org.rsmod.api.controller.events.ControllerQueueEvents
import org.rsmod.api.controller.events.ControllerTimerEvents
import org.rsmod.game.type.controller.ControllerType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.plugin.scripts.ScriptContext

/* Timer functions */
public fun ScriptContext.onAiConTimer(
    type: ControllerType,
    action: ControllerAIEvents.Timer.() -> Unit,
): Unit = onEvent(type.id, action)

public fun ScriptContext.onConTimer(
    type: ControllerType,
    action: ControllerTimerEvents.Type.() -> Unit,
): Unit = onEvent(type.id, action)

public fun ScriptContext.onConTimer(
    type: ControllerType,
    timer: TimerType,
    action: ControllerTimerEvents.Type.() -> Unit,
): Unit = onEvent((type.id.toLong() shl 32) or timer.id.toLong(), action)

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
    action: ControllerQueueEvents.Default<Nothing>.() -> Unit,
): Unit = onEvent(type.id, action)

public fun <T> ScriptContext.onConQueueWithArgs(
    type: QueueType,
    action: ControllerQueueEvents.Default<T>.() -> Unit,
): Unit = onEvent(type.id, action)

public fun ScriptContext.onConQueue(
    type: ControllerType,
    queue: QueueType,
    action: ControllerQueueEvents.Type<Nothing>.() -> Unit,
): Unit = onEvent((type.id.toLong() shl 32) or queue.id.toLong(), action)

public fun <T> ScriptContext.onConQueueWithArgs(
    type: ControllerType,
    queue: QueueType,
    action: ControllerQueueEvents.Type<T>.() -> Unit,
): Unit = onEvent((type.id.toLong() shl 32) or queue.id.toLong(), action)
