package org.rsmod.plugins.api.event

import org.rsmod.game.client.Client
import org.rsmod.game.events.Event

sealed class ClientSession : Event {

    data class Connect(val client: Client) : ClientSession()
    data class Disconnect(val client: Client) : ClientSession()
}
