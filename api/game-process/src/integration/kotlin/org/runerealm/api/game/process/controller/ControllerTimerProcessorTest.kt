package org.runerealm.api.game.process.controller

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.runerealm.api.controller.access.StandardConAccess
import org.runerealm.api.script.onConTimer
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.factory.controllerTypeFactory
import org.runerealm.api.testing.factory.timerTypeFactory
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class ControllerTimerProcessorTest {
    @Test
    fun GameTestState.`run two timers with delay after first`() =
        runInjectedGameTest(TimerTestState::class, TimerTestModule, DualTimerTestScript::class) {
            val con = spawnController(CoordGrid(0, 50, 50, 0, 0), CONTROLLER)

            con.timer(TIMER_1, cycles = 1)
            con.timer(TIMER_2, cycles = 1)

            advance(ticks = 1)
            check(con.isDelayed) { "Con should be delayed from `TIMER_1`: $con" }
            assertTrue(it.launchedTimer1)
            assertFalse(it.launchedTimer2)

            // Remove timer, otherwise it will re-run the following cycle.
            con.clearTimer(TIMER_1)
            advance(ticks = 1)
            check(con.isNotDelayed) { "Con should no longer be delayed from `TIMER_1`: $con" }
            assertTrue(it.launchedTimer2)
        }

    private class DualTimerTestScript @Inject constructor(private val state: TimerTestState) :
        PluginScript() {
        override fun ScriptContext.startup() {
            onConTimer(CONTROLLER, TIMER_1) { timer1() }
            onConTimer(CONTROLLER, TIMER_2) { timer2() }
        }

        private suspend fun StandardConAccess.timer1() {
            state.launchedTimer1 = true
            delay(1)
        }

        private fun timer2() {
            state.launchedTimer2 = true
        }
    }

    private object TimerTestModule : AbstractModule() {
        override fun configure() {
            bind(TimerTestState::class.java).`in`(Scopes.SINGLETON)
        }
    }

    private data class TimerTestState(
        var launchedTimer1: Boolean = false,
        var launchedTimer2: Boolean = false,
    )

    private companion object {
        val CONTROLLER = controllerTypeFactory.create(id = 1)
        val TIMER_1 = timerTypeFactory.create(id = 1)
        val TIMER_2 = timerTypeFactory.create(id = 2)
    }
}
