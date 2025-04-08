package org.rsmod.server.services

/**
 * A service that participates in the application's lifecycle, managed by [ServiceManager].
 *
 * The [startup] function is called before the application begins serving work. If any service fails
 * to start, the [ServiceManager] will shut down all services whose [startup] was invoked (even if
 * it failed) and propagate the error.
 *
 * The [shutdown] function is called during application termination or after a failed [startup]. Use
 * this to clean up any resources held or partially initialized by the service.
 *
 * Both functions may suspend to accommodate asynchronous work such as I/O, logging, or thread
 * coordination.
 */
public interface Service {
    /**
     * Initializes any resources required for the service.
     *
     * This method is executed in a coroutine launched by the **main application thread** during
     * startup. The application will **block** until all services have completed their startup
     * routines.
     *
     * _Exceptions thrown during this phase should **not** be suppressed. They will be caught by the
     * [ServiceManager] and reported to the caller as part of the startup error flow._
     */
    public suspend fun startup()

    /**
     * Cleans up any resources held by the service.
     *
     * This method is called during application shutdown or if [startup] fails partway through. It
     * will always be invoked if [startup] was called, regardless of whether it succeeded.
     *
     * _Exceptions thrown during shutdown will be collected and reported by the [ServiceManager],
     * but will not prevent other services from shutting down._
     */
    public suspend fun shutdown()
}
