package org.rsmod.plugins.profile.dispatch.client

import org.rsmod.game.client.ClientList
import org.rsmod.game.events.GameEventBus
import org.rsmod.plugins.api.model.event.ClientSession
import org.rsmod.plugins.api.publish
import org.rsmod.plugins.profile.dispatch.transaction.TransactionDispatch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class ClientDeregisterDispatch @Inject constructor(
    private val clientList: ClientList,
    private val eventBus: GameEventBus
) : TransactionDispatch<ClientDispatchRequest, ClientDeregisterResponse>() {

    internal fun serve(): Unit = super.serve(TRANSACTIONS_PER_SERVE)

    override fun serve(request: ClientDispatchRequest): ClientDeregisterResponse {
        val client = request.client
        val player = client.player
        clientList -= client
        player.publish(ClientSession.Disconnect(client), eventBus)
        return ClientDeregisterResponse.Success
    }

    private companion object {

        private const val TRANSACTIONS_PER_SERVE = 25
    }
}
