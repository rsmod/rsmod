package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.interact.ApDefaultEvent
import org.rsmod.api.player.events.interact.OpDefaultEvent
import org.rsmod.api.player.events.interact.PlayerEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onProtectedEvent
import org.rsmod.plugin.scripts.ScriptContext

/* Op functions */
public fun ScriptContext.onOpPlayer1(
    action: suspend ProtectedAccess.(PlayerEvents.Op1) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Op1>(OpDefaultEvent.ID, action)

public fun ScriptContext.onOpPlayer2(
    action: suspend ProtectedAccess.(PlayerEvents.Op2) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Op2>(OpDefaultEvent.ID, action)

public fun ScriptContext.onOpPlayer3(
    action: suspend ProtectedAccess.(PlayerEvents.Op3) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Op3>(OpDefaultEvent.ID, action)

public fun ScriptContext.onOpPlayer4(
    action: suspend ProtectedAccess.(PlayerEvents.Op4) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Op4>(OpDefaultEvent.ID, action)

public fun ScriptContext.onOpPlayer5(
    action: suspend ProtectedAccess.(PlayerEvents.Op5) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Op5>(OpDefaultEvent.ID, action)

/* Ap functions */
public fun ScriptContext.onApPlayer1(
    action: suspend ProtectedAccess.(PlayerEvents.Ap1) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Ap1>(ApDefaultEvent.ID, action)

public fun ScriptContext.onApPlayer2(
    action: suspend ProtectedAccess.(PlayerEvents.Ap2) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Ap2>(ApDefaultEvent.ID, action)

public fun ScriptContext.onApPlayer3(
    action: suspend ProtectedAccess.(PlayerEvents.Ap3) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Ap3>(ApDefaultEvent.ID, action)

public fun ScriptContext.onApPlayer4(
    action: suspend ProtectedAccess.(PlayerEvents.Ap4) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Ap4>(ApDefaultEvent.ID, action)

public fun ScriptContext.onApPlayer5(
    action: suspend ProtectedAccess.(PlayerEvents.Ap5) -> Unit
): Unit = onProtectedEvent<PlayerEvents.Ap5>(ApDefaultEvent.ID, action)
