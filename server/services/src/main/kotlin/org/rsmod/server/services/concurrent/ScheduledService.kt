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
 * **Important:** [ScheduledService] is designed for single-threaded use. The [run] method is
 * invoked exactly once, on a coroutine bound to the thread returned by [createExecutor]. If your
 * service requires a thread pool or the ability to handle multiple concurrent tasks, consider using
 * a standard [Service] implementation and managing your executor directly.
 *
 * The [createExecutor] method **must return a new instance** on each call, and must not return a
 * cached or shared executor. The returned executor is owned by the system and will be shut down
 * when the service is stopped.
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
     * This executor is used to run both [setup] and the coroutine that repeatedly invokes [run].
     * The returned executor should typically be single-threaded, as only one coroutine is launched
     * and expected to remain active until shutdown.
     *
     * Avoid returning a shared or cached executor. The returned executor will be shut down
     * automatically when the service is stopped.
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
