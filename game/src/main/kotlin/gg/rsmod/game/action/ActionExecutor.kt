package gg.rsmod.game.action

import com.google.inject.Inject

typealias ActionExecutor<T> = (T).() -> Unit

class ActionMap(
    private val actions: MutableMap<ActionType, ActionExecutorMap>
) : Map<ActionType, ActionExecutorMap> by actions {

    @Inject
    constructor() : this(mutableMapOf())

    fun <T : Action> register(
        type: ActionType,
        id: Long,
        executor: ActionExecutor<T>
    ): Boolean {
        val executors = actions.getOrPut(type) { ActionExecutorMap() }
        return executors.register(id, executor)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Action> publish(
        action: T,
        id: Int,
        type: ActionType
    ) {
        publish(action, id.toLong(), type)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Action> publish(
        action: T,
        id: Long,
        type: ActionType
    ) {
        val executors = actions[type] ?: return
        val executor = executors[id] as? ActionExecutor<T> ?: return
        executor(action)
    }
}

class ActionExecutorMap(
    private val executors: MutableMap<Long, ActionExecutor<*>> = mutableMapOf()
) : Map<Long, ActionExecutor<*>> by executors {

    internal fun <T : Action> register(
        id: Long,
        executor: ActionExecutor<T>
    ): Boolean {
        if (executors.containsKey(id)) {
            return false
        }
        executors[id] = executor
        return true
    }
}
