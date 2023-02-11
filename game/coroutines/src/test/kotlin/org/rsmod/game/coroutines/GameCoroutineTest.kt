package org.rsmod.game.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GameCoroutineTest {

    @Test
    fun testCoroutineSuspension() {
        val scope = GameCoroutineScope()
        val coroutine = GameCoroutine("test-coroutine")
        Assertions.assertTrue(coroutine.isIdle)

        scope.launch(coroutine) {}
        Assertions.assertTrue(coroutine.isIdle)

        scope.launch(coroutine) {
            pause { false }
        }
        Assertions.assertTrue(coroutine.isSuspended)
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
        Assertions.assertTrue(coroutine.isSuspended)

        coroutine.cancel()
        Assertions.assertTrue(coroutine.isIdle)
        Assertions.assertFalse(oversteppedCoroutine)
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
        Assertions.assertEquals(0, stepped)
        Assertions.assertTrue(coroutine.isSuspended)

        for (i in 0 until validSteps) {
            coroutine.resume()
            Assertions.assertEquals(i + 1, stepped)
        }
        for (i in validSteps until totalSteps) {
            coroutine.resume()
            Assertions.assertNotEquals(i + 1, stepped)
        }
        Assertions.assertEquals(validSteps, stepped)
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
        Assertions.assertTrue(coroutine.isSuspended)
        Assertions.assertFalse(completed)

        repeat(ticks - 1) {
            coroutine.resume()
        }
        Assertions.assertTrue(coroutine.isSuspended)

        coroutine.resume()
        Assertions.assertTrue(coroutine.isIdle)
        Assertions.assertTrue(completed)
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
        Assertions.assertTrue(coroutine.isSuspended)
        Assertions.assertFalse(completed)

        coroutine.resume()
        Assertions.assertTrue(coroutine.isSuspended)

        resume = true
        coroutine.resume()
        Assertions.assertFalse(coroutine.isSuspended)
        Assertions.assertTrue(completed)
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
        Assertions.assertTrue(coroutine.isSuspended)
        Assertions.assertFalse(completed)

        /* stays suspended until proper value is submitted to `resumeWith` */
        coroutine.resume()
        Assertions.assertTrue(coroutine.isSuspended)

        /* coroutine will not accept value type it did not ask for */
        coroutine.resumeWith(true)
        Assertions.assertTrue(coroutine.isSuspended)

        coroutine.resumeWith(expectedValue)
        Assertions.assertEquals(expectedValue, deferred)
        Assertions.assertTrue(coroutine.isIdle)
        Assertions.assertTrue(completed)
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
        Assertions.assertTrue(coroutine.isSuspended)
        Assertions.assertFalse(completed)

        coroutine.resume()
        Assertions.assertTrue(coroutine.isSuspended)
        coroutine.resumeWith(0)
        Assertions.assertTrue(coroutine.isSuspended)

        coroutine.resume()
        Assertions.assertTrue(coroutine.isIdle)
        Assertions.assertTrue(completed)
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
        Assertions.assertSame(parentThread, coroutineThread)
        Assertions.assertNotNull(coroutineName)
        Assertions.assertNotEquals(parentName, coroutineName)
    }
}
