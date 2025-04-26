package org.rsmod.api.utils.system

import kotlin.concurrent.thread
import kotlin.system.exitProcess

public object SafeServiceExit {
    private const val TERMINATION_THREAD_NAME = "SystemReboot"

    /**
     * Safely terminates the server process by exiting through a dedicated daemon thread.
     *
     * Directly calling [exitProcess] from service-spawned threads (e.g., from the game service or
     * network service) can cause a circular wait during shutdown: the JVM shutdown sequence runs
     * shutdown hooks, which wait for services to finish shutting down. Because [exitProcess] blocks
     * the calling thread until shutdown hooks complete, a deadlock occurs.
     *
     * This method avoids that risk by spawning a new thread to call [exitProcess] independently,
     * ensuring that shutdown hooks can complete cleanly.
     *
     * This should be used whenever terminating the server from service threads.
     *
     * @see [exitProcess]
     */
    public fun terminate(status: Int = 0) {
        thread(name = TERMINATION_THREAD_NAME, isDaemon = true) { exitProcess(status) }
    }
}
