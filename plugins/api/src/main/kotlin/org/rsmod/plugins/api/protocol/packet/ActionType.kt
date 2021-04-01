package org.rsmod.plugins.api.protocol.packet

import com.google.common.base.MoreObjects
import org.rsmod.game.action.Action
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.obj.type.ObjectType
import org.rsmod.game.model.ui.Component

sealed class MoveType {
    object Neutral : MoveType()
    object ForceWalk : MoveType()
    object ForceRun : MoveType()
    object Displace : MoveType()
}

data class MapMove(
    val player: Player,
    val destination: Coordinates,
    val type: MoveType,
    val noclip: Boolean = false
) : Action

data class ButtonClick(
    val player: Player,
    val clickType: Int,
    val component: Component,
    val child: Int,
    val item: Int
) : Action

data class ObjectClick(
    val player: Player,
    val action: ObjectAction,
    val approach: Boolean
) : Action {

    val type: ObjectType
        get() = action.type

    val shape: Int
        get() = action.shape

    val rot: Int
        get() = action.rot

    val coords: Coordinates
        get() = action.coords
}

sealed class ObjectAction(
    val player: Player,
    val type: ObjectType,
    val shape: Int,
    val rot: Int,
    val coords: Coordinates
) : Action {

    class Option1(player: Player, type: ObjectType, shape: Int, rot: Int, coords: Coordinates) :
        ObjectAction(player, type, shape, rot, coords)

    class Option2(player: Player, type: ObjectType, shape: Int, rot: Int, coords: Coordinates) :
        ObjectAction(player, type, shape, rot, coords)

    class Option3(player: Player, type: ObjectType, shape: Int, rot: Int, coords: Coordinates) :
        ObjectAction(player, type, shape, rot, coords)

    class Option4(player: Player, type: ObjectType, shape: Int, rot: Int, coords: Coordinates) :
        ObjectAction(player, type, shape, rot, coords)

    class Option5(player: Player, type: ObjectType, shape: Int, rot: Int, coords: Coordinates) :
        ObjectAction(player, type, shape, rot, coords)

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("player", player)
        .add("type", type)
        .add("shape", shape)
        .add("rot", rot)
        .add("coords", coords)
        .toString()
}
