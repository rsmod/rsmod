package org.rsmod.game.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.coroutines.cancellation.CancellationException

@ExperimentalCoroutinesApi
class GameCoroutineSuspendTest {

    @Test
    fun `non-suspended game coroutine should be idle`() = runTest {
        val contextElement = GameCoroutineContextElement()
        assert(contextElement.isIdle)
    }

    @Test
    fun `resume tick-suspended coroutine on tryResume`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)
        val context = GameCoroutineContextElement()
        testCoroutineScope.launch(context) { pause(ticks = 1) }
        advanceUntilIdle()
        assert(context.isSuspended) { "Coroutine should be suspended from pause call." }
        context.tryResume()
        assert(context.isIdle) { "Coroutine should resume after tryResume call." }
    }

    @Test
    fun `resume condition-suspended coroutine on tryResume`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)
        val context = GameCoroutineContextElement()
        testCoroutineScope.launch(context) { pause(condition = { true }) }
        advanceUntilIdle()
        assert(context.isSuspended) { "Coroutine should be suspended from pause call." }
        context.tryResume()
        assert(context.isIdle) { "Coroutine should resume after tryResume call." }
    }

    @Test
    fun `resume pubsub-suspended coroutine on respective type submit`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)
        val context = GameCoroutineContextElement()
        testCoroutineScope.launch(context) { pause(type = Int::class) }
        advanceUntilIdle()
        assert(context.isSuspended) { "Coroutine should be suspended from pause call." }
        context.tryResume()
        assert(context.isSuspended) { "Coroutine should stay suspended until a value is published." }
        context.submit(true)
        assert(context.isSuspended) { "Coroutine should stay suspended until correct value type is published." }
        context.submit(0)
        assert(context.isIdle) { "Coroutine should resume when correct value type is published." }
    }

    @Test
    fun `resume back-to-back suspension points`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)
        val context = GameCoroutineContextElement()
        testCoroutineScope.launch(context) {
            pause(ticks = 1)
            pause(condition = { true })
            pause(Int::class)
        }
        advanceUntilIdle()
        assert(context.isSuspended)
        context.tryResume()
        assert(context.isSuspended) { "Coroutine should be suspended from second pause call." }
        context.tryResume()
        assert(context.isSuspended) { "Coroutine should be suspended from third pause call." }
        context.submit(0)
        assert(context.isIdle) { "Coroutine should no longer be suspended." }
    }

    @Test
    fun `cancel call should halt further coroutine execution`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)
        val context = GameCoroutineContextElement()
        testCoroutineScope.launch(context) {
            pause(ticks = 1)
            pause(condition = { true })
            pause(ticks = 1)
        }
        advanceUntilIdle()
        assert(context.isSuspended)
        assertThrows<CancellationException> { context.cancel() }
        assert(context.isIdle)
    }
}
