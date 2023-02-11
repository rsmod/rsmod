package org.rsmod.game.coroutines.resume

import kotlin.reflect.KClass

public class DeferResumeCondition<T : Any>(public val type: KClass<T>) : ResumeCondition<T> {

    private var value: T? = null

    public fun set(value: T) {
        this.value = value
    }

    override fun resumeOrNull(): T? {
        return value
    }
}
