package org.rsmod.server.services

/**
 * A [Service] that participates in additional lifecycle signaling during startup and shutdown.
 *
 * Services implementing this interface are notified:
 * - After all services have completed their [Service.startup] phase.
 * - When the shutdown sequence has been initiated but before services are shut down.
 *
 * Both functions are invoked in coroutines launched by the **main application thread**.
 * Implementations may suspend to perform asynchronous work.
 *
 * _Note: Only services that require special coordination during startup or shutdown should
 * implement this interface._
 */
public interface ListenerService : Service {
    /**
     * Performs additional initialization after all services have completed their [Service.startup]
     * phase.
     *
     * This method is executed in a coroutine launched by the **main application thread** during
     * startup. The application will **block** until all listener services have completed their
     * signal routines.
     *
     * _Exceptions thrown during this phase should **not** be suppressed. They will be caught by the
     * [ServiceManager] and reported to the caller as part of the startup error flow._
     */
    public suspend fun signalStartup()

    /**
     * Performs expedited work during the early shutdown sequence.
     *
     * This method is executed in a coroutine launched by the **main application thread** during
     * shutdown, before [Service.shutdown] is invoked for services.
     *
     * _Exceptions thrown during this phase will be collected and reported by the [ServiceManager],
     * but will not prevent shutdown from proceeding._
     */
    public suspend fun signalShutdown()
}
