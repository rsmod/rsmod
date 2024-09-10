package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.interact.NpcUnimplementedEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onProtectedEvent
import org.rsmod.game.type.npc.NpcType
import org.rsmod.plugin.scripts.ScriptContext

/* Op functions */
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
