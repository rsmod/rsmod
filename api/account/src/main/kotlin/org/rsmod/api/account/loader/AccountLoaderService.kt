package org.rsmod.api.account.loader

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import org.rsmod.api.account.character.CharacterDataStage
import org.rsmod.api.account.character.CharacterMetadataList
import org.rsmod.api.account.character.main.CharacterAccountRepository
import org.rsmod.api.account.loader.request.AccountLoadRequest
import org.rsmod.api.account.loader.request.AccountLoadResponse
import org.rsmod.api.db.DatabaseConnection
import org.rsmod.api.db.sqlite.SqliteDatabase
import org.rsmod.server.services.concurrent.ScheduledService

public class AccountLoaderService
@Inject
constructor(
    private val database: SqliteDatabase,
    private val repository: CharacterAccountRepository,
    private val pipelines: Set<CharacterDataStage.Pipeline>,
) : ScheduledService {
    private val logger = InlineLogger()

    private val shuttingDown = AtomicBoolean(false)

    private val pendingRequests = ConcurrentLinkedQueue<AccountLoadRequest>()
    private val activeRequests = AtomicInteger(0)

    private val pendingBatch = ArrayList<AccountLoadRequest>(REQUESTS_PER_BATCH)
    private var consecutiveFailureCount = AtomicInteger(0)

    /**
     * Attempts to add the given [request] to the service queue.
     *
     * The request will only be queued if the current number of active account requests is below
     * [MAX_REQUEST_SOFT_CAP]. Returns `false` if the limit has been reached, and the request is not
     * queued.
     */
    public fun queue(request: AccountLoadRequest): Boolean {
        if (activeRequests.get() >= MAX_REQUEST_SOFT_CAP) {
            return false
        }
        pendingRequests.add(request)
        activeRequests.incrementAndGet()
        return true
    }

    /**
     * Returns `true` if the service is in the process of shutting down.
     *
     * During shutdown, no new requests should be queued, and any remaining queued requests will be
     * rejected.
     */
    public fun isShuttingDown(): Boolean = shuttingDown.get()

    /**
     * Returns `true` if the service is temporarily rejecting new requests due to consecutive
     * failures.
     *
     * This occurs when [MAX_CONSECUTIVE_FAILURES] has been reached and there are still pending
     * requests in the queue. Once a batch successfully processes, the failure count resets and
     * normal request intake resumes.
     *
     * _Note: This assumes the function is called consistently (e.g., before [queue]) to allow the
     * failure state to self-recover. The `pendingRequests.isNotEmpty` condition ensures that a
     * single request is always allowed through to attempt service recovery._
     */
    public fun isTemporarilyRejectingRequests(): Boolean =
        consecutiveFailureCount.get() >= MAX_CONSECUTIVE_FAILURES && pendingRequests.isNotEmpty()

    override suspend fun run() {
        val failureBackoffEnabled = consecutiveFailureCount.get() >= MAX_CONSECUTIVE_FAILURES
        val batchPollCount = if (failureBackoffEnabled) 1 else REQUESTS_PER_BATCH

        batchPendingRequests(batchPollCount)
        handlePendingBatch()
        correctActiveRequests()

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
            activeRequests.decrementAndGet()
            pendingBatch += request
        }
    }

    private suspend fun handlePendingBatch() {
        if (pendingBatch.isNotEmpty()) {
            try {
                handleRequests(pendingBatch)
                resetConsecutiveFailures()
                pendingBatch.clear()
            } catch (e: Exception) {
                val size = pendingBatch.size
                incrementConsecutiveFailures()
                rejectPendingBatch()
                logger.error(e) { "Error processing account batch. (size=$size)" }
            }
        }
    }

    private fun correctActiveRequests() {
        // Reset `activeRequests` when no requests remain. As a soft cap, some drift is acceptable,
        // but this helps correct any accumulated discrepancy over time.
        if (pendingRequests.isEmpty()) {
            activeRequests.set(0)
        }
    }

    override fun createExecutor(): ExecutorService {
        val threadFactory = ThreadFactory { runnable ->
            Thread(runnable, SERVICE_THREAD_NAME).apply { isDaemon = false }
        }
        return Executors.newSingleThreadExecutor(threadFactory)
    }

    override suspend fun setup() {
        logger.debug {
            "Account loader service accepting $REQUESTS_PER_BATCH requests / $DELAY_PER_BATCH ms."
        }
    }

    override suspend fun startup() {}

    override suspend fun shutdown() {
        logger.info { "Attempting to shutdown account loader service." }
        try {
            shuttingDown.set(true)
            rejectPendingRequests()
            activeRequests.set(0)
            pendingBatch.clear()
            logger.info { "Account loader service successfully shut down." }
        } catch (t: Throwable) {
            logger.error(t) { "Account loader service failed to shut down." }
        }
    }

    private fun rejectPendingRequests() {
        while (true) {
            val request = pendingRequests.poll() ?: break
            request.callback(AccountLoadResponse.Err.ShutdownInProgress)
        }
    }

    private suspend fun handleRequests(requests: List<AccountLoadRequest>) = supervisorScope {
        val readOnly = requests.filterIsInstance<AccountLoadRequest.ReadOnly>()
        val writeRequired = requests.filterIsInstance<AccountLoadRequest.WriteRequired>()

        if (readOnly.isNotEmpty()) {
            val timeout = REQUEST_READ_TIMEOUT_MS
            val readOnlyJobs =
                readOnly.map { request -> async { handleRequestWithTimeout(request, timeout) } }
            readOnlyJobs.awaitAll()
        }

        for (request in writeRequired) {
            handleRequestWithTimeout(request, REQUEST_WRITE_TIMEOUT_MS)
        }
    }

    private suspend fun handleRequestWithTimeout(request: AccountLoadRequest, timeoutMillis: Long) {
        val result = withTimeoutOrNull(timeoutMillis) { handleRequest(request) }
        if (result == null) {
            logger.warn { "Account load timed out for: '${request.loginName}' ($request)" }
            request.callback(AccountLoadResponse.Err.Timeout)
        }
    }

    private suspend fun handleRequest(request: AccountLoadRequest) {
        val response = database.withTransaction { connection -> connection.handleRequest(request) }
        request.callback(response)
    }

    private fun DatabaseConnection.handleRequest(request: AccountLoadRequest): AccountLoadResponse {
        val metadataList = repository.selectAndCreateMetadataList(this, request.loginName)
        if (metadataList == null) {
            val response = accountNotFoundResponse(request)
            return response
        }
        for (pipeline in pipelines) {
            pipeline.append(this, metadataList)
        }
        val response =
            AccountLoadResponse.Ok.LoadAccount(request.auth, metadataList.accountData, metadataList)
        return response
    }

    private fun DatabaseConnection.accountNotFoundResponse(request: AccountLoadRequest) =
        when (request) {
            is AccountLoadRequest.StrictSearch -> AccountLoadResponse.Err.AccountNotFound
            is AccountLoadRequest.SearchOrCreateWithPassword -> createAccountResponse(request)
        }

    private fun DatabaseConnection.createAccountResponse(
        request: AccountLoadRequest.SearchOrCreateWithPassword
    ): AccountLoadResponse =
        try {
            val metadataList = createMetadataList(request.loginName, request.hashedPassword())
            AccountLoadResponse.Ok.NewAccount(request.auth, metadataList.accountData, metadataList)
        } catch (e: Exception) {
            AccountLoadResponse.Err.Exception(e)
        }

    private fun DatabaseConnection.createMetadataList(
        loginName: String,
        hashedPassword: String,
    ): CharacterMetadataList {
        val accountId = repository.insertOrSelectAccountId(this, loginName, hashedPassword)
        if (accountId == null) {
            throw IllegalStateException("Could not insert or select account id for: '$loginName'")
        }

        val characterId = repository.insertAndSelectCharacterId(this, accountId)
        if (characterId == null) {
            throw IllegalStateException("Could not insert character for: '$loginName' ($accountId)")
        }

        val metadataList = repository.selectAndCreateMetadataList(this, loginName)
        if (metadataList == null) {
            throw IllegalStateException("Could not select character after creation: '$loginName'")
        }
        return metadataList
    }

    /**
     * Rejects all requests in [pendingBatch] with a generic internal error.
     *
     * This is used in the rare case that the entire batch fails due to a system exception. It
     * ensures that these requests do not hang indefinitely without a callback.
     *
     * _Note: [pendingBatch] is cleared after sending the error response._
     */
    private fun rejectPendingBatch() {
        for (request in pendingBatch) {
            request.callback(AccountLoadResponse.Err.InternalServiceError)
        }
        pendingBatch.clear()
    }

    private fun incrementConsecutiveFailures() {
        consecutiveFailureCount.updateAndGet { curr -> min(curr + 1, MAX_CONSECUTIVE_FAILURES) }
    }

    private fun resetConsecutiveFailures() {
        val previous = consecutiveFailureCount.getAndSet(0)
        if (previous == 0) {
            return
        }
        logger.info { "Account loader service recovered after $previous failure(s)." }
    }

    private companion object {
        private const val SERVICE_THREAD_NAME = "account-reader"

        /**
         * Soft cap for the number of account load requests that can be queued at once. Requests
         * beyond this threshold will be rejected.
         */
        private const val MAX_REQUEST_SOFT_CAP = 2000

        /**
         * The time (in ms) to wait between each batch cycle when not under failure backoff. This
         * defines the baseline processing intervals.
         */
        private const val DELAY_PER_BATCH = 250L

        /** The maximum number of requests processed per service cycle ([run]). */
        private const val REQUESTS_PER_BATCH = 50

        /**
         * The timeout (in ms) for read-only requests. These are expected to complete quickly, and
         * timeouts help prevent stall buildup.
         */
        private const val REQUEST_READ_TIMEOUT_MS = 1000L

        /**
         * The timeout (in ms) for requests that involve database writes. These may be heavier, so a
         * longer timeout is allowed.
         */
        private const val REQUEST_WRITE_TIMEOUT_MS = 5000L

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
         * state persists until at least one batch is successfully processed.
         *
         * Callers of [queue] should check [isTemporarilyRejectingRequests] before queuing requests.
         * This ensures that only a single request is processed during backoff, allowing the service
         * to recover naturally once requests succeed again.
         */
        private const val MAX_CONSECUTIVE_FAILURES = 5
    }
}
