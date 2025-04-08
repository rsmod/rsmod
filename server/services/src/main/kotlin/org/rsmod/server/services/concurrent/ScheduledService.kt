package org.rsmod.server.services.concurrent

import java.util.concurrent.ExecutorService
import org.rsmod.server.services.Service

/**
 * A long-lived service that runs scheduled work on a dedicated thread, managed by the
 * [org.rsmod.server.services.ServiceManager].
 *
 * This service defines three lifecycle phases:
 * - [startup] and [shutdown]: called on the **main application thread**.
 * - [setup]: invoked once on the **executor thread**, before the run loop begins.
 * - [run]: repeatedly called on the **executor thread** until shutdown.
 *
 * _Note: [setup] runs once before the first [run] call. This is useful for initializing resources
 * that must exist on the same thread as [run]._
 *
 * The [run] function is expected to include cooperative suspension points (e.g. `delay(...)`) to
 * avoid tight loops and to yield control appropriately. If an uncaught exception occurs during
 * [run], the error will be surfaced by the [org.rsmod.server.services.ServiceManager], triggering
 * application shutdown and propagating the error to the caller.
 *
 * Use [createExecutor] to define the thread context in which [run] is executed. While a
 * single-threaded executor is commonly used for deterministic or stateful logic, other types of
 * executors may be appropriate depending on the nature of the task.
 */
public interface ScheduledService : Service {
    /**
     * Called repeatedly until the application begins shutting down.
     *
     * This method is expected to suspend regularly (e.g., with `delay`) to avoid consuming cpu
     * resources unnecessarily.
     */
    public suspend fun run()

    /**
     * Creates and returns a new [ExecutorService] for running this service.
     *
     * This method **must return a new instance** each time it is called and must not return a
     * shared or cached executor. The returned executor will be used exclusively by the system to
     * invoke [run] and will be shut down automatically when the service is stopped.
     *
     * Avoid caching or reusing the executor within the [ScheduledService] implementation.
     */
    public fun createExecutor(): ExecutorService

    /**
     * Sets up one-time state before entering the [run] loop.
     *
     * This method runs once, on the executor created by [createExecutor], before the service enters
     * its scheduled [run] cycle.
     */
    public suspend fun setup()
}
