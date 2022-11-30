package org.rsmod.game.coroutines.state

internal class GameCoroutinePredicateState(private var condition: () -> Boolean) : GameCoroutineState<Unit> {

    override fun resumeOrNull(): Unit? {
        if (condition.invoke()) return Unit
        return null
    }
}
