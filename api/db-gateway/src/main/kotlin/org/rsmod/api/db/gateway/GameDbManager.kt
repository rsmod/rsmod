package org.rsmod.api.db.gateway

import jakarta.inject.Inject
import org.rsmod.api.db.gateway.model.GameDbRequest
import org.rsmod.api.db.gateway.model.GameDbResponse
import org.rsmod.api.db.gateway.service.ResponseDbGatewayService

public class GameDbManager
@Inject
constructor(private val responseGateway: ResponseDbGatewayService) {
    public fun <T> request(request: GameDbRequest<T>, response: GameDbResponse<T>) {
        responseGateway.request(request, response)
    }
}
