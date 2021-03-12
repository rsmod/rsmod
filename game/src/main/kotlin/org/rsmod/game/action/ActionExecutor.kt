package org.rsmod.game.action

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
