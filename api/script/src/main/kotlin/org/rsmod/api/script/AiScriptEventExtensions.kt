package org.rsmod.api.script

import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.npc.events.AiTimerEvents
import org.rsmod.api.npc.events.interact.AiPlayerEvents
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.plugin.scripts.ScriptContext

/* Timer functions */
public fun ScriptContext.onAiTimer(type: NpcType, action: AiTimerEvents.Type.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onAiTimer(
    content: ContentGroupType,
    action: AiTimerEvents.Content.() -> Unit,
): Unit = onEvent(content.id, action)

/* Player op functions */
public fun ScriptContext.onAiOpPlayer1(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Op1) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)

public fun ScriptContext.onAiOpPlayer2(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Op2) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)

public fun ScriptContext.onAiOpPlayer3(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Op3) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)

public fun ScriptContext.onAiOpPlayer4(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Op4) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)

public fun ScriptContext.onAiOpPlayer5(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Op5) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)

/* Player ap functions */
public fun ScriptContext.onAiApPlayer1(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Ap1) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)

public fun ScriptContext.onAiApPlayer2(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Ap2) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)

public fun ScriptContext.onAiApPlayer3(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Ap3) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)

public fun ScriptContext.onAiApPlayer4(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Ap4) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)

public fun ScriptContext.onAiApPlayer5(
    npc: NpcType,
    action: suspend StandardNpcAccess.(AiPlayerEvents.Ap5) -> Unit,
): Unit = onNpcAccessEvent(npc.id, action)
