package org.rsmod.game.coroutines.state

import kotlin.reflect.KClass

internal class GameCoroutineDeferValueState<T : Any>(val type: KClass<T>) : GameCoroutineState<T> {

    private var value: T? = null

    fun set(value: T) {
        this.value = value
    }

    override fun resumeOrNull(): T? {
        return value
    }
}
