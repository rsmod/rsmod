package org.rsmod.game.coroutines.state

public interface GameCoroutineState<T> {

    /**
     * @return null if coroutine is suspended. any non-null value should resume
     * the coroutine in question.
     */
    public fun resumeOrNull(): T?
}
