package org.rsmod.api.script

import org.rsmod.api.npc.events.NpcAIEvents
import org.rsmod.api.npc.events.NpcQueueEvents
import org.rsmod.api.npc.events.NpcTimerEvents
import org.rsmod.api.player.events.interact.NpcContentEvents
import org.rsmod.api.player.events.interact.NpcEvents
import org.rsmod.api.player.events.interact.NpcTContentEvents
import org.rsmod.api.player.events.interact.NpcTDefaultEvents
import org.rsmod.api.player.events.interact.NpcTEvents
import org.rsmod.api.player.events.interact.NpcUContentEvents
import org.rsmod.api.player.events.interact.NpcUDefaultEvents
import org.rsmod.api.player.events.interact.NpcUEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
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

public fun ScriptContext.onOpNpcT(
    component: ComponentType,
    action: suspend ProtectedAccess.(NpcTDefaultEvents.Op) -> Unit,
): Unit = onProtectedEvent(component.packed, action)

public fun ScriptContext.onOpNpcT(
    type: NpcType,
    component: ComponentType,
    action: suspend ProtectedAccess.(NpcTEvents.Op) -> Unit,
): Unit = onProtectedEvent((type.id.toLong() shl 32) or component.packed.toLong(), action)

public fun ScriptContext.onOpNpcT(
    content: ContentGroupType,
    component: ComponentType,
    action: suspend ProtectedAccess.(NpcTContentEvents.Op) -> Unit,
): Unit = onProtectedEvent((content.id.toLong() shl 32) or component.packed.toLong(), action)

public fun ScriptContext.onOpNpcU(
    npcType: NpcType,
    action: suspend ProtectedAccess.(NpcUDefaultEvents.OpType) -> Unit,
): Unit = onProtectedEvent(npcType.id, action)

public fun ScriptContext.onOpNpcU(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcUDefaultEvents.OpContent) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpNpcU(
    npcType: NpcType,
    objType: ObjType,
    action: suspend ProtectedAccess.(NpcUEvents.Op) -> Unit,
): Unit = onProtectedEvent((npcType.id.toLong() shl 32) or objType.id.toLong(), action)

public fun ScriptContext.onOpNpcU(
    content: ContentGroupType,
    objType: ObjType,
    action: suspend ProtectedAccess.(NpcUContentEvents.Op) -> Unit,
): Unit = onProtectedEvent((content.id.toLong() shl 32) or objType.id.toLong(), action)

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

public fun ScriptContext.onApNpcT(
    component: ComponentType,
    action: suspend ProtectedAccess.(NpcTDefaultEvents.Ap) -> Unit,
): Unit = onProtectedEvent(component.packed, action)

public fun ScriptContext.onApNpcT(
    type: NpcType,
    component: ComponentType,
    action: suspend ProtectedAccess.(NpcTEvents.Ap) -> Unit,
): Unit = onProtectedEvent((type.id.toLong() shl 32) or component.packed.toLong(), action)

public fun ScriptContext.onApNpcT(
    content: ContentGroupType,
    component: ComponentType,
    action: suspend ProtectedAccess.(NpcTContentEvents.Ap) -> Unit,
): Unit = onProtectedEvent((content.id.toLong() shl 32) or component.packed.toLong(), action)

public fun ScriptContext.onApNpcU(
    npcType: NpcType,
    action: suspend ProtectedAccess.(NpcUDefaultEvents.ApType) -> Unit,
): Unit = onProtectedEvent(npcType.id, action)

public fun ScriptContext.onApNpcU(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(NpcUDefaultEvents.ApContent) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApNpcU(
    npcType: NpcType,
    objType: ObjType,
    action: suspend ProtectedAccess.(NpcUEvents.Ap) -> Unit,
): Unit = onProtectedEvent((npcType.id.toLong() shl 32) or objType.id.toLong(), action)

public fun ScriptContext.onApNpcU(
    content: ContentGroupType,
    objType: ObjType,
    action: suspend ProtectedAccess.(NpcUContentEvents.Ap) -> Unit,
): Unit = onProtectedEvent((content.id.toLong() shl 32) or objType.id.toLong(), action)

/* Timer functions */
public fun ScriptContext.onAiTimer(type: NpcType, action: NpcAIEvents.Type.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onAiTimer(
    content: ContentGroupType,
    action: NpcAIEvents.Content.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onNpcTimer(
    timer: TimerType,
    action: NpcTimerEvents.Default.() -> Unit,
): Unit = onEvent(timer.id, action)

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
public fun ScriptContext.onNpcQueue(
    type: QueueType,
    action: NpcQueueEvents.Default<Nothing>.() -> Unit,
): Unit = onEvent(type.id, action)

public fun <T> ScriptContext.onNpcQueueWithArgs(
    type: QueueType,
    action: NpcQueueEvents.Default<T>.() -> Unit,
): Unit = onEvent(type.id, action)

public fun ScriptContext.onNpcQueue(
    type: NpcType,
    queue: QueueType,
    action: NpcQueueEvents.Type<Nothing>.() -> Unit,
): Unit = onEvent((type.id.toLong() shl 32) or queue.id.toLong(), action)

public fun <T> ScriptContext.onNpcQueueWithArgs(
    type: NpcType,
    queue: QueueType,
    action: NpcQueueEvents.Type<T>.() -> Unit,
): Unit = onEvent((type.id.toLong() shl 32) or queue.id.toLong(), action)

public fun ScriptContext.onNpcQueue(
    content: ContentGroupType,
    queue: QueueType,
    action: NpcQueueEvents.Content<Nothing>.() -> Unit,
): Unit = onEvent((content.id.toLong() shl 32) or queue.id.toLong(), action)

public fun <T> ScriptContext.onNpcQueueWithArgs(
    content: ContentGroupType,
    queue: QueueType,
    action: NpcQueueEvents.Content<T>.() -> Unit,
): Unit = onEvent((content.id.toLong() shl 32) or queue.id.toLong(), action)
