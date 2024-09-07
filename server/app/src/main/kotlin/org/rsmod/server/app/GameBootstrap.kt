package org.rsmod.server.app

import com.google.common.util.concurrent.Service
import com.google.common.util.concurrent.ServiceManager
import jakarta.inject.Inject

class GameBootstrap @Inject constructor(services: Set<Service>) {
    private val serviceManager = ServiceManager(services)

    fun startUp() {
        serviceManager.startAsync()
        try {
            serviceManager.awaitHealthy()
        } catch (t: Throwable) {
            serviceManager.stopAsync().awaitStopped()
            throw t.unwrapCause()
        }
        val shutdown = Thread(::shutdown, "ShutdownHook")
        Runtime.getRuntime().addShutdownHook(shutdown)
        serviceManager.awaitStopped()
        Runtime.getRuntime().removeShutdownHook(shutdown)
    }

    private fun shutdown() {
        serviceManager.stopAsync().awaitStopped()
    }

    private fun Throwable.unwrapCause(): Throwable =
        suppressedExceptions.firstOrNull()?.cause ?: this
}
