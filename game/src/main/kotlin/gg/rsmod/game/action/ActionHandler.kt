package gg.rsmod.game.action

import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.mob.Player

interface ActionHandler<T : Action> {
    fun handle(client: Client, player: Player, action: T)
}
