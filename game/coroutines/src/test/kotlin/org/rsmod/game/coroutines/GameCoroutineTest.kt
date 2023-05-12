package org.rsmod.game.coroutines

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
    fun testCoroutineSuspension() {
        val scope = GameCoroutineScope()
        val coroutine = GameCoroutine("test-coroutine")
        assertTrue(coroutine.isIdle)

        scope.launch(coroutine) {}
        assertTrue(coroutine.isIdle)

        scope.launch(coroutine) {
            pause { false }
        }
        assertTrue(coroutine.isSuspended)
    }

    @Test
    fun testCoroutineCancel() {
        val scope = GameCoroutineScope()
        val coroutine = GameCoroutine("test-coroutine")
        check(coroutine.isIdle)

        var oversteppedCoroutine = false
        scope.launch(coroutine) {
            pause { false }
            oversteppedCoroutine = true
        }
        assertTrue(coroutine.isSuspended)

        coroutine.cancel()
        assertTrue(coroutine.isIdle)
        assertFalse(oversteppedCoroutine)
    }

    @Test
    fun testCoroutineStop() {
        val scope = GameCoroutineScope()
        val coroutine = GameCoroutine("test-coroutine")
        check(coroutine.isIdle)

        val totalSteps = 10
        val validSteps = totalSteps / 2
        var stepped = 0
        scope.launch(coroutine) {
            for (i in 0 until totalSteps) {
                pause(ticks = 1)
                stepped++
                if (stepped >= validSteps) {
                    stop()
                }
            }
        }
        assertEquals(0, stepped)
        assertTrue(coroutine.isSuspended)

        for (i in 0 until validSteps) {
            coroutine.resume()
            assertEquals(i + 1, stepped)
        }
        for (i in validSteps until totalSteps) {
            coroutine.resume()
            assertNotEquals(i + 1, stepped)
        }
        assertEquals(validSteps, stepped)
    }

    @Test
    fun testCoroutineTickPause() {
        val scope = GameCoroutineScope()
        val coroutine = GameCoroutine("test-coroutine")
        check(coroutine.isIdle)

        val ticks = 10
        var completed = false
        scope.launch(coroutine) {
            pause(ticks)
            completed = true
        }
        assertTrue(coroutine.isSuspended)
        assertFalse(completed)

        repeat(ticks - 1) {
            coroutine.resume()
        }
        assertTrue(coroutine.isSuspended)

        coroutine.resume()
        assertTrue(coroutine.isIdle)
        assertTrue(completed)
    }

    @Test
    fun testCoroutinePredicatePause() {
        val scope = GameCoroutineScope()
        val coroutine = GameCoroutine("test-coroutine")
        check(coroutine.isIdle)

        var resume = false
        var completed = false
        scope.launch(coroutine) {
            pause { resume }
            completed = true
        }
        assertTrue(coroutine.isSuspended)
        assertFalse(completed)

        coroutine.resume()
        assertTrue(coroutine.isSuspended)

        resume = true
        coroutine.resume()
        assertFalse(coroutine.isSuspended)
        assertTrue(completed)
    }

    @Test
    fun testCoroutineDeferPause() {
        val scope = GameCoroutineScope()
        val coroutine = GameCoroutine("test-coroutine")
        check(coroutine.isIdle)

        var completed = false
        var deferred = 0
        val expectedValue = 5
        scope.launch(coroutine) {
            deferred = pause(Int::class)
            completed = true
        }
        assertTrue(coroutine.isSuspended)
        assertFalse(completed)

        /* stays suspended until proper value is submitted to `resumeWith` */
        coroutine.resume()
        assertTrue(coroutine.isSuspended)

        /* coroutine will not accept value type it did not ask for */
        coroutine.resumeWith(true)
        assertTrue(coroutine.isSuspended)

        coroutine.resumeWith(expectedValue)
        assertEquals(expectedValue, deferred)
        assertTrue(coroutine.isIdle)
        assertTrue(completed)
    }

    @Test
    fun testBackToBackSuspensionPoints() {
        val scope = GameCoroutineScope()
        val coroutine = GameCoroutine("test-coroutine")
        check(coroutine.isIdle)

        var completed = false
        scope.launch(coroutine) {
            pause(ticks = 1)
            pause(Int::class)
            pause(ticks = 1)
            completed = true
        }
        assertTrue(coroutine.isSuspended)
        assertFalse(completed)

        coroutine.resume()
        assertTrue(coroutine.isSuspended)
        coroutine.resumeWith(0)
        assertTrue(coroutine.isSuspended)

        coroutine.resume()
        assertTrue(coroutine.isIdle)
        assertTrue(completed)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun testCoroutineThreadSafe() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)
        val parentThread = Thread.currentThread()
        var coroutineThread: Thread? = null
        val parentName = parentThread.name
        var coroutineName: String? = null
        testCoroutineScope.launch {
            val scope = GameCoroutineScope()
            val coroutine = GameCoroutine("test-coroutine")
            check(coroutine.isIdle)

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
}
