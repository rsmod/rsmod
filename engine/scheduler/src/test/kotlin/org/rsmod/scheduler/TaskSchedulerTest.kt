package org.rsmod.scheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("DeferredResultUnused")
class TaskSchedulerTest {
    private lateinit var scheduler: TaskScheduler

    @BeforeEach
    fun setUp() {
        val scope = TestScope(StandardTestDispatcher())
        scheduler = TaskScheduler(scope)
    }

    @AfterEach
    fun tearDown() {
        scheduler.clear()
    }

    @Test
    fun `schedule and execute tasks`() = runTest {
        var executed = 0
        scheduler.scheduleIO { executed++ }
        scheduler.scheduleIO { executed++ }
        assertEquals(0, executed)

        launchTasks()
        advanceUntilIdle()
        assertEquals(2, executed)
    }

    @Test
    fun `clear tasks`() = runTest {
        var executed = 0
        scheduler.scheduleIO { executed++ }
        assertEquals(1, scheduler.ioSchedule.size)
        scheduler.clear()
        launchTasks()
        assertEquals(0, executed)
    }

    @Test
    fun `throw uncaught exceptions on caller`() = runTest {
        scheduler.scheduleIO { /* no-op */ }
        supervisorScope {
            val deference = launchTasks()
            assertDoesNotThrow { deference.await() }
        }
        advanceUntilIdle()
        scheduler.clear()
        scheduler.scheduleIO { throw TaskException() }
        supervisorScope {
            val deference = launchTasks()
            assertThrows<TaskException> { deference.await() }
        }
        advanceUntilIdle()
    }

    @Test
    fun `execute tasks given same delay`() = runTest {
        var executed = 0
        val concurrent = 5
        // Coroutine tests' `advanceTimeBy` doesn't run tasks
        // scheduled at the _exact_ moment. We take that into
        // account in the coroutine delay.
        // @see [TestScope.advanceTimeBy]
        val delay = 100L
        repeat(concurrent) {
            scheduler.scheduleIO {
                delay(delay - 1)
                executed++
            }
        }
        launchTasks()
        assertEquals(0, executed)

        advanceTimeBy(delay)
        assertEquals(delay, currentTime)
        assertEquals(concurrent, executed)

        // Ensure no dangling tasks were left and caused test to falsely pass.
        advanceUntilIdle()
        assertEquals(concurrent, executed)
    }

    @Test
    fun `await tasks different delays`() = runTest {
        var executed = 0
        val delay0 = 100L
        val delay1 = 5000L
        val delay2 = 10000L
        scheduler.scheduleIO {
            delay(delay0 - 1)
            executed++
        }
        scheduler.scheduleIO {
            delay(delay1 - 1)
            executed++
        }
        scheduler.scheduleIO {
            delay(delay2 - 1)
            executed++
        }
        launchTasks()
        assertEquals(0, executed)

        advanceTimeBy(delay0)
        assertEquals(delay0, currentTime)
        assertEquals(1, executed)

        advanceTimeBy(delay1 - delay0)
        assertEquals(delay1, currentTime)
        assertEquals(2, executed)

        advanceTimeBy(delay2 - delay1)
        assertEquals(delay2, currentTime)
        assertEquals(3, executed)

        advanceUntilIdle()
        assertEquals(3, executed)
    }

    private fun CoroutineScope.launchTasks(): Deferred<Unit> =
        with(scheduler) { execute(ioSchedule) }

    private class TaskException : Exception()
}
