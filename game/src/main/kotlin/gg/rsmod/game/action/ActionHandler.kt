package gg.rsmod.game.action

import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.mob.Player

data class ActionMessage<T : Action>(
    val action: T,
    val handler: ActionHandler<Action>
)

interface ActionHandler<T : Action> {

    fun handle(client: Client, player: Player, packet: T)
}
