package org.runerealm.coroutine.resume

public interface ResumeCondition<T> {
    public fun resume(): Boolean

    public fun value(): T
}
