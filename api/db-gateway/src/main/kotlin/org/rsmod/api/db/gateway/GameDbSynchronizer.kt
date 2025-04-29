package org.rsmod.api.db.gateway

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
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

    /**
     * Blocks until all database gateway services have completed their fast-forward shutdown or
     * until a watchdog timeout is reached.
     *
     * This function must be invoked before the game begins its fast-forwarded tick loop during
     * pre-shutdown. It ensures that all pending database operations are either processed or
     * explicitly rejected, and that any required in-game responses are safely enqueued while the
     * game thread is still active and able to process them.
     *
     * Each service is expected to handle its own shutdown timeout to avoid indefinite hangs. This
     * method additionally applies a long-duration watchdog timeout as a safeguard against
     * misbehavior or oversight.
     *
     * This method must be called exactly once during shutdown. Failure to invoke it before shutdown
     * ticks may result in lost or orphaned database responses.
     *
     * @see [ResponseDbGatewayService.fastForwardShutdown]
     */
    public fun blockingFastForwardShutdown(): Unit = runBlocking {
        val timeout = TimeUnit.MINUTES.toMillis(WATCHDOG_TIMEOUT_MINS.toLong())
        val success = withTimeoutOrNull(timeout) { gateway.fastForwardShutdown() }
        if (success == null) {
            logger.error {
                "Shutdown watchdog timeout exceeded ($WATCHDOG_TIMEOUT_MINS minutes). " +
                    "Some database gateways may not have completed shutdown properly."
            }
        }
    }

    private companion object {
        private const val WATCHDOG_TIMEOUT_MINS = 2
        private val logger = InlineLogger()
    }
}
