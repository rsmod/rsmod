package org.rsmod.server.services.concurrent

/**
 * A [ScheduledService] that listens for an early shutdown signal.
 *
 * Services implementing this interface are notified when a shutdown sequence begins, allowing them
 * to perform expedited work (e.g., fast-forwarding processing, persisting critical state) before
 * the normal [shutdown] phase is invoked.
 *
 * This extension is intended for scheduled services that need to finalize or adjust state ahead of
 * cancellation, without interrupting the normal service lifecycle.
 *
 * _Note: Only services that require special handling during shutdown preparation should implement
 * this interface._
 */
public interface ScheduledListenerService : ScheduledService {
    /**
     * Called when the shutdown sequence has been initiated, but before services are canceled or
     * shut down.
     *
     * Implementations can use this method to perform finalization or expedited processing that must
     * complete before shutdown proceeds.
     *
     * _This method is awaited with a timeout. If the operation fails to complete in time, shutdown
     * will continue and the failure will be reported._
     *
     * _Note: `signalShutdown` is invoked on the same coroutine dispatcher (thread) as [run] and
     * [setup]. Implementations can assume that no concurrent mutation from [run] will occur during
     * execution._
     */
    public suspend fun signalShutdown()
}
