package org.runerealm.api.script

import org.runerealm.api.player.events.interact.ObjContentEvents
import org.runerealm.api.player.events.interact.ObjEvents
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.game.type.content.ContentGroupType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.plugin.scripts.ScriptContext

public fun ScriptContext.onOpObj1(
    type: ObjType,
    action: suspend ProtectedAccess.(ObjEvents.Op1) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpObj2(
    type: ObjType,
    action: suspend ProtectedAccess.(ObjEvents.Op2) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpObj3(
    type: ObjType,
    action: suspend ProtectedAccess.(ObjEvents.Op3) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpObj4(
    type: ObjType,
    action: suspend ProtectedAccess.(ObjEvents.Op4) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpObj5(
    type: ObjType,
    action: suspend ProtectedAccess.(ObjEvents.Op5) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpObj1(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(ObjContentEvents.Op1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpObj2(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(ObjContentEvents.Op2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpObj3(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(ObjContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpObj4(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(ObjContentEvents.Op4) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpObj5(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(ObjContentEvents.Op5) -> Unit,
): Unit = onProtectedEvent(content.id, action)
