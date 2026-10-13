package org.runerealm.api.script.advanced

import org.runerealm.api.player.events.interact.ObjDefaultEvents
import org.runerealm.api.player.events.interact.OpDefaultEvent
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onProtectedEvent
import org.runerealm.plugin.scripts.ScriptContext

public fun ScriptContext.onDefaultOpObj1(
    action: suspend ProtectedAccess.(ObjDefaultEvents.Op1) -> Unit
): Unit = onProtectedEvent<ObjDefaultEvents.Op1>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultOpObj2(
    action: suspend ProtectedAccess.(ObjDefaultEvents.Op2) -> Unit
): Unit = onProtectedEvent<ObjDefaultEvents.Op2>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultOpObj3(
    action: suspend ProtectedAccess.(ObjDefaultEvents.Op3) -> Unit
): Unit = onProtectedEvent<ObjDefaultEvents.Op3>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultOpObj4(
    action: suspend ProtectedAccess.(ObjDefaultEvents.Op4) -> Unit
): Unit = onProtectedEvent<ObjDefaultEvents.Op4>(OpDefaultEvent.ID, action)

public fun ScriptContext.onDefaultOpObj5(
    action: suspend ProtectedAccess.(ObjDefaultEvents.Op5) -> Unit
): Unit = onProtectedEvent<ObjDefaultEvents.Op5>(OpDefaultEvent.ID, action)
