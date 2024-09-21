package org.rsmod.server.shared.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal inline fun <T> ExecutorService.use(
    timeoutSecs: Int = 30,
    block: (ExecutorService) -> T,
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    try {
        return block(this)
    } finally {
        shutdown()
        try {
            if (!awaitTermination(timeoutSecs.toLong(), TimeUnit.SECONDS)) {
                shutdownNow()
            }
        } catch (_: InterruptedException) {
            shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}
