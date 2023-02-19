package org.rsmod.plugins.profile.dispatch.client

import org.rsmod.game.client.Client
import org.rsmod.plugins.profile.dispatch.DispatchRequest

public data class ClientDispatchRequest(val client: Client) : DispatchRequest
