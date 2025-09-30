package org.rsmod.api.game.process.npc

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.script.onNpcTimer
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.api.testing.factory.timerTypeFactory
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class NpcTimerProcessorTest {
    @Test
    fun GameTestState.`run two timers with delay after first`() =
        runInjectedGameTest(TimerTestState::class, TimerTestModule, DualTimerTestScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 0, 0), npcTypeFactory.create())

            npc.timer(TIMER_1, cycles = 1)
            npc.timer(TIMER_2, cycles = 1)

            advance(ticks = 1)
            check(npc.isDelayed) { "Npc should be delayed from `TIMER_1`: $npc" }
            assertTrue(it.launchedTimer1)
            assertFalse(it.launchedTimer2)

            // Remove timer, otherwise it will re-run the following cycle.
            npc.clearTimer(TIMER_1)
            advance(ticks = 1)
            check(npc.isNotDelayed) { "Npc should no longer be delayed from `TIMER_1`: $npc" }
            assertTrue(it.launchedTimer2)
        }

    private class DualTimerTestScript @Inject constructor(private val state: TimerTestState) :
        PluginScript() {
        override fun ScriptContext.startup() {
            onNpcTimer(TIMER_1) { timer1() }
            onNpcTimer(TIMER_2) { timer2() }
        }

        private suspend fun StandardNpcAccess.timer1() {
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

    companion object {
        val TIMER_1 = timerTypeFactory.create(id = 1)
        val TIMER_2 = timerTypeFactory.create(id = 2)
    }
}
