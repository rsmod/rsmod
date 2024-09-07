package org.rsmod.api.script

import org.rsmod.api.player.events.interact.LocContentEvents
import org.rsmod.api.player.events.interact.LocEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.content.ContentType
import org.rsmod.game.type.loc.LocType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onOpLoc1(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op1) -> Unit,
): Unit = onProtectedEvent<LocEvents.Op1>(type.id, action)

public fun ScriptContext.onOpLoc2(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op2) -> Unit,
): Unit = onProtectedEvent<LocEvents.Op2>(type.id, action)

public fun ScriptContext.onOpLoc3(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op3) -> Unit,
): Unit = onProtectedEvent<LocEvents.Op3>(type.id, action)

public fun ScriptContext.onOpLoc4(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op4) -> Unit,
): Unit = onProtectedEvent<LocEvents.Op4>(type.id, action)

public fun ScriptContext.onOpLoc5(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op5) -> Unit,
): Unit = onProtectedEvent<LocEvents.Op5>(type.id, action)

public fun ScriptContext.onOpLoc1(
    content: ContentType,
    action: suspend ProtectedAccess.(LocContentEvents.Op1) -> Unit,
): Unit = onProtectedEvent<LocContentEvents.Op1>(content.id, action)

public fun ScriptContext.onOpLoc2(
    content: ContentType,
    action: suspend ProtectedAccess.(LocContentEvents.Op2) -> Unit,
): Unit = onProtectedEvent<LocContentEvents.Op2>(content.id, action)

public fun ScriptContext.onOpLoc3(
    content: ContentType,
    action: suspend ProtectedAccess.(LocContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent<LocContentEvents.Op3>(content.id, action)

public fun ScriptContext.onOpLoc4(
    content: ContentType,
    action: suspend ProtectedAccess.(LocContentEvents.Op4) -> Unit,
): Unit = onProtectedEvent<LocContentEvents.Op4>(content.id, action)

public fun ScriptContext.onOpLoc5(
    content: ContentType,
    action: suspend ProtectedAccess.(LocContentEvents.Op5) -> Unit,
): Unit = onProtectedEvent<LocContentEvents.Op5>(content.id, action)
