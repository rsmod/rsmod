package org.rsmod.api.script

import org.rsmod.api.player.events.interact.WornObjContentEvents
import org.rsmod.api.player.events.interact.WornObjEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.ScriptContext

/* Standard obj op functions */
/** **Important Note:** This replaces the default unequip op handling for obj [type]. */
public fun ScriptContext.onOpWorn1(
    type: ObjType,
    action: suspend ProtectedAccess.(WornObjEvents.Op1) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpWorn2(
    type: ObjType,
    action: suspend ProtectedAccess.(WornObjEvents.Op2) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpWorn3(
    type: ObjType,
    action: suspend ProtectedAccess.(WornObjEvents.Op3) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpWorn4(
    type: ObjType,
    action: suspend ProtectedAccess.(WornObjEvents.Op4) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpWorn5(
    type: ObjType,
    action: suspend ProtectedAccess.(WornObjEvents.Op5) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpWorn6(
    type: ObjType,
    action: suspend ProtectedAccess.(WornObjEvents.Op6) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpWorn7(
    type: ObjType,
    action: suspend ProtectedAccess.(WornObjEvents.Op7) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpWorn8(
    type: ObjType,
    action: suspend ProtectedAccess.(WornObjEvents.Op8) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpWorn9(
    type: ObjType,
    action: suspend ProtectedAccess.(WornObjEvents.Op9) -> Unit,
): Unit = onProtectedEvent(type.id, action)

/* Standard content op functions */
/**
 * **Important Note:** This replaces the default unequip op handling for content group [content].
 */
public fun ScriptContext.onOpWorn1(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(WornObjContentEvents.Op1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpWorn2(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(WornObjContentEvents.Op2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpWorn3(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(WornObjContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpWorn4(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(WornObjContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpWorn5(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(WornObjContentEvents.Op5) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpWorn6(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(WornObjContentEvents.Op6) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpWorn7(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(WornObjContentEvents.Op7) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpWorn8(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(WornObjContentEvents.Op8) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpWorn9(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(WornObjContentEvents.Op9) -> Unit,
): Unit = onProtectedEvent(content.id, action)
