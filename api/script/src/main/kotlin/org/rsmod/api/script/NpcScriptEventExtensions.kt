package org.rsmod.api.script

import org.rsmod.api.npc.events.NpcAIEvents
import org.rsmod.api.npc.events.NpcTimerEvent
import org.rsmod.api.player.events.interact.NpcContentEvents
import org.rsmod.api.player.events.interact.NpcEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.content.ContentType
import org.rsmod.game.type.npc.NpcType
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
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpNpc2(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpNpc3(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpNpc4(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op4) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpNpc5(
    content: ContentType,
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
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApNpc2(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApNpc3(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApNpc4(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap4) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApNpc5(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap5) -> Unit,
): Unit = onProtectedEvent(content.id, action)

/* Timer functions */
public fun ScriptContext.onAiTimer(type: NpcType, action: NpcAIEvents.Type.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onAiTimer(
    content: ContentType,
    action: NpcAIEvents.Content.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onNpcTimer(type: NpcType, action: NpcTimerEvent.() -> Unit): Unit =
    onEvent(type.id, action)
