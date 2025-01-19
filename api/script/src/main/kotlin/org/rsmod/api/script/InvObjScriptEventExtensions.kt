package org.rsmod.api.script

import org.rsmod.api.player.events.interact.InvEquipEvents
import org.rsmod.api.player.events.interact.InvObjContentEvents
import org.rsmod.api.player.events.interact.InvObjDropEvents
import org.rsmod.api.player.events.interact.InvObjEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.droptrig.DropTriggerType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.ScriptContext

/* Drop functions */
public fun ScriptContext.onDropTrigger(
    type: DropTriggerType,
    action: InvObjDropEvents.Trigger.() -> Unit,
): Unit = onEvent(type.id, action)

/* Equip functions */
public fun ScriptContext.onEquipObj(
    content: ContentGroupType,
    action: InvEquipEvents.Equip.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onUnequipObj(
    content: ContentGroupType,
    action: InvEquipEvents.Unequip.() -> Unit,
): Unit = onEvent(content.id, action)

/* Standard obj op functions */
public fun ScriptContext.onInvObj1(
    type: ObjType,
    action: suspend ProtectedAccess.(InvObjEvents.Op1) -> Unit,
): Unit = onProtectedEvent(type.id, action)

/** **Important Note:** This replaces the default wield/wear op handling for obj [type]. */
public fun ScriptContext.onInvObj2(
    type: ObjType,
    action: suspend ProtectedAccess.(InvObjEvents.Op2) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onInvObj3(
    type: ObjType,
    action: suspend ProtectedAccess.(InvObjEvents.Op3) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onInvObj4(
    type: ObjType,
    action: suspend ProtectedAccess.(InvObjEvents.Op4) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onInvObj5(
    type: ObjType,
    action: suspend ProtectedAccess.(InvObjEvents.Op5) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onInvObj6(
    type: ObjType,
    action: suspend ProtectedAccess.(InvObjEvents.Op6) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onInvObj7(
    type: ObjType,
    action: suspend ProtectedAccess.(InvObjEvents.Op7) -> Unit,
): Unit = onProtectedEvent(type.id, action)

/* Standard content op functions */
public fun ScriptContext.onInvObj1(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(InvObjContentEvents.Op1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

/**
 * **Important Note:** This replaces the default wield/wear op handling for content group [content].
 */
public fun ScriptContext.onInvObj2(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(InvObjContentEvents.Op2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onInvObj3(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(InvObjContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onInvObj4(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(InvObjContentEvents.Op4) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onInvObj5(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(InvObjContentEvents.Op5) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onInvObj6(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(InvObjContentEvents.Op6) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onInvObj7(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(InvObjContentEvents.Op7) -> Unit,
): Unit = onProtectedEvent(content.id, action)
