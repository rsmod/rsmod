package org.rsmod.api.script.advanced

import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.npc.events.interact.AiPlayerDefaultEvents
import org.rsmod.api.npc.events.interact.ApDefaultEvent
import org.rsmod.api.npc.events.interact.OpDefaultEvent
import org.rsmod.api.script.onNpcAccessEvent
import org.rsmod.plugin.scripts.ScriptContext

/* Player op functions */
public fun ScriptContext.onDefaultAiOpPlayer1(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Op1) -> Unit
): Unit = onNpcAccessEvent(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultAiOpPlayer2(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Op2) -> Unit
): Unit = onNpcAccessEvent(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultAiOpPlayer3(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Op3) -> Unit
): Unit = onNpcAccessEvent(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultAiOpPlayer4(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Op4) -> Unit
): Unit = onNpcAccessEvent(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultAiOpPlayer5(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Op5) -> Unit
): Unit = onNpcAccessEvent(OpDefaultEvent.ID, action)

/* Player ap functions */
public fun ScriptContext.onDefaultAiApPlayer1(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Ap1) -> Unit
): Unit = onNpcAccessEvent(ApDefaultEvent.ID, action)

public fun ScriptContext.onDefaultAiApPlayer2(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Ap2) -> Unit
): Unit = onNpcAccessEvent(ApDefaultEvent.ID, action)

public fun ScriptContext.onDefaultAiApPlayer3(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Ap3) -> Unit
): Unit = onNpcAccessEvent(ApDefaultEvent.ID, action)

public fun ScriptContext.onDefaultAiApPlayer4(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Ap4) -> Unit
): Unit = onNpcAccessEvent(ApDefaultEvent.ID, action)

public fun ScriptContext.onDefaultAiApPlayer5(
    action: suspend StandardNpcAccess.(AiPlayerDefaultEvents.Ap5) -> Unit
): Unit = onNpcAccessEvent(ApDefaultEvent.ID, action)
