package org.rsmod.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameCoroutineTest {
    @Test
    fun `suspension state`() {
        val scope = TestCoroutineScope()
        val coroutine = GameCoroutine()
        assertTrue(coroutine.isIdle)

        scope.launch(coroutine) { /* no-op */ }
        assertTrue(coroutine.isIdle)

        scope.launch(coroutine) { pauseIndefinitely() }
        assertTrue(coroutine.isSuspended)
    }

    @Test
    fun `cancel suspension`() {
        val scope = TestCoroutineScope()
        val coroutine = GameCoroutine()
        var overstepped = false
        scope.launch(coroutine) {
            pauseIndefinitely()
            overstepped = true
        }
        assertTrue(coroutine.isSuspended)

        coroutine.cancel()
        assertTrue(coroutine.isIdle)
        assertFalse(overstepped)
    }

    @Test
    fun `stop suspension`() {
        val scope = TestCoroutineScope()
        val coroutine = GameCoroutine()

        val totalSteps = 10
        val stepsBeforeStop = totalSteps / 2
        var lastStepCount = 0
        scope.launch(coroutine) {
            repeat(totalSteps) {
                pause(Unit::class)
                if (++lastStepCount >= stepsBeforeStop) {
                    stop()
                }
            }
        }
        assertEquals(0, lastStepCount)

        for (i in 0 until stepsBeforeStop) {
            assertTrue(coroutine.isSuspended)
            coroutine.resumeWith(Unit)
            assertEquals(i + 1, lastStepCount)
        }
        assertTrue(coroutine.isIdle)
        assertEquals(stepsBeforeStop, lastStepCount)
    }

    @Test
    fun `predicate resume condition`() {
        val scope = TestCoroutineScope()
        val coroutine = GameCoroutine()

        var resume = false
        var completed = false
        scope.launch(coroutine) {
            pause { resume }
            completed = true
        }
        assertTrue(coroutine.isSuspended)
        assertFalse(completed)

        coroutine.advance()
        assertTrue(coroutine.isSuspended)

        resume = true
        coroutine.advance()
        assertTrue(coroutine.isIdle)
        assertTrue(completed)
    }

    @Test
    fun `deferred resume condition`() {
        val scope = TestCoroutineScope()
        val coroutine = GameCoroutine()

        var completed = false
        var deferred = 0
        val expectedValue = 5
        scope.launch(coroutine) {
            deferred = pause(Int::class)
            completed = true
        }
        assertTrue(coroutine.isSuspended)
        assertFalse(completed)

        coroutine.advance()
        assertTrue(coroutine.isSuspended)

        coroutine.resumeWith(true)
        assertTrue(coroutine.isSuspended)

        coroutine.resumeWith(expectedValue)
        assertEquals(expectedValue, deferred)
        assertTrue(coroutine.isIdle)
        assertTrue(completed)
    }

    @Test
    fun `back to back suspension points`() {
        val scope = TestCoroutineScope()
        val coroutine = GameCoroutine()

        var completed = false
        scope.launch(coroutine) {
            pause(Unit::class)
            pause(Int::class)
            pause(Boolean::class)
            completed = true
        }
        assertTrue(coroutine.isSuspended)
        assertFalse(completed)

        coroutine.resumeWith(Unit)
        assertTrue(coroutine.isSuspended)

        coroutine.resumeWith(0)
        assertTrue(coroutine.isSuspended)

        coroutine.resumeWith(true)
        assertTrue(coroutine.isIdle)
        assertTrue(completed)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `thread safety`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)
        val parentThread = Thread.currentThread()
        var coroutineThread: Thread? = null
        val parentName = parentThread.name
        var coroutineName: String? = null
        testCoroutineScope.launch {
            val scope = TestCoroutineScope()
            val coroutine = GameCoroutine()
            scope.launch(coroutine) {
                coroutineThread = Thread.currentThread()
                coroutineName = coroutineThread?.name
            }
            advanceUntilIdle()
        }
        advanceUntilIdle()
        assertSame(parentThread, coroutineThread)
        assertNotNull(coroutineName)
        assertNotEquals(parentName, coroutineName)
    }

    private suspend fun GameCoroutine.pauseIndefinitely() = pause { false }
}
