package org.rsmod.plugins.profile.dispatch.client

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.game.client.ClientList
import org.rsmod.game.events.EventBus
import org.rsmod.plugins.api.model.event.ClientSession
import org.rsmod.plugins.profile.dispatch.transaction.TransactionDispatch

@Singleton
public class ClientRegisterDispatch @Inject constructor(
    private val clientList: ClientList,
    private val eventBus: EventBus
) : TransactionDispatch<ClientDispatchRequest, ClientRegisterResponse>() {

    internal fun serve(): Unit = super.serve(TRANSACTIONS_PER_SERVE)

    override fun serve(request: ClientDispatchRequest): ClientRegisterResponse {
        val client = request.client
        clientList += client
        eventBus.publish(ClientSession.Connect(client))
        return ClientRegisterResponse.Success
    }

    private companion object {

        private const val TRANSACTIONS_PER_SERVE = 25
    }
}
