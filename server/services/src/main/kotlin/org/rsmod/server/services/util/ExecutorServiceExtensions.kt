package org.rsmod.server.services.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

public fun ExecutorService.safeShutdown(timeoutSecs: Long = 15) {
    shutdown()
    try {
        if (!awaitTermination(timeoutSecs, TimeUnit.SECONDS)) {
            shutdownNow()
        }
    } catch (_: InterruptedException) {
        shutdownNow()
        Thread.currentThread().interrupt()
    }
}
