package org.rsmod.game.coroutines.state

internal class GameCoroutineTimedState(private var ticks: Int) : GameCoroutineState<Unit> {

    override fun resumeOrNull(): Unit? {
        if (--ticks == 0) return Unit
        return null
    }
}
