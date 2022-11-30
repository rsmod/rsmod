package org.rsmod.game.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

@ExperimentalCoroutinesApi
class GameCoroutineContextTest {

    @Test
    fun `verify GameCoroutineContextElement is set for pause functions`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)
        assertThrows<IllegalStateException> { pause(ticks = 1) }
        assertThrows<IllegalStateException> { pause(condition = { true }) }
        assertThrows<IllegalStateException> { pause(type = Int::class) }
        assertDoesNotThrow {
            testCoroutineScope.launch(GameCoroutineContextElement()) {
                pause(ticks = 1)
            }
        }
        assertDoesNotThrow {
            testCoroutineScope.launch(GameCoroutineContextElement()) {
                pause(condition = { true })
            }
        }
        assertDoesNotThrow {
            testCoroutineScope.launch(GameCoroutineContextElement()) {
                pause(type = Int::class)
            }
        }
    }

    @Test
    fun `verify GameCoroutineContextElement is set for stop function`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)
        assertThrows<IllegalStateException> { stop() }
        assertDoesNotThrow {
            testCoroutineScope.launch(GameCoroutineContextElement()) {
                stop()
            }
        }
    }
}
