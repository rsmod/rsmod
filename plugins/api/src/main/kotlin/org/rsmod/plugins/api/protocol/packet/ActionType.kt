package org.rsmod.plugins.api.protocol.packet

import com.google.common.base.MoreObjects
import org.rsmod.game.action.Action
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Npc
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.npc.type.NpcType
import org.rsmod.game.model.obj.GameObject
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
) : Action {

    override fun toString() = MoreObjects.toStringHelper(this)
        .add("dest", destination)
        .add("type", type)
        .add("noclip", noclip)
        .add("player", player)
        .toString()
}

data class ButtonClick(
    val player: Player,
    val clickType: Int,
    val component: Component,
    val child: Int,
    val item: Int
) : Action {

    override fun toString() = MoreObjects.toStringHelper(this)
        .add("component", component)
        .add("slot", child)
        .add("clickType", clickType)
        .add("item", item)
        .add("player", player)
        .toString()
}

data class ObjectClick(
    val player: Player,
    val moveType: MoveType,
    val action: ObjectAction,
    val approach: Boolean
) : Action {

    val obj: GameObject
        get() = action.obj

    override fun toString() = MoreObjects.toStringHelper(this)
        .add("object", action.obj)
        .add("moveType", moveType)
        .add("approach", approach)
        .add("player", player)
        .toString()

    data class ExamineAction(val player: Player, val type: ObjectType) : Action
}

sealed class ObjectAction(
    val player: Player,
    val obj: GameObject
) : Action {

    class Option1(player: Player, obj: GameObject) : ObjectAction(player, obj)
    class Option2(player: Player, obj: GameObject) : ObjectAction(player, obj)
    class Option3(player: Player, obj: GameObject) : ObjectAction(player, obj)
    class Option4(player: Player, obj: GameObject) : ObjectAction(player, obj)
    class Option5(player: Player, obj: GameObject) : ObjectAction(player, obj)

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("object", obj)
        .add("player", player)
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

    override fun toString() = MoreObjects.toStringHelper(this)
        .add("npc", npc)
        .add("moveType", moveType)
        .add("approach", approach)
        .add("player", player)
        .toString()

    data class ExamineAction(val player: Player, val type: NpcType) : Action
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
        .add("type", type)
        .add("npc", npc)
        .add("player", player)
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
    class ExamineAction(player: Player, type: ItemType) : ItemAction(player, type)

    class Equipment1(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment2(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment3(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment4(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment5(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment6(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment7(player: Player, type: ItemType) : ItemAction(player, type)
    class Equipment8(player: Player, type: ItemType) : ItemAction(player, type)

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("type", type)
        .add("player", player)
        .toString()
}
