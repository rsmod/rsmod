package org.rsmod.api.db.gateway

import jakarta.inject.Inject
import org.rsmod.api.db.gateway.service.ResponseDbGatewayService

public class GameDbSynchronizer @Inject constructor(private val gateway: ResponseDbGatewayService) {
    private val callbackBuffer = mutableListOf<ResponseDbGatewayService.PendingCallback<*>>()

    /**
     * Invokes up to [count] pending callbacks from the [ResponseDbGatewayService] on the caller
     * thread.
     *
     * _Note: This method should only be called by the game-thread._
     */
    public fun invokeCallbacks(count: Int) {
        gateway.take(callbackBuffer, count)
        for (callback in callbackBuffer) {
            callback.invoke()
        }
        callbackBuffer.clear()
    }
}
