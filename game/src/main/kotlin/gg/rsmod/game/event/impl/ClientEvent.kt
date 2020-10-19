package gg.rsmod.game.event.impl

import gg.rsmod.game.event.Event
import gg.rsmod.game.model.client.Client

data class ClientRegister(
    val client: Client
) : Event

data class ClientUnregister(
    val client: Client
) : Event
