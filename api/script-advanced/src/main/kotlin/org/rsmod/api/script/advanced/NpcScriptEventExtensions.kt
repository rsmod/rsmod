package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.interact.NpcDefaultEvents
import org.rsmod.api.player.events.interact.NpcUnimplementedEvents
import org.rsmod.api.player.events.interact.OpDefaultEvent
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onProtectedEvent
import org.rsmod.game.type.npc.NpcType
import org.rsmod.plugin.scripts.ScriptContext

/* Unimplemented op functions */
public fun ScriptContext.onUnimplementedOpNpc1(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcUnimplementedEvents.Op1) -> Unit,
): Unit = onProtectedEvent<NpcUnimplementedEvents.Op1>(type.id, action)

public fun ScriptContext.onUnimplementedOpNpc2(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcUnimplementedEvents.Op2) -> Unit,
): Unit = onProtectedEvent<NpcUnimplementedEvents.Op2>(type.id, action)

public fun ScriptContext.onUnimplementedOpNpc3(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcUnimplementedEvents.Op3) -> Unit,
): Unit = onProtectedEvent<NpcUnimplementedEvents.Op3>(type.id, action)

public fun ScriptContext.onUnimplementedOpNpc4(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcUnimplementedEvents.Op4) -> Unit,
): Unit = onProtectedEvent<NpcUnimplementedEvents.Op4>(type.id, action)

public fun ScriptContext.onUnimplementedOpNpc5(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcUnimplementedEvents.Op5) -> Unit,
): Unit = onProtectedEvent<NpcUnimplementedEvents.Op5>(type.id, action)

/* Default op functions */
public fun ScriptContext.onDefaultOpNpc1(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Op1) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Op1>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultOpNpc2(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Op2) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Op2>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultOpNpc3(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Op3) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Op3>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultOpNpc4(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Op4) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Op4>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultOpNpc5(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Op5) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Op5>(OpDefaultEvent.ID, action)

/* Default ap functions */
public fun ScriptContext.onDefaultApNpc1(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Ap1) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Ap1>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultApNpc2(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Ap2) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Ap2>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultApNpc3(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Ap3) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Ap3>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultApNpc4(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Ap4) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Ap4>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultApNpc5(
    action: suspend ProtectedAccess.(NpcDefaultEvents.Ap5) -> Unit
): Unit = onProtectedEvent<NpcDefaultEvents.Ap5>(OpDefaultEvent.ID, action)
