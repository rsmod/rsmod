package org.rsmod.api.script

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
): Unit = onProtectedEvent<NpcEvents.Op1>(type.id, action)

public fun ScriptContext.onOpNpc2(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Op2) -> Unit,
): Unit = onProtectedEvent<NpcEvents.Op2>(type.id, action)

public fun ScriptContext.onOpNpc3(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Op3) -> Unit,
): Unit = onProtectedEvent<NpcEvents.Op3>(type.id, action)

public fun ScriptContext.onOpNpc4(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Op4) -> Unit,
): Unit = onProtectedEvent<NpcEvents.Op4>(type.id, action)

public fun ScriptContext.onOpNpc5(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Op5) -> Unit,
): Unit = onProtectedEvent<NpcEvents.Op5>(type.id, action)

public fun ScriptContext.onOpNpc1(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op1) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Op1>(content.id, action)

public fun ScriptContext.onOpNpc2(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op2) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Op2>(content.id, action)

public fun ScriptContext.onOpNpc3(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Op3>(content.id, action)

public fun ScriptContext.onOpNpc4(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op4) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Op4>(content.id, action)

public fun ScriptContext.onOpNpc5(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Op5) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Op5>(content.id, action)

/* Ap functions */
public fun ScriptContext.onApNpc1(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap1) -> Unit,
): Unit = onProtectedEvent<NpcEvents.Ap1>(type.id, action)

public fun ScriptContext.onApNpc2(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap2) -> Unit,
): Unit = onProtectedEvent<NpcEvents.Ap2>(type.id, action)

public fun ScriptContext.onApNpc3(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap3) -> Unit,
): Unit = onProtectedEvent<NpcEvents.Ap3>(type.id, action)

public fun ScriptContext.onApNpc4(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap4) -> Unit,
): Unit = onProtectedEvent<NpcEvents.Ap4>(type.id, action)

public fun ScriptContext.onApNpc5(
    type: NpcType,
    action: suspend ProtectedAccess.(NpcEvents.Ap5) -> Unit,
): Unit = onProtectedEvent<NpcEvents.Ap5>(type.id, action)

public fun ScriptContext.onApNpc1(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap1) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Ap1>(content.id, action)

public fun ScriptContext.onApNpc2(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap2) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Ap2>(content.id, action)

public fun ScriptContext.onApNpc3(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap3) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Ap3>(content.id, action)

public fun ScriptContext.onApNpc4(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap4) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Ap4>(content.id, action)

public fun ScriptContext.onApNpc5(
    content: ContentType,
    action: suspend ProtectedAccess.(NpcContentEvents.Ap5) -> Unit,
): Unit = onProtectedEvent<NpcContentEvents.Ap5>(content.id, action)
