package org.rsmod.game.coroutines.resume

public interface ResumeCondition<T> {

    public fun resumeOrNull(): T?
}
