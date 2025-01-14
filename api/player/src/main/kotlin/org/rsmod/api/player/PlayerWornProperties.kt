package org.rsmod.api.player

import kotlin.reflect.KProperty
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.Wearpos

public var Player.hat: InvObj? by WornDelegate(Wearpos.Hat)
public var Player.back: InvObj? by WornDelegate(Wearpos.Back)
public var Player.front: InvObj? by WornDelegate(Wearpos.Front)
public var Player.righthand: InvObj? by WornDelegate(Wearpos.RightHand)
public var Player.torso: InvObj? by WornDelegate(Wearpos.Torso)
public var Player.lefthand: InvObj? by WornDelegate(Wearpos.LeftHand)
public var Player.legs: InvObj? by WornDelegate(Wearpos.Legs)
public var Player.hands: InvObj? by WornDelegate(Wearpos.Hands)
public var Player.feet: InvObj? by WornDelegate(Wearpos.Feet)
public var Player.ring: InvObj? by WornDelegate(Wearpos.Ring)
public var Player.quiver: InvObj? by WornDelegate(Wearpos.Quiver)

private class WornDelegate(private val pos: Wearpos) {
    operator fun getValue(thisRef: Player, property: KProperty<*>): InvObj? = thisRef.worn[pos.slot]

    operator fun setValue(thisRef: Player, property: KProperty<*>, value: InvObj?) {
        thisRef.worn[pos.slot] = value
    }
}
