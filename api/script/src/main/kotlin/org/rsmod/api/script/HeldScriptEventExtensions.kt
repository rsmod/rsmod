package org.rsmod.api.script

import org.rsmod.api.player.events.interact.HeldContentEvents
import org.rsmod.api.player.events.interact.HeldDropEvents
import org.rsmod.api.player.events.interact.HeldEquipEvents
import org.rsmod.api.player.events.interact.HeldObjEvents
import org.rsmod.api.player.events.interact.HeldUContentEvents
import org.rsmod.api.player.events.interact.HeldUDefaultEvents
import org.rsmod.api.player.events.interact.HeldUEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.EventBus
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

/** **Important Note:** This replaces the default drop op handling for obj [type]. */
public fun ScriptContext.onOpHeld5(
    type: ObjType,
    action: suspend ProtectedAccess.(HeldObjEvents.Op5) -> Unit,
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

/** **Important Note:** This replaces the default drop op handling for content group [content]. */
public fun ScriptContext.onOpHeld5(
    content: ContentGroupType,
    action: suspend ProtectedAccess.(HeldContentEvents.Op5) -> Unit,
): Unit = onProtectedEvent(content.id, action)

/* HeldU (inv obj on inv obj) functions */
/**
 * Registers a script that triggers when an inventory obj ([first]) is used on another inventory obj
 * ([second]).
 *
 * The [HeldUEvents.Type.first] and [HeldUEvents.Type.second] values passed to the script will
 * **always match** the registration order in this function. That is, [first] will always correspond
 * to `HeldUEvents.Type.first`, and [second] to `HeldUEvents.Type.second`, regardless of which obj
 * the player uses on the other in-game.
 */
public fun ScriptContext.onOpHeldU(
    first: ObjType,
    second: ObjType,
    action: suspend ProtectedAccess.(HeldUEvents.Type) -> Unit,
) {
    // Note: We preserve the order of `first` and `second` when registering to expose a predictable
    // and fixed order in the respective script. Because of this, we can't rely on the event bus to
    // catch duplicate registrations - we must manually check that the reversed combination has
    // not already been registered.
    val opposite = EventBus.composeLongKey(second.id, first.id)
    val registeredOpposite = eventBus.contains(HeldUEvents.Type::class.java, opposite)
    if (registeredOpposite) {
        val message = "OpHeldU for combination already registered: first=$second, second=$first"
        throw IllegalStateException(message)
    }
    onProtectedEvent(EventBus.composeLongKey(first.id, second.id), action)
}

/**
 * Registers a script that triggers when an inventory obj ([first]) is used on another inventory obj
 * ([second]).
 *
 * The [HeldUContentEvents.Type.first] and [HeldUContentEvents.Type.second] values passed to the
 * script will **always match** the registration order in this function. That is, [first] will
 * always correspond to `HeldUContentEvents.Type.first`, and [second] to
 * `HeldUContentEvents.Type.second`, regardless of which obj the player uses on the other in-game.
 */
public fun ScriptContext.onOpHeldU(
    first: ContentGroupType,
    second: ObjType,
    action: suspend ProtectedAccess.(HeldUContentEvents.Type) -> Unit,
): Unit = onProtectedEvent(EventBus.composeLongKey(first.id, second.id), action)

/**
 * Registers a script that triggers when an inventory obj ([first]) is used on another inventory obj
 * ([second]).
 *
 * The [HeldUContentEvents.Content.first] and [HeldUContentEvents.Content.second] values passed to
 * the script will **always match** the registration order in this function. That is, [first] will
 * always correspond to `HeldUContentEvents.Content.first`, and [second] to
 * `HeldUContentEvents.Content.second`, regardless of which obj the player uses on the other
 * in-game.
 */
public fun ScriptContext.onOpHeldU(
    first: ContentGroupType,
    second: ContentGroupType,
    action: suspend ProtectedAccess.(HeldUContentEvents.Content) -> Unit,
) {
    // Note: We preserve the order of `first` and `second` when registering to expose a predictable
    // and fixed order in the respective script. Because of this, we can't rely on the event bus to
    // catch duplicate registrations - we must manually check that the reversed combination has
    // not already been registered.
    val opposite = EventBus.composeLongKey(second.id, first.id)
    val registeredOpposite = eventBus.contains(HeldUContentEvents.Content::class.java, opposite)
    if (registeredOpposite) {
        val message = "OpHeldU for combination already registered: first=$second, second=$first"
        throw IllegalStateException(message)
    }
    onProtectedEvent(EventBus.composeLongKey(first.id, second.id), action)
}

/**
 * Registers a script that triggers when an inventory obj ([first]) is used on _any other_ inventory
 * obj.
 *
 * The [HeldUDefaultEvents.Type.first] value passed to the script will **always** be [first], while
 * the target obj will be [HeldUDefaultEvents.Type.second].
 */
public fun ScriptContext.onOpHeldU(
    first: ObjType,
    action: suspend ProtectedAccess.(HeldUDefaultEvents.Type) -> Unit,
): Unit = onProtectedEvent(first.id.toLong(), action)

/**
 * Registers a script that triggers when an inventory obj ([first]) is used on _any other_ inventory
 * obj.
 *
 * The [HeldUDefaultEvents.Content.first] value passed to the script will **always** be [first],
 * while the target obj will be [HeldUDefaultEvents.Content.second].
 */
public fun ScriptContext.onOpHeldU(
    first: ContentGroupType,
    action: suspend ProtectedAccess.(HeldUDefaultEvents.Content) -> Unit,
): Unit = onProtectedEvent(first.id.toLong(), action)
