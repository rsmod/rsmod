package org.rsmod.plugins.api.event

import org.rsmod.game.client.Client
import org.rsmod.game.events.Event

public sealed class ClientSession : Event {

    public data class Connect(val client: Client) : ClientSession()
    public data class Disconnect(val client: Client) : ClientSession()
}
