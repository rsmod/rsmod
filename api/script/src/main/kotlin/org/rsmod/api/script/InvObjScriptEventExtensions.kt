package org.rsmod.api.script

import org.rsmod.api.player.events.interact.InvEquipEvents
import org.rsmod.api.player.events.interact.InvObjContentEvents
import org.rsmod.api.player.events.interact.InvObjDropEvents
import org.rsmod.api.player.events.interact.InvObjEvents
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
public fun ScriptContext.onInvObj2(type: ObjType, action: InvObjEvents.Op1.() -> Unit): Unit =
    onEvent(type.id, action)

/** **Important Note:** This replaces the default wield/wear op handling for obj [type]. */
public fun ScriptContext.onInvObj3(type: ObjType, action: InvObjEvents.Op2.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onInvObj4(type: ObjType, action: InvObjEvents.Op3.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onInvObj6(type: ObjType, action: InvObjEvents.Op4.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onInvObj8(type: ObjType, action: InvObjEvents.Op6.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onInvObj9(type: ObjType, action: InvObjEvents.Op7.() -> Unit): Unit =
    onEvent(type.id, action)

/* Standard content op functions */
public fun ScriptContext.onInvObj2(
    content: ContentGroupType,
    action: InvObjContentEvents.Op1.() -> Unit,
): Unit = onEvent(content.id, action)

/**
 * **Important Note:** This replaces the default wield/wear op handling for content group [content].
 */
public fun ScriptContext.onInvObj3(
    content: ContentGroupType,
    action: InvObjContentEvents.Op2.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onInvObj4(
    content: ContentGroupType,
    action: InvObjContentEvents.Op3.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onInvObj6(
    content: ContentGroupType,
    action: InvObjContentEvents.Op4.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onInvObj8(
    content: ContentGroupType,
    action: InvObjContentEvents.Op6.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onInvObj9(
    content: ContentGroupType,
    action: InvObjContentEvents.Op7.() -> Unit,
): Unit = onEvent(content.id, action)
