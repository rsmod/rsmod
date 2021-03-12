package org.rsmod.game.event.impl

import org.rsmod.game.event.Event
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.mob.Player

data class AccountCreation(
    val client: Client
) : Event {

    val player: Player
        get() = client.player
}
