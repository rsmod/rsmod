package org.rsmod.api.script

import org.rsmod.api.npc.events.NpcAIEvents
import org.rsmod.api.npc.events.NpcQueueEvent
import org.rsmod.api.npc.events.NpcTimerEvents
import org.rsmod.api.player.events.interact.NpcContentEvents
import org.rsmod.api.player.events.interact.NpcEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.plugin.scripts.ScriptContext

/* Op functions */
public fun ScriptContext.onOpNpc1(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Op1) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpNpc2(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Op2) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpNpc3(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Op3) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpNpc4(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Op4) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpNpc5(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Op5) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpNpc1(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpNpc2(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpNpc3(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpNpc4(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op4) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpNpc5(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op5) -> Unit,
): Unit = onProtectedEvent(content.id, action)

/* Ap functions */
public fun ScriptContext.onApNpc1(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap1) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApNpc2(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap2) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApNpc3(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap3) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApNpc4(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap4) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApNpc5(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap5) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApNpc1(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApNpc2(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApNpc3(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApNpc4(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap4) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApNpc5(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap5) -> Unit,
): Unit = onProtectedEvent(content.id, action)

/* Timer functions */
public fun ScriptContext.onAiTimer(type: NpcType, action: NpcAIEvents.Type.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onAiTimer(
    content: ContentGroupType,
    action: NpcAIEvents.Content.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onNpcTimer(
    type: NpcType,
    action: NpcTimerEvents.Default.() -> Unit,
): Unit = onEvent(type.id, action)

public fun ScriptContext.onNpcTimer(
    npc: NpcType,
    timer: TimerType,
    action: NpcTimerEvents.Type.() -> Unit,
): Unit = onEvent((npc.id.toLong() shl 32) or timer.id.toLong(), action)

public fun ScriptContext.onNpcTimer(
    content: ContentGroupType,
    timer: TimerType,
    action: NpcTimerEvents.Content.() -> Unit,
): Unit = onEvent((content.id.toLong() shl 32) or timer.id.toLong(), action)

/* Queue functions */
public fun ScriptContext.onNpcQueue(type: QueueType, action: NpcQueueEvent.() -> Unit): Unit =
    onEvent(type.id, action)
