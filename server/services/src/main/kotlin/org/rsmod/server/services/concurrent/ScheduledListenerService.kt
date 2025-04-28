package org.rsmod.server.services.concurrent

/**
 * A [ScheduledService] that listens for application startup and shutdown signals.
 *
 * Services implementing this interface are notified:
 * - After all registered services have successfully completed their `startup` phase, allowing them
 *   to perform any setup that depends on the full application environment being available.
 * - When a shutdown sequence begins, allowing them to perform expedited work (e.g., fast-forwarding
 *   processing, persisting critical state) before the normal [shutdown] phase is invoked.
 *
 * This extension is intended for scheduled services that require additional setup after full
 * startup or special handling during shutdown preparation, without interrupting the normal service
 * lifecycle.
 *
 * _Note: Only services that require special startup or shutdown handling should implement this
 * interface._
 */
public interface ScheduledListenerService : ScheduledService {
    /**
     * Called after all registered services have successfully completed their `startup` phase.
     *
     * Implementations can use this method to perform setup work that depends on the full
     * application environment being available, such as accessing database connections or fully
     * initialized services.
     *
     * _Exceptions thrown during this phase should not be suppressed. They will be caught by the
     * `ServiceManager` and reported as part of the startup error flow, just like [startup]
     * failures._
     *
     * _This method is awaited with a timeout. If it fails or times out, startup will fail and
     * trigger service shutdown._
     *
     * _Note: `signalStartup` is invoked on the same coroutine dispatcher (thread) as [run] and
     * [setup]._
     */
    public suspend fun signalStartup()

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
