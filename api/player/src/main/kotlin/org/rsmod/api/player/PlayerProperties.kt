package org.rsmod.api.player

import kotlin.reflect.KProperty
import org.rsmod.api.config.refs.BaseVarBits
import org.rsmod.api.config.refs.BaseVarps
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.player.vars.typeIntVarp
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.Wearpos

public var Player.hat: InvObj? by WornDelegate(Wearpos.Hat)
public var Player.back: InvObj? by WornDelegate(Wearpos.Back)
public var Player.front: InvObj? by WornDelegate(Wearpos.Front)
public var Player.righthand: InvObj? by WornDelegate(Wearpos.RightHand)
public var Player.torso: InvObj? by WornDelegate(Wearpos.Torso)
public var Player.lefthand: InvObj? by WornDelegate(Wearpos.LeftHand)
public var Player.arms: InvObj? by WornDelegate(Wearpos.Arms)
public var Player.legs: InvObj? by WornDelegate(Wearpos.Legs)
public var Player.head: InvObj? by WornDelegate(Wearpos.Head)
public var Player.hands: InvObj? by WornDelegate(Wearpos.Hands)
public var Player.feet: InvObj? by WornDelegate(Wearpos.Feet)
public var Player.jaw: InvObj? by WornDelegate(Wearpos.Jaw)
public var Player.ring: InvObj? by WornDelegate(Wearpos.Ring)
public var Player.quiver: InvObj? by WornDelegate(Wearpos.Quiver)

public var Player.chatboxUnlocked: Boolean by boolVarp(BaseVarBits.chatbox_unlocked)

public var Player.varMoveSpeed: MoveSpeed
    get() = varSpeed
    set(value) {
        varSpeed = value
        // Assign as `varSpeed` as it may not have
        // changed due to protected access.
        cachedMoveSpeed = varSpeed
    }

private var Player.varSpeed: MoveSpeed by
    typeIntVarp(BaseVarps.player_run, ::getSpeed, ::getSpeedId)

private fun getSpeed(id: Int?): MoveSpeed =
    when (id) {
        2 -> MoveSpeed.Crawl
        1 -> MoveSpeed.Run
        else -> MoveSpeed.Walk
    }

private fun getSpeedId(speed: MoveSpeed): Int =
    when (speed) {
        MoveSpeed.Crawl -> 2
        MoveSpeed.Run -> 1
        else -> 0
    }

private class WornDelegate(private val pos: Wearpos) {
    operator fun getValue(thisRef: Player, property: KProperty<*>): InvObj? = thisRef.worn[pos.slot]

    operator fun setValue(thisRef: Player, property: KProperty<*>, value: InvObj?) {
        thisRef.worn[pos.slot] = value
    }
}
