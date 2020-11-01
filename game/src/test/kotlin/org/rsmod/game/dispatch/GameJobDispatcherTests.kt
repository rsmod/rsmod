package org.rsmod.game.dispatch

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameJobDispatcherTests {

    @Test
    fun `add continuous game job`() {
        val dispatcher = GameJobDispatcher()
        dispatcher.schedule {}
        Assertions.assertEquals(1, dispatcher.size)
    }

    @Test
    fun `add one-time game job`() {
        val dispatcher = GameJobDispatcher()
        dispatcher.execute {}
        Assertions.assertEquals(1, dispatcher.size)
    }

    @Test
    fun `keep continuous game job after executing all jobs`() {
        val dispatcher = GameJobDispatcher()
        dispatcher.schedule {}
        Assertions.assertEquals(1, dispatcher.size)

        dispatcher.executeAll()
        Assertions.assertEquals(1, dispatcher.size)
    }

    @Test
    fun `discard one-time game job after executing all jobs`() {
        val dispatcher = GameJobDispatcher()
        dispatcher.execute {}
        Assertions.assertEquals(1, dispatcher.size)

        dispatcher.executeAll()
        Assertions.assertEquals(0, dispatcher.size)
    }
}
