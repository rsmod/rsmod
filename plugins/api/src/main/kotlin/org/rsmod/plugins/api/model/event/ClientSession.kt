package org.rsmod.plugins.api.model.event

import org.rsmod.game.client.Client
import org.rsmod.game.events.GameEvent

public sealed class ClientSession : GameEvent {

    public data class Connect(val client: Client) : ClientSession()
    public data class Disconnect(val client: Client) : ClientSession()
}
