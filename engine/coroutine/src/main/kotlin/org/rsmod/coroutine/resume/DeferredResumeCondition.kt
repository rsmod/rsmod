package org.rsmod.coroutine.resume

import kotlin.reflect.KClass

/*
 * [type] must be a KClass otherwise there are unexpected side effects.
 */
public class DeferredResumeCondition<T : Any>(public val type: KClass<T>) : ResumeCondition<T> {
    private lateinit var value: T
    private var set = false

    override fun resume(): Boolean = set

    override fun value(): T = value

    public fun set(value: T) {
        this.value = value
        this.set = true
    }
}
