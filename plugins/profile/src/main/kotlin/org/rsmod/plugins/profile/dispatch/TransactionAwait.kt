package org.rsmod.plugins.profile.dispatch

import com.github.michaelbull.retry.RetryFailure
import com.github.michaelbull.retry.RetryInstruction
import com.github.michaelbull.retry.policy.binaryExponentialBackoff
import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.policy.plus
import com.github.michaelbull.retry.retry

private const val RETRY_ATTEMPTS = 10
private const val BACKOFF_BASE = 100L
private const val BACKOFF_MAX = 10000L

public suspend fun <L: DispatchRequest, T : DispatchResponse> DispatchTransaction<L, T>.await(
    retryPolicy: suspend RetryFailure<Throwable>.() -> RetryInstruction = defaultRetryPolicy()
): T = retry(retryPolicy) {
    if (!ready.get()) throw NotReady
    response
}

private fun defaultRetryPolicy(): suspend RetryFailure<Throwable>.() -> RetryInstruction {
    return limitAttempts(RETRY_ATTEMPTS) +
        binaryExponentialBackoff(BACKOFF_BASE, BACKOFF_MAX)
}

private sealed class RetryReason : Throwable()
private object NotReady : RetryReason()
