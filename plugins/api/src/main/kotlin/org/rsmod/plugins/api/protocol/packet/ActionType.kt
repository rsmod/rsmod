package org.rsmod.plugins.api.protocol.packet

import com.google.common.base.MoreObjects
import org.rsmod.game.action.Action
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Npc
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.npc.type.NpcType
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
    val moveType: MoveType,
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

data class NpcClick(
    val player: Player,
    val moveType: MoveType,
    val action: NpcAction,
    val approach: Boolean
) : Action {

    val type: NpcType
        get() = action.type

    val npc: Npc
        get() = action.npc
}

sealed class NpcAction(
    val player: Player,
    val type: NpcType,
    val npc: Npc
) : Action {

    class Option1(player: Player, type: NpcType, npc: Npc) : NpcAction(player, type, npc)
    class Option2(player: Player, type: NpcType, npc: Npc) : NpcAction(player, type, npc)
    class Option3(player: Player, type: NpcType, npc: Npc) : NpcAction(player, type, npc)
    class Option4(player: Player, type: NpcType, npc: Npc) : NpcAction(player, type, npc)
    class Option5(player: Player, type: NpcType, npc: Npc) : NpcAction(player, type, npc)

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("player", player)
        .add("type", type)
        .add("npc", npc)
        .toString()
}

sealed class ItemAction(
    val player: Player,
    val type: ItemType
) : Action {

    class Inventory1(player: Player, type: ItemType) : ItemAction(player, type)
    class Inventory2(player: Player, type: ItemType) : ItemAction(player, type)
    class Inventory3(player: Player, type: ItemType) : ItemAction(player, type)
    class Inventory4(player: Player, type: ItemType) : ItemAction(player, type)
    class Inventory5(player: Player, type: ItemType) : ItemAction(player, type)
    class Inventory6(player: Player, type: ItemType) : ItemAction(player, type)

    class Equipment1(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment2(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment3(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment4(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment5(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment6(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment7(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment8(player: Player, type: ItemType) : ItemAction(player, type)

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("player", player)
        .add("type", type)
        .toString()
}
