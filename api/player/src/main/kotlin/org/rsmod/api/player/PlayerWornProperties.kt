package org.rsmod.api.player

import kotlin.reflect.KProperty
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.obj.Wearpos

/*
 * Using these properties to set a player's equipment will **not** trigger any `onEquipObj`
 * or `onUnequipObj` events. For example:
 * - Assigning a value to `Player.hat` will not invoke an `onUnequipObj` event for any previously
 *   equipped hat.
 * - Similarly, equipping an object via these properties will not trigger `onEquipObj`.
 *
 * These properties are primarily intended for direct manipulation of equipment state (e.g.,
 * initialization or debugging) where event handling is unnecessary.
 *
 * If you need to handle equip/unequip events, use the appropriate mechanisms elsewhere in the
 * system to ensure consistency.
 */

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.hat: InvObj? by WornDelegate(Wearpos.Hat)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.back: InvObj? by WornDelegate(Wearpos.Back)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.front: InvObj? by WornDelegate(Wearpos.Front)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.righthand: InvObj? by WornDelegate(Wearpos.RightHand)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.torso: InvObj? by WornDelegate(Wearpos.Torso)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.lefthand: InvObj? by WornDelegate(Wearpos.LeftHand)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.legs: InvObj? by WornDelegate(Wearpos.Legs)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.hands: InvObj? by WornDelegate(Wearpos.Hands)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.feet: InvObj? by WornDelegate(Wearpos.Feet)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.ring: InvObj? by WornDelegate(Wearpos.Ring)

/** When using as a setter, be sure to read warning in [WornDelegate]. */
public var Player.quiver: InvObj? by WornDelegate(Wearpos.Quiver)

/**
 * Delegate for managing worn equipment slots.
 *
 * Note: Using properties that rely on this delegate to set equipment will **not** trigger any
 * `onEquipObj` or `onUnequipObj` events. These properties are meant for direct manipulation of
 * equipment state (e.g., initialization or debugging) without triggering event handlers.
 */
private class WornDelegate(private val pos: Wearpos) {
    operator fun getValue(thisRef: Player, property: KProperty<*>): InvObj? = thisRef.worn[pos.slot]

    operator fun setValue(thisRef: Player, property: KProperty<*>, value: InvObj?) {
        thisRef.worn[pos.slot] = value
        thisRef.rebuildAppearance()
    }
}
