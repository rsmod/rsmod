package org.rsmod.api.script

import org.rsmod.api.player.events.interact.LocContentEvents
import org.rsmod.api.player.events.interact.LocEvents
import org.rsmod.api.player.events.interact.LocTContentEvents
import org.rsmod.api.player.events.interact.LocTDefaultEvents
import org.rsmod.api.player.events.interact.LocTEvents
import org.rsmod.api.player.events.interact.LocUContentEvents
import org.rsmod.api.player.events.interact.LocUDefaultEvents
import org.rsmod.api.player.events.interact.LocUEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.EventBus
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.ScriptContext

/* Op functions */
public fun ScriptContext.onOpLoc1(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op1) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpLoc2(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op2) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpLoc3(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op3) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpLoc4(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op4) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpLoc5(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Op5) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpLoc1(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Op1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpLoc2(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Op2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpLoc3(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpLoc4(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Op4) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpLoc5(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Op5) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpLocT(
    component: ComponentType,
    action: suspend ProtectedAccess.(LocTDefaultEvents.Op) -> Unit,
): Unit = onProtectedEvent(component.packed, action)

public fun ScriptContext.onOpLocT(
    type: LocType,
    component: ComponentType,
    action: suspend ProtectedAccess.(LocTEvents.Op) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(type.id, component.packed), action)

public fun ScriptContext.onOpLocT(
    content: ContentGroupType,
    component: ComponentType,
    action: suspend ProtectedAccess.(LocTContentEvents.Op) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(content.id, component.packed), action)

public fun ScriptContext.onOpLocU(
    type: LocType,
    action: suspend ProtectedAccess.(LocUDefaultEvents.OpType) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpLocU(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocUDefaultEvents.OpContent) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpLocU(
    type: LocType,
    objType: ObjType,
    action: suspend ProtectedAccess.(LocUEvents.Op) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(type.id, objType.id), action)

public fun ScriptContext.onOpLocU(
    content: ContentGroupType,
    objType: ObjType,
    action: suspend ProtectedAccess.(LocUContentEvents.OpType) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(content.id, objType.id), action)

public fun ScriptContext.onOpLocU(
    locContent: ContentGroupType,
    objContent: ContentGroupType,
    action: suspend ProtectedAccess.(LocUContentEvents.OpContent) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(locContent.id, objContent.id), action)

/* Ap functions */
public fun ScriptContext.onApLoc1(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Ap1) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApLoc2(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Ap2) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApLoc3(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Ap3) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApLoc4(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Ap4) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApLoc5(
    type: LocType,
    action: suspend ProtectedAccess.(LocEvents.Ap5) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApLoc1(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Ap1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApLoc2(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Ap2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApLoc3(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Ap3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApLoc4(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Ap4) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApLoc5(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocContentEvents.Ap5) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApLocT(
    component: ComponentType,
    action: suspend ProtectedAccess.(LocTDefaultEvents.Ap) -> Unit,
): Unit = onProtectedEvent(component.packed, action)

public fun ScriptContext.onApLocT(
    type: LocType,
    component: ComponentType,
    action: suspend ProtectedAccess.(LocTEvents.Ap) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(type.id, component.packed), action)

public fun ScriptContext.onApLocT(
    content: ContentGroupType,
    component: ComponentType,
    action: suspend ProtectedAccess.(LocTContentEvents.Ap) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(content.id, component.packed), action)

public fun ScriptContext.onApLocU(
    type: LocType,
    action: suspend ProtectedAccess.(LocUDefaultEvents.ApType) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onApLocU(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(LocUDefaultEvents.ApContent) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onApLocU(
    type: LocType,
    objType: ObjType,
    action: suspend ProtectedAccess.(LocUEvents.Ap) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(type.id, objType.id), action)

public fun ScriptContext.onApLocU(
    content: ContentGroupType,
    objType: ObjType,
    action: suspend ProtectedAccess.(LocUContentEvents.ApType) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(content.id, objType.id), action)

public fun ScriptContext.onApLocU(
    locContent: ContentGroupType,
    objContent: ContentGroupType,
    action: suspend ProtectedAccess.(LocUContentEvents.ApContent) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(locContent.id, objContent.id), action)
