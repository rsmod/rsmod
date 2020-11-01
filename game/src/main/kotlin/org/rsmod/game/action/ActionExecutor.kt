package org.rsmod.game.action

import com.google.inject.Inject
import kotlin.reflect.KClass

typealias ActionExecutor<T> = (T).() -> Unit

class ActionBus(
    val mappedExecutors: MutableMap<KClass<out Action>, ActionExecutorMap>,
    val singleExecutors: MutableMap<KClass<out Action>, ActionExecutor<*>>
) {
    @Inject
    constructor() : this(mutableMapOf(), mutableMapOf())

    inline fun <reified T : Action> register(id: Long, noinline executor: ActionExecutor<T>): Boolean {
        val executors = mappedExecutors.getOrPut(T::class) { ActionExecutorMap() }
        return executors.register(id, executor)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Action> publish(action: T, id: Long) {
        val executors = mappedExecutors[action::class] ?: return
        val executor = executors[id] as? ActionExecutor<T> ?: return
        executor(action)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Action> publish(action: T, id: Int) = publish(action, id.toLong())

    inline fun <reified T : Action> register(noinline executor: ActionExecutor<T>): Boolean {
        if (singleExecutors.containsKey(T::class)) {
            return false
        }
        singleExecutors[T::class] = executor
        return true
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Action> publish(action: T) {
        val executor = singleExecutors[action::class] as? ActionExecutor<T> ?: return
        executor(action)
    }
}

class ActionExecutorMap(
    val executors: MutableMap<Long, ActionExecutor<*>> = mutableMapOf()
) : Map<Long, ActionExecutor<*>> by executors {

    inline fun <reified T : Action> register(
        id: Long,
        noinline executor: ActionExecutor<T>
    ): Boolean {
        if (executors.containsKey(id)) {
            return false
        }
        executors[id] = executor
        return true
    }
}
