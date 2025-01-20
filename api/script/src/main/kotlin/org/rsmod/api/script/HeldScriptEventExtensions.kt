package org.rsmod.api.script

import org.rsmod.api.player.events.interact.HeldContentEvents
import org.rsmod.api.player.events.interact.HeldDropEvents
import org.rsmod.api.player.events.interact.HeldEquipEvents
import org.rsmod.api.player.events.interact.HeldObjEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.droptrig.DropTriggerType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.ScriptContext

/* Drop functions */
public fun ScriptContext.onDropTrigger(
    type: DropTriggerType,
    action: HeldDropEvents.Trigger.() -> Unit,
): Unit = onEvent(type.id, action)

/* Equip functions */
public fun ScriptContext.onEquipObj(
    content: ContentGroupType,
    action: HeldEquipEvents.Equip.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onUnequipObj(
    content: ContentGroupType,
    action: HeldEquipEvents.Unequip.() -> Unit,
): Unit = onEvent(content.id, action)

/* Standard obj op functions */
public fun ScriptContext.onOpHeld1(
    type: ObjType,
    action: suspend ProtectedAccess.(HeldObjEvents.Op1) -> Unit,
): Unit = onProtectedEvent(type.id, action)

/** **Important Note:** This replaces the default wield/wear op handling for obj [type]. */
public fun ScriptContext.onOpHeld2(
    type: ObjType,
    action: suspend ProtectedAccess.(HeldObjEvents.Op2) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpHeld3(
    type: ObjType,
    action: suspend ProtectedAccess.(HeldObjEvents.Op3) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpHeld4(
    type: ObjType,
    action: suspend ProtectedAccess.(HeldObjEvents.Op4) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpHeld5(
    type: ObjType,
    action: suspend ProtectedAccess.(HeldObjEvents.Op5) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpHeld6(
    type: ObjType,
    action: suspend ProtectedAccess.(HeldObjEvents.Op6) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun ScriptContext.onOpHeld7(
    type: ObjType,
    action: suspend ProtectedAccess.(HeldObjEvents.Op7) -> Unit,
): Unit = onProtectedEvent(type.id, action)

/* Standard content op functions */
public fun ScriptContext.onOpHeld1(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(HeldContentEvents.Op1) -> Unit,
): Unit = onProtectedEvent(content.id, action)

/**
 * **Important Note:** This replaces the default wield/wear op handling for content group [content].
 */
public fun ScriptContext.onOpHeld2(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(HeldContentEvents.Op2) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpHeld3(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(HeldContentEvents.Op3) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpHeld4(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(HeldContentEvents.Op4) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpHeld5(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(HeldContentEvents.Op5) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpHeld6(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(HeldContentEvents.Op6) -> Unit,
): Unit = onProtectedEvent(content.id, action)

public fun ScriptContext.onOpHeld7(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(HeldContentEvents.Op7) -> Unit,
): Unit = onProtectedEvent(content.id, action)
