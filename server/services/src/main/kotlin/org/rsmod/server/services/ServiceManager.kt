package org.rsmod.server.services

import java.util.Queue
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeout
import org.rsmod.server.services.concurrent.ScheduledListenerService
import org.rsmod.server.services.concurrent.ScheduledService

/**
 * Manages the lifecycle of a collection of [Service] and [ScheduledService] instances.
 *
 * [ServiceManager] is responsible for orchestrating the startup, scheduling, and graceful shutdown
 * of all registered services. It provides error handling, service failure propagation, and resource
 * cleanup guarantees during shutdown.
 *
 * Services are started in parallel when [awaitStartup] is invoked. If any service fails during
 * startup, all previously initialized services will be shut down in parallel, and the error will be
 * returned to the caller.
 *
 * Scheduled services implement [ScheduledService] and are scheduled on dedicated [ExecutorService]
 * instances returned by each implementation's [ScheduledService.createExecutor]. These executors
 * are owned and shut down by the manager.
 *
 * To trigger shutdown, call [shutdown], which will unblock [awaitShutdown] and begin shutting down
 * all services. Errors during scheduled service execution are automatically caught and will also
 * initiate the shutdown of all services.
 *
 * @see [Service]
 * @see [ScheduledService]
 */
public class ServiceManager
private constructor(
    private val services: List<Service>,
    private val scheduled: List<ScheduledService>,
    private val listeners: List<ScheduledListenerService>,
) {
    private val activeExecutors = mutableListOf<ExecutorService>()
    private val activeScopes = mutableMapOf<ScheduledService, CoroutineScope>()
    private val scheduledErrors = ConcurrentLinkedQueue<Throwable>()

    private val shutdownLatch = CountDownLatch(1)
    private val shutdownRequest = AtomicBoolean(false)
    private val shutdownInProgress = AtomicBoolean(false)

    /**
     * Starts all registered [Service] and [ScheduledService] instances.
     *
     * This method blocks the current thread until all services have either:
     * - Successfully started, in which case it returns [StartResult.Healthy], or
     * - Encountered an error during startup, in which case it returns an appropriate
     *   [StartResult.Error].
     *
     * Services are started concurrently using coroutines. If any service throws during its
     * [Service.startup] call, all services whose [Service.startup] was invoked will have their
     * [Service.shutdown] method called to ensure proper cleanup. Any failure in the cleanup step
     * will be captured and reported as part of [StartResult.Error.Lingering].
     *
     * If all services start successfully, [ScheduledService] instances will be scheduled on their
     * dedicated [ExecutorService] instances as returned by [ScheduledService.createExecutor]. These
     * executors are managed and shut down automatically by the manager.
     *
     * @return [StartResult] indicating whether startup completed successfully or failed.
     */
    public fun awaitStartup(): StartResult = runBlocking {
        val started = ConcurrentLinkedQueue<Service>()

        val startupJobs = supervisorScope { services.map { startService(it, started) } }
        try {
            startupJobs.awaitAll()
        } catch (t: Throwable) {
            val startedCopy = started.toList()
            val shutdownJobs = supervisorScope { startedCopy.map { stopService(it, started) } }
            val shutdownResult =
                try {
                    shutdownJobs.awaitAll()
                    StartResult.Error.Clean(t)
                } catch (shutdownEx: Throwable) {
                    StartResult.Error.Lingering(t, shutdownEx, started)
                }
            return@runBlocking shutdownResult
        }

        val executors = mutableListOf<ExecutorService>()
        try {
            for (service in scheduled) {
                val executor = service.createExecutor()
                scheduleService(service, executor)
                executors += executor
            }
            activeExecutors += executors
        } catch (t: Throwable) {
            executors.forEach(::safeShutdown)
            return@runBlocking StartResult.Error.CouldNotSchedule(t)
        }

        StartResult.Healthy
    }

    /**
     * Blocks the current thread until a shutdown signal is received via [shutdown].
     *
     * Once triggered, this method will:
     * - Cancel all active [ScheduledService] coroutine jobs.
     * - Shut down all [ScheduledService] executors.
     * - Call [Service.shutdown] on all registered services concurrently.
     *
     * If any service throws an exception during its shutdown call, the error will be captured and
     * returned as part of the resulting [ShutdownResult.Report].
     *
     * _This method may only be executed once. Subsequent calls will return
     * [ShutdownResult.AlreadyShutDown]._
     *
     * @param signalTimeoutSecs the maximum time to wait for active [ScheduledListenerService] to
     *   acknowledge a shutdown signal. This phase allows services to perform immediate preparations
     *   (e.g., a game service fast-forwarding player processing before disconnecting players,
     *   allowing a player-saving service to persist their data).
     * @param cleanupTimeoutSecs the maximum time to wait for active [ScheduledService] coroutine
     *   jobs to be canceled and their executors to be shut down.
     * @param shutdownTimeoutSecs the maximum time to wait for all registered services to complete
     *   their [Service.shutdown] logic.
     * @return [ShutdownResult] representing the outcome of the shutdown process.
     */
    public fun awaitShutdown(
        signalTimeoutSecs: Int = 30,
        cleanupTimeoutSecs: Int = 30,
        shutdownTimeoutSecs: Int = 30,
    ): ShutdownResult {
        shutdownLatch.await()

        val canShutdown = shutdownInProgress.compareAndSet(false, true)
        if (!canShutdown) {
            return ShutdownResult.AlreadyShutDown
        }

        val signalTimeout = signalTimeoutSecs * 1000L
        signalServices(signalTimeout)

        val cleanupTimeout = cleanupTimeoutSecs * 1000L
        cleanupThreads(cleanupTimeout)

        val shutdownTimeout = shutdownTimeoutSecs * 1000L
        shutdownServices(shutdownTimeout)

        val errors = scheduledErrors
        return if (errors.isNotEmpty()) {
            ShutdownResult.Report(errors)
        } else {
            ShutdownResult.Clean
        }
    }

    /**
     * Initiates the shutdown process for all services.
     *
     * This method signals the manager to begin shutting down. It unblocks any thread waiting on
     * [awaitShutdown] and triggers the cleanup process, which includes:
     * - Canceling active coroutine jobs.
     * - Shutting down executor services.
     * - Shutting down all registered services.
     *
     * If shutdown has already been requested, subsequent calls to this method will no-op.
     *
     * This method is safe to call multiple times, but shutdown will only be performed once.
     */
    public fun shutdown() {
        val requestShutdown = shutdownRequest.compareAndSet(false, true)
        if (requestShutdown) {
            shutdownLatch.countDown()
        }
    }

    private fun CoroutineScope.startService(service: Service, started: Queue<Service>) = async {
        started.add(service)
        service.startup()
    }

    private fun CoroutineScope.stopService(service: Service, started: Queue<Service>) = async {
        service.shutdown()
        started.remove(service)
    }

    private fun scheduleService(service: ScheduledService, executor: ExecutorService) {
        val coroutineScope = CoroutineScope(SupervisorJob() + executor.asCoroutineDispatcher())
        activeScopes[service] = coroutineScope
        coroutineScope.launch {
            try {
                service.setup()
                while (isActive && !shutdownRequest.get()) {
                    service.run()
                }
            } catch (_: CleanupException) {
                // Noop - This is a controlled shutdown signal.
            } catch (t: Throwable) {
                scheduledErrors += t
                shutdown()
            }
        }
    }

    private fun signalServices(timeoutMillis: Long) = runBlocking {
        try {
            withTimeout(timeoutMillis) {
                supervisorScope {
                    try {
                        val signalJobs =
                            listeners.map { service ->
                                val serviceScope = activeScopes.getValue(service)
                                serviceScope.async { service.signalShutdown() }
                            }
                        signalJobs.awaitAll()
                    } catch (t: Throwable) {
                        scheduledErrors += t
                    }
                }
            }
        } catch (t: TimeoutCancellationException) {
            scheduledErrors += t
        }
    }

    private fun cleanupThreads(timeoutMillis: Long) = runBlocking {
        val activeScopes = activeScopes.values
        val activeJobs = activeScopes.mapNotNull { it.coroutineContext[Job] }.filter(Job::isActive)
        activeJobs.forEach(::safeCancel)
        activeExecutors.forEach(::safeShutdown)
        try {
            withTimeout(timeoutMillis) { activeJobs.joinAll() }
        } catch (t: TimeoutCancellationException) {
            scheduledErrors += t
        }
    }

    private fun shutdownServices(timeoutMillis: Long) = runBlocking {
        try {
            withTimeout(timeoutMillis) {
                supervisorScope {
                    try {
                        val shutdownJobs = services.map { service -> async { service.shutdown() } }
                        shutdownJobs.awaitAll()
                    } catch (t: Throwable) {
                        scheduledErrors += t
                    }
                }
            }
        } catch (t: TimeoutCancellationException) {
            scheduledErrors += t
        }
    }

    private fun safeCancel(job: Job) {
        job.cancel(CleanupException())
    }

    private fun safeShutdown(executor: ExecutorService, timeoutSecs: Long = 15L) {
        executor.shutdown()
        try {
            if (!executor.awaitTermination(timeoutSecs, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (_: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    public sealed class StartResult {
        public data object Healthy : StartResult()

        public sealed class Error : StartResult() {
            public abstract val throwable: Throwable

            public data class Clean(override val throwable: Throwable) : Error()

            public data class Lingering(
                override val throwable: Throwable,
                val shutdownEx: Throwable,
                val pending: Collection<Service>,
            ) : Error()

            public data class CouldNotSchedule(override val throwable: Throwable) : Error()
        }
    }

    public sealed class ShutdownResult {
        public data object Clean : ShutdownResult()

        public data object AlreadyShutDown : ShutdownResult()

        public data class Report(val errors: Collection<Throwable>) : ShutdownResult()
    }

    private class CleanupException : CancellationException("Coroutine cancelled by ServiceManager")

    public companion object {
        /**
         * Creates a new [ServiceManager] with the provided set of [Service] instances.
         *
         * @param services the complete set of services to manage. May include both regular and
         *   scheduled services.
         * @return a configured [ServiceManager] instance ready for startup and shutdown
         *   orchestration.
         */
        public fun create(services: Set<Service>): ServiceManager {
            val scheduled = services.filterIsInstance<ScheduledService>()
            val listeners = services.filterIsInstance<ScheduledListenerService>()
            return ServiceManager(services.toList(), scheduled, listeners)
        }
    }
}
