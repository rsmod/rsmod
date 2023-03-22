package org.rsmod.plugins.api.model.event

import org.rsmod.game.client.Client

public sealed class ClientSession : TypeGameEvent {

    public data class Connect(val client: Client) : ClientSession()
    public data class Disconnect(val client: Client) : ClientSession()
}
