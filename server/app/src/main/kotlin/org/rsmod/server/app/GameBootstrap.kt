package org.rsmod.server.app

import jakarta.inject.Inject
import org.rsmod.server.services.Service
import org.rsmod.server.services.ServiceManager

class GameBootstrap @Inject constructor(services: Set<Service>) {
    private val serviceManager = ServiceManager.create(services)

    suspend fun startup() {
        val startupResult = serviceManager.awaitStartup()
        if (startupResult is ServiceManager.StartResult.Error) {
            throw startupResult.throwable
        }
        val runtime = Runtime.getRuntime()
        val shutdownHook = Thread(::shutdown, "ShutdownHook")
        runtime.addShutdownHook(shutdownHook)
        serviceManager.awaitShutdownOrThrow()
        try {
            runtime.removeShutdownHook(shutdownHook)
        } catch (_: IllegalStateException) {
            // Virtual machine is already in the process of shutting down - can safely noop.
        }
    }

    private fun shutdown() {
        serviceManager.shutdown()
        serviceManager.awaitShutdownOrThrow()
    }

    private fun ServiceManager.awaitShutdownOrThrow() {
        val result = awaitShutdown()
        if (result is ServiceManager.ShutdownResult.Report && result.errors.isNotEmpty()) {
            throw result.errors.first()
        }
    }
}
