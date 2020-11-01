package org.rsmod.game.event.impl

import org.rsmod.game.event.Event
import org.rsmod.game.model.client.Client

data class ClientRegister(
    val client: Client
) : Event

data class ClientUnregister(
    val client: Client
) : Event
