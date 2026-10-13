package org.runerealm.api.db.gateway

import jakarta.inject.Inject
import org.runerealm.api.db.gateway.model.GameDbRequest
import org.runerealm.api.db.gateway.model.GameDbResponse
import org.runerealm.api.db.gateway.service.ResponseDbGatewayService

public class GameDbManager
@Inject
constructor(private val responseGateway: ResponseDbGatewayService) {
    public fun <T> request(request: GameDbRequest<T>, response: GameDbResponse<T>) {
        responseGateway.request(request, response)
    }
}
