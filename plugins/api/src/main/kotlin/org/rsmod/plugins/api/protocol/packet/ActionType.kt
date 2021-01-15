package org.rsmod.plugins.api.protocol.packet

import org.rsmod.game.action.Action
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.obj.GameObject
import org.rsmod.game.model.step.StepSpeed

data class MapMove(val player: Player, val destination: Coordinates, val speed: StepSpeed) : Action

data class ObjectClick(
    val player: Player,
    val obj: GameObject,
    val action: ObjectAction,
    val approach: Boolean
) : Action

sealed class ObjectAction(val player: Player, val obj: GameObject) : Action {

    class Operate1(player: Player, obj: GameObject) : ObjectAction(player, obj)
    class Operate2(player: Player, obj: GameObject) : ObjectAction(player, obj)
    class Operate3(player: Player, obj: GameObject) : ObjectAction(player, obj)
    class Operate4(player: Player, obj: GameObject) : ObjectAction(player, obj)
    class Operate5(player: Player, obj: GameObject) : ObjectAction(player, obj)
}
