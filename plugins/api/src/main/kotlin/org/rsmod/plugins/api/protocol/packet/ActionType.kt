package org.rsmod.plugins.api.protocol.packet

import org.rsmod.game.action.Action
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.obj.type.ObjectType
import org.rsmod.game.model.step.StepSpeed

data class MapMove(val player: Player, val destination: Coordinates, val speed: StepSpeed) : Action

data class ObjectClick(
    val player: Player,
    val type: ObjectType,
    val coords: Coordinates,
    val action: ObjectAction,
    val approach: Boolean
) : Action

sealed class ObjectAction(
    val player: Player,
    val type: ObjectType,
    val coords: Coordinates
) : Action {

    class Option1(player: Player, type: ObjectType, coords: Coordinates) : ObjectAction(player, type, coords)
    class Option2(player: Player, type: ObjectType, coords: Coordinates) : ObjectAction(player, type, coords)
    class Option3(player: Player, type: ObjectType, coords: Coordinates) : ObjectAction(player, type, coords)
    class Option4(player: Player, type: ObjectType, coords: Coordinates) : ObjectAction(player, type, coords)
    class Option5(player: Player, type: ObjectType, coords: Coordinates) : ObjectAction(player, type, coords)
}
