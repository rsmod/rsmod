package org.rsmod.plugins.profile.dispatch.client

import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.client.ClientList
import org.rsmod.game.events.EventBus
import org.rsmod.plugins.api.model.event.ClientSession
import org.rsmod.plugins.profile.dispatch.TransactionDispatch
import javax.inject.Inject
import javax.inject.Singleton

private val logger = InlineLogger()

@Singleton
public class ClientDeregisterDispatch @Inject constructor(
    private val clientList: ClientList,
    private val eventBus: EventBus
) : TransactionDispatch<ClientDispatchRequest, ClientDeregisterResponse>() {

    internal fun serve(): Unit = super.serve(TRANSACTIONS_PER_SERVE)

    override fun serve(request: ClientDispatchRequest): ClientDeregisterResponse {
        logger.debug { "Serve client deregister response for request $request." }
        val client = request.client
        clientList -= client
        eventBus += ClientSession.Disconnect(client)
        return ClientDeregisterResponse.Success
    }

    private companion object {

        private const val TRANSACTIONS_PER_SERVE = 25
    }
}
