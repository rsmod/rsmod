package org.rsmod.api.account.saver

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull
import org.rsmod.api.account.character.CharacterDataStage
import org.rsmod.api.account.character.main.CharacterAccountRepository
import org.rsmod.api.account.saver.request.AccountSaveRequest
import org.rsmod.api.account.saver.request.AccountSaveResponse
import org.rsmod.api.db.sqlite.SqliteDatabase
import org.rsmod.server.services.concurrent.ScheduledService

// This service is tailored for sqlite, where concurrent writes provide little to no benefit.
// Therefore, save operations are not parallelized and instead run on a single thread.
public class AccountSavingService
@Inject
constructor(
    private val database: SqliteDatabase,
    private val repository: CharacterAccountRepository,
    private val pipelines: Set<CharacterDataStage.Pipeline>,
) : ScheduledService {
    private val logger = InlineLogger()

    private val pendingRequests = ConcurrentLinkedQueue<AccountSaveRequest>()

    private val pendingBatch = ArrayList<AccountSaveRequest>(REQUESTS_PER_BATCH)
    private var consecutiveFailureCount = AtomicInteger(0)

    public fun queue(request: AccountSaveRequest) {
        pendingRequests.add(request)
    }

    override suspend fun run() {
        val failureBackoffEnabled = consecutiveFailureCount.get() >= MAX_CONSECUTIVE_FAILURES
        val batchPollCount = if (failureBackoffEnabled) 1 else REQUESTS_PER_BATCH

        batchPendingRequests(batchPollCount)
        handlePendingBatch()

        if (!failureBackoffEnabled) {
            delay(DELAY_PER_BATCH)
            return
        }

        val backoffDelay = ERROR_BACKOFF_MS
        logger.warn { "Too many consecutive failures, backing off for: ${backoffDelay}ms" }
        delay(backoffDelay)
    }

    private fun batchPendingRequests(count: Int) {
        for (i in 0 until count) {
            val request = pendingRequests.poll() ?: break
            pendingBatch += request
        }
    }

    private suspend fun handlePendingBatch() {
        for (request in pendingBatch) {
            handleRequestWithTimeout(request, REQUEST_TIMEOUT_MS)
        }
        pendingBatch.clear()
    }

    private suspend fun handleRequestWithTimeout(request: AccountSaveRequest, timeoutMillis: Long) {
        val result = withTimeoutOrNull(timeoutMillis) { handleRequest(request) }
        if (result == null) {
            logger.warn { "Account save timed out for: ${request.player}" }
            incrementSaveAttempts(request)
            incrementConsecutiveFailures()
        }
    }

    private suspend fun handleRequest(request: AccountSaveRequest) {
        try {
            saveSegments(request)
            invokeSaveCallback(request)
            resetConsecutiveFailures()
        } catch (e: Exception) {
            incrementSaveAttempts(request)
            incrementConsecutiveFailures()
            logger.warn(e) { "Could not save player account: ${request.player}" }
        }
    }

    private suspend fun saveSegments(request: AccountSaveRequest) {
        repository.save(database, request.player, request.characterId)
        for (pipeline in pipelines) {
            pipeline.save(database, request.player, request.characterId)
        }
    }

    private fun invokeSaveCallback(request: AccountSaveRequest) {
        safeInvokeCallback(request, AccountSaveResponse.Success(request.player))
    }

    override fun createExecutor(): ExecutorService {
        val threadFactory = ThreadFactory { runnable ->
            Thread(runnable, SERVICE_THREAD_NAME).apply { isDaemon = false }
        }
        return Executors.newSingleThreadExecutor(threadFactory)
    }

    override suspend fun setup() {
        logger.debug {
            "Account saving service accepting $REQUESTS_PER_BATCH requests / $DELAY_PER_BATCH ms."
        }
    }

    override suspend fun startup() {}

    override suspend fun shutdown() {
        logger.info { "Attempting to shutdown account saving service." }
        try {
            handleShutdownRequests()
            pendingBatch.clear()
            logger.info { "Account saving service successfully shut down." }
        } catch (t: Throwable) {
            logger.error(t) { "Account saving service failed to shut down." }
        }
    }

    private suspend fun handleShutdownRequests() {
        var saveCount = 0
        val result =
            withTimeoutOrNull(SHUTDOWN_TIMEOUT_MS) {
                while (isActive) {
                    val request = pendingRequests.poll() ?: break
                    handleRequestWithTimeout(request, SHUTDOWN_REQUEST_TIMEOUT_MS)
                    saveCount++
                }
            }

        if (saveCount > 0) {
            logger.info { "Saved $saveCount pending requests before shut down." }
        }

        // If `result` did not time out, we can return early. If it failed due to a timeout,
        // we switch to an "emergency mode" where we inform the remaining callbacks about
        // the internal server error so they can handle it appropriately.
        if (result != null) {
            return
        }

        logger.error { "Force-save failed due to timeout. Rejecting all pending requests..." }

        val leftover = pendingRequests.size
        val emergencyRejection =
            withTimeoutOrNull(SHUTDOWN_EMERGENCY_TIMEOUT_MS) { rejectPendingRequests() }

        if (emergencyRejection != null) {
            logger.info { "Sent out $leftover pending request max-retry responses." }
            return
        }

        val usernames = pendingRequests.take(100).joinToString(",") { it.player.username }
        logger.error {
            "Emergency shutdown measure timed out! " +
                "(${pendingRequests.size} pending requests, usernames=$usernames)"
        }
    }

    /** Rejects the remaining [pendingRequests], sending an internal server error response. */
    private fun rejectPendingRequests() {
        while (true) {
            val request = pendingRequests.poll() ?: break
            val response = AccountSaveResponse.InternalShutdownError(request.player)
            safeInvokeCallback(request, response)
        }
    }

    private fun incrementSaveAttempts(request: AccountSaveRequest) {
        if (request.attempts++ < REQUEST_RETRIES_BEFORE_FAILURE) {
            queue(request)
            return
        }
        safeInvokeCallback(request, AccountSaveResponse.ExcessiveRetries(request.player))
        logger.error { "Reached max retry attempts for player: ${request.player}" }
    }

    // Ideally, any errors would be handled within the callback itself. However, since this is
    // executed on the service thread, we add a safeguard to prevent one faulty callback from
    // affecting other pending requests.
    private fun safeInvokeCallback(request: AccountSaveRequest, response: AccountSaveResponse) {
        try {
            request.callback(response)
        } catch (e: Exception) {
            val message = "Error handling save request callback for: $request (response=$response)"
            logger.error(e) { message }
        }
    }

    private fun incrementConsecutiveFailures() {
        consecutiveFailureCount.updateAndGet { curr -> min(curr + 1, MAX_CONSECUTIVE_FAILURES) }
    }

    private fun resetConsecutiveFailures() {
        val previous = consecutiveFailureCount.getAndSet(0)
        if (previous == 0) {
            return
        }
        logger.info { "Account saving service recovered after $previous failure(s)." }
    }

    private companion object {
        private const val SERVICE_THREAD_NAME = "account-writer"

        /**
         * The time (in ms) to wait between each batch cycle when not under failure backoff. This
         * defines the baseline processing intervals.
         */
        private const val DELAY_PER_BATCH = 100L

        /** The maximum number of requests processed per service cycle ([run]). */
        private const val REQUESTS_PER_BATCH = 25

        /**
         * The number of times a request will retry before returning a failure response to the
         * callback. The caller is responsible for any backup or recovery strategy.
         *
         * This is compared against [AccountSaveRequest.attempts].
         */
        private const val REQUEST_RETRIES_BEFORE_FAILURE = 3

        /**
         * The timeout (in ms) before a request is considered failed and its attempt count
         * ([AccountSaveRequest.attempts]) is incremented.
         *
         * If the request still has remaining retries, it will be re-queued for another attempt.
         */
        private const val REQUEST_TIMEOUT_MS = 5000L

        /**
         * Similar to [REQUEST_TIMEOUT_MS], but used during the [shutdown] process when attempting
         * to hastily process the pending requests.
         */
        private const val SHUTDOWN_REQUEST_TIMEOUT_MS = 2000L

        /**
         * The delay duration (in ms) when backing off due to consecutive failures. Gives external
         * systems time to recover (e.g. Database under load).
         */
        private const val ERROR_BACKOFF_MS = 10_000L

        /**
         * The maximum number of consecutive batch failures before the service enters a backoff
         * period.
         *
         * During backoff, the service delays each cycle using [ERROR_BACKOFF_MS]. This failure
         * state persists until at least one request is successfully processed.
         */
        private const val MAX_CONSECUTIVE_FAILURES = 10

        /**
         * A safeguard timeout to prevent the application from waiting indefinitely during service
         * shutdown.
         *
         * While all pending requests are expected to eventually complete, or invoke their callback
         * with a "failed after X attempts" response, this timeout ensures the shutdown process does
         * not hang in case something goes wrong.
         */
        private const val SHUTDOWN_TIMEOUT_MS = 30_000L

        /**
         * A secondary safeguard timeout used after [SHUTDOWN_TIMEOUT_MS] during service shutdown.
         *
         * If any requests remain after the initial timeout, this ensures they forcibly invoke their
         * [AccountSaveResponse.InternalShutdownError] callback. This allows any external backup or
         * recovery strategies to proceed.
         */
        private const val SHUTDOWN_EMERGENCY_TIMEOUT_MS = 30_000L
    }
}
